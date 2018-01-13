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
private Bubblesort sort;

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
    // Initialize bubblesort thread.
    sort = new Bubblesort(a,lock,elements);
    // Assert that implementation is sorting correctly.
    int[] test = getRndArr(N);
    sort.sort(test);
    assert(isSorted(test));
    // Start bubblesort thread.
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
        // Notify bubblesort thread that frame has been drawn.
        sort.notifyFrameDraw();
        lock.notify();
    }
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