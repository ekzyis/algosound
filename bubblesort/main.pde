/**
 * Mainfile of bubblesort visualization.
 * =====================================
 * This sketch produces a visualization of bubblesort
 * by creating a "bubblesort-thread" which periodically notifies
 * the draw function when a new frame has been calculated.
 *
 * @author ekzyis
 * @date 25 January 2018
 */
import netP5.*;
import oscP5.*;
import supercollider.*;
/**
 * Members needed for sonification.
 * --------------------------------
 */
// Open sound control instance.
private OscP5 OSC;
// Address of sc3-server.
private NetAddress SUPERCOLLIDER;
// Status of connection.
private boolean connected;
// Osc address of status listener.
private final String OSC_STATUS = "/status";
// SuperCollider status reply.
private final String SC_REPLY = "/hello";
// Osc address of boot listener.
private final String OSC_BOOT = "/boot";
// Osc address of swap listener.
private final String OSC_SWAP = "/swap";
/** Port on which sc3-server is listening for messages.
 * This should match the output of NetAddr.localAddr in SuperCollider. */
final private int SC_PORT = 57120;
// Port on which OSC should listen for messages.
final private int OSC_PORT = 12000;
/*
 * --------------------------------
 **/
/**
 * Global variables.
 * -----------------
 */
 // Width and height of canvas.
final int W=640,H=320;
 // Number of elements to be sorted.
final int N=W/5;
 // Framerate of visualization.
final int FR = 60;
/*
 * -----------------
 **/

// The bubblesort thread.
private Bubblesort sort;
// IPC-status-thread.
private Thread status;
public void settings()
{
    size(W, H);
}

void setup()
{
    // Initialize open sound control for communication with sc3-server.
    thread("initOSC");
    // Define frame rate.
    frameRate(FR);
    // Initialize bubblesort thread.
    sort = new Bubblesort(N);
    // Assert that implementation is sorting correctly.
    int[] test = getRndArr(N);
    sort.sort(test);
    assert(isSorted(test));
    // Start bubblesort thread.
    sort.start();
}

void draw()
{
    synchronized(sort)
    {
        background(25);
        // Wait until new frame is ready.
        while(!sort.frameIsReady())
        {
            try
            {
                sort.wait();
            }
            catch(InterruptedException e)
            {
            }
        }
        // Draw elements.
        for(Element e : sort.getElements()) e.show();
        // Draw status of IPC.
        drawIPCStatus();
        // Notify bubblesort thread that frame has been drawn.
        sort.notifyFrameDraw();
        sort.notify();
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

// Initialise open sound control for communication with sc3-server.
void initOSC()
{
    OSC = new OscP5(this, OSC_PORT);
    // Initialize address to local sc server
    SUPERCOLLIDER = new NetAddress("127.0.0.1", SC_PORT);
    // Check if server is running with a status message.
    connected = false;
    OSC.send(new OscMessage(OSC_STATUS),SUPERCOLLIDER);
    // Send boot message.
    OSC.send(new OscMessage(OSC_BOOT),SUPERCOLLIDER);
    // Start a thread which periodically checks if sc3-server is still running.
    status = new Thread()
    {
        @Override
        public void run()
        {
            while(!isInterrupted())
            {
                /**
                 * TODO: This implementation depends on order of execution.
                 * If 1. connected gets set to false, 2. a frame gets drawn,
                 * the ICP status is marked as lost in the frame even though
                 * connection may not be lost.
                 * -> Find a way without setting connected to false before checking
                 * the sc3-server.
                 * (Another 'connected' variable could work, but would be messy?)
                 */
                connected = false;
                OSC.send(new OscMessage(OSC_STATUS),SUPERCOLLIDER);
                try
                {
                    sleep(1000);
                }
                catch(InterruptedException e)
                {
                    // Exception clears the interrupted flag. Reset it.
                    this.interrupt();
                }
            }
            println("--- checkSC3Status-thread has terminated.");
        }
    };
    status.start();
}

// Listen for messages.
void oscEvent(OscMessage msg)
{
    // SC3 will send SC_REPLY-message if OSC did send OSC_STATUS-message.
    if(msg.checkAddrPattern(SC_REPLY)) connected = true;
}

// This function is called during exit.
void exit()
{
    // Interrupt the sorting thread causing it to terminate properly.
    sort.interrupt();
    // Interrupt thread which checks connection between OSC and sc3-server.
    status.interrupt();
    try
    {
        // Wait for threads to terminate.
        sort.join();
        status.join();
    }
    catch(Exception e) {}
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
