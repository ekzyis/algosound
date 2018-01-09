/**
 * Mainfile of bubblesort visualization.
 * =====================================
 * This sketch produces a visualization of bubblesort
 * by creating a "bubblesort-thread" which periodically notifies
 * the draw function when a new frame has been calculated. 
 *
 * @author ekzyis
 * @date 09 January 2018
 */

/**
 * Global variables.
 * -----------------
 */
 // Width and height of canvas.
final int W=640,H=320;
 // Number of elements to be sorted.
final int N=W/5;
 // Framerate of visualization.
final int FR = 120;
/*
 * -----------------
 **/

// The elements to sort.
private Element[] elements;
// Integer array representation of the elements values.
private int[] a;
// Object for synchronization of algorithm and visualization.
private Object lock;
// The bubblesort thread.
private Bubblesort t;

public void settings() 
{
    size(640, 320);
}

void setup() 
{
    // Define frame rate.
    frameRate(FR);
    // Initialize elements.
    //elements = getElements(getColors());
    elements = getElements(getColors());
    // Initialize integer array.
    a = getValues(elements);
    // Initialize lock object.
    lock = new Object();
    // Initialize bubblesort thread.
    t = new Bubblesort(a,lock,elements);
    // Start bubblesort thread.
    t.start();
}

void draw()
{
    synchronized(lock)
    {
        background(25);
        // Wait until new frame is ready.
        while(!t.frameIsReady())
        {
            try
            {
                lock.wait();
            }
            catch(InterruptedException e)
            {
            }
        }
        // Draw elements.
        for(Element e : t.getElements()) e.show();
        // Notify bubblesort thread that frame has been drawn.
        t.notifyFrameDraw();
        lock.notify();
    }
}