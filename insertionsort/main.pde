/**
 * Mainfile of insertionsort visualization.
 * =====================================
 * This sketch produces a visualization of insertionsort
 * by creating a "insertionsort-thread" which periodically notifies
 * the draw function when a new frame has been calculated.
 *
 * @author ekzyis
 * @date 18 January 2018
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
    // Initialize insertionsort thread.
    sort = new Insertionsort(N);
    // Assert that implementation is sorting correctly.
    int[] test = getRndArr(N);
    sort.sort(test);
    assert(isSorted(test));
    // Start insertionsort thread.
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
        // Notify sorting thread that frame has been drawn.
        sort.notifyFrameDraw();
        sort.notify();
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
