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
 */
private OscP5 osc;
private NetAddress supercollider;
private boolean connected;
/**
 * Global variables.
 * -----------------
 */
 // Width and height of canvas.
final int W=640,H=320;
 // Number of elements to be sorted.
final int N=W/5;
 // Framerate of visualization.
final int FR = 300;
/*
 * -----------------
 **/

// The bubblesort thread.
private Bubblesort sort;

public void settings()
{
    size(W, H);
}

void setup()
{
    // Initialise open sound control for communication with sc3-server.
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
        // Draw status of IPC.
        drawIPCStatus();
        // Draw elements.
        for(Element e : sort.getElements()) e.show();
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
    osc = new OscP5(this, 12000);
    // Initialize address to local sc server
    // SC will listen for messages at port 57120
    supercollider = new NetAddress("127.0.0.1", 57120);
    // Check if server is running with a test message.
    OscMessage msg = new OscMessage("/boot");
    connected = false;
    // send message
    osc.send(msg,supercollider);
}

// Listen for messages.
void oscEvent(OscMessage msg)
{
    println("OSC: message received.");
    if(msg.addrPattern().equals("/helloFromSC")) connected = true;
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
