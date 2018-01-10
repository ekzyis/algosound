/**
 * Mainfile of insertionsort visualization.
 * =====================================
 * This sketch produces a visualization of insertionsort
 * by creating a "insertionsort-thread" which periodically notifies
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
// The insertionsort thread.
private Insertionsort sort;

public void settings() 
{
    size(W, H);
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
    // Initialize insertionsort thread.
    sort = new Insertionsort(a,lock,elements);
    // Start insertionsort thread.
    sort.start();
}

void draw()
{
    synchronized(lock)
    {
        background(25);
        // Wait until new frame is ready.
        while(!sort.frameIsReady())
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
        for(Element e : sort.getElements()) e.show();
        // Notify sorting thread that frame has been drawn.
        sort.notifyFrameDraw();
        lock.notify();
    }
}