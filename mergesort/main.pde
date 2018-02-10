/**
 * Mainfile of mergesort visualization.
 * ====================================
 * This sketch produces a visualization of mergesort
 * by creating a "mergesort-thread" which periodically notifies
 * the draw function when a new frame has been calculated.
 *
 * @author ekzyis
 * @date 10 February 2018
 */
import netP5.*;
import oscP5.*;
import supercollider.*;
import controlP5.*;
/**
 * Members needed for sonification.
 * --------------------------------
 */
// Choose sonification variant.
private final Sonification s = Sonification.WAVE;
// Open sound control instance.
private OscP5 OSC;
// Address of sc3-server.
private NetAddress SUPERCOLLIDER;
// Status of connection.
private boolean connected;
// Osc address of status listener.
private final String OSC_STATUS = s.STATUSPATH;
// SuperCollider status reply.
private final String SC_REPLY = "/hello";
// Osc address of boot listener.
private final String OSC_BOOT = "/boot";
// Osc address of audio listeners
private final String OSC_STARTAUDIO = s.STARTPATH;
private final String OSC_PAUSEAUDIO = s.PAUSEPATH;
private final String OSC_RESUMEAUDIO = s.RESUMEPATH;
private final String OSC_MODAUDIO = s.MODPATH;
private final String OSC_FREEAUDIO = s.FREEPATH;
/**
 * Port on which sc3-server is listening for messages.
 * This should match the output of NetAddr.localAddr in SuperCollider.
 */
final private int SC_PORT = 57120;
// Port on which OSC should listen for messages.
final private int OSC_PORT = 12000;
/*
 * --------------------------------
 **/
// Width and height of canvas.
final int W=640,H=320;
// Number of elements to be sorted.
final int N=W/5;
// Framerate of visualization.
final int FR = 60;

// 'GUI-instance'
private ControlP5 cp5;
// Width of GUI
final int GUI_W=70;
// Buttons.
private Button start;
private Button exit;
private Button reset;
// The mergesort thread.
private Mergesort sort;
// IPC-status-thread.
private Thread status;

public void settings()
{
    size(W+GUI_W, H);
}

void setup()
{
    // Initialize open sound control for communication with sc3-server.
    thread("initOSC");
    // Define frame rate.
    frameRate(FR);
    // Initialize the graphical user interface.
    cp5 = new ControlP5(this);
    initGUI();
    // Initialize mergesort thread.
    sort = new Mergesort(N);
    // Assert that implementation is sorting correctly.
    int[] test = getRndArr(N);
    test = sort.mergesort(test,Mergesort.NATIVE);
    assert(isSorted(test));
}

/**
 * Initialize user interface which consists of buttons at the right side.
 */
void initGUI()
{
    // Create the y-coordinates for the buttons and save them in an array.
    Button.autoWidth = 50;
    Button.autoHeight = 20;
    int yInset = 10;
    int len = (int)(H/(yInset+Button.autoHeight));
    int[] yPos = new int[len];
    int y0 = yInset+Button.autoHeight;
    int x0 = W+10;
    for(int i=0;i<len;++i)
    {
        yPos[i] = (i+1)*y0 - Button.autoHeight;
    }
    start = cp5.addButton("start/pause").setPosition(x0,yPos[0]).setLabel("Start");
    /**
     * Naming the button like the exit()-function triggers the function when pressing
     * thus no need of defining a if-Statement for this button in controlEvent().
     */
    exit = cp5.addButton("exit").setPosition(x0,yPos[len-1]).setLabel("Exit");
    reset = cp5.addButton("reset").setPosition(x0,yPos[1]).setLabel("Reset");
}

/**
 * Eventhandling of user interface.
 * TODO:
 * ---Bugfix#2
 *      When immediately pressing a button after opening the sketch,
 *      a InvocationTargetException is thrown but sketch keeps running fine after that.
 */
void controlEvent(ControlEvent event)
{
    Controller c = event.getController();
    if(c==start)
    {
        String currentLabel = c.getLabel();
        // Do action corresponding to current label.
        if(currentLabel.equals("Start"))
        {
            /**
             * Did thread already start? If not, start it.
             * (Execution never reaches this statement when it would be false since
             * the label will never be again "Start" so it's actually unnecessary.)
             */
            if(!sort.isAlive())
            {
                //println("---starting audio");
                sort.start();
            }
            c.setLabel("Pause");
        }
        else if(currentLabel.equals("Pause"))
        {
            //println("---pause audio");
            sort.pause();
            c.setLabel("Resume");
        }
        else if(currentLabel.equals("Resume"))
        {
            //println("---resume audio");
            sort.unpause();
            c.setLabel("Pause");
        }
    }
    else if(c==reset)
    {
        start.setLabel("Start");
        sendMessage(OSC_FREEAUDIO);
        sort = new Mergesort(N);
    }
}

void draw()
{
    synchronized(sort)
    {
        background(25);
        // Has sorting thread started and is not paused nor exiting? If not, no need for waiting.
        if(sort.isAlive() && !sort.isPaused() && !sort.isExiting())
        {
            // Wait until new frame is ready.
            while(!sort.frameIsReady())
            {
                try
                {
                    sort.wait();
                }
                catch(InterruptedException e) {}
            }
        }
        // Draw elements.
        for(Element e : sort.getElements()) e.show();
        // Draw status of IPC.
        drawIPCStatus();
        /**
         * Notify mergesort thread that frame has been drawn.
         * (notify() will just do nothing if thread did not start yet.)
         */
        if(sort.isAlive() && !sort.isPaused())
        {
            sort.notifyFrameDraw();
            sort.notify();
        }
    }
}

// Draw status of IPC.
void drawIPCStatus()
{
    ellipseMode(CENTER);
    noStroke();
    fill(255);
    ellipse(10,10,10,10);
    if(connected)
    {
        fill(0,255,0);
    }
    else
    {
        fill(255,0,0);
    }
    text("SC3-server",20,15);
    ellipse(10,10,8,8);
    stroke(0);
}

// This function is called during exit.
void exit()
{
    // Exit the sorting thread using its own implemented exit-method.
    sort.exit();
    /**
     * Interrupt status-thread which checks connection between OSC and sc3-server.
     * This will terminate the status-thread in a clean way.
     * TODO: Set status-thread as daemon thread so JVM will exit even when status is still running.
     * UPDATE: If thread should be terminated by JVM when no other none-daemon-threads are running,
     * a NullPointer because of the OSC-instance is thrown. This leads to a decision between
     * using daemon thread convenience but needing a if-statement (osc!=null) in status-thread
     * or interrupting and waiting for termination.
     * STATUS: not decided yet.
     */
    status.interrupt();
    try
    {
        // Wait for threads to terminate.
        sort.join();
        status.join();
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    // Close OSC after execution to prevent blocking of OSC_PORT.
    OSC.dispose();
    // Call exit() of PApplet to properly exit this sketch.
    super.exit();
}

// Return a random integer array of size n.
int[] getRndArr(int n)
{
    int[] ret = new int[n];
    for(int i=0;i<n;++i)
    {
        ret[i] = (int)(Math.random()*H);
    }
    return ret;
}

// Check if given array is in ascending order.
boolean isSorted(int[] a)
{
    for(int i=0;i<a.length-1;++i)
    {
        if(a[i]>a[i+1]) return false;
    }
    return true;
}

void printarr(int[] a)
{
    print("{");
    for(int i=0;i<a.length-1;++i)
    {
        print(a[i] + ", ");
    }
    println(a[a.length-1]+"}");
}
