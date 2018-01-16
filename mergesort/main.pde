/**
 * Mainfile of mergesort visualization.
 * ====================================
 * This sketch produces a visualization of mergesort
 * by creating a "mergesort-thread" which periodically notifies
 * the draw function when a new frame has been calculated.
 *
 * @author ekzyis
 * @date 16 January 2018
 */

/**
 * Global variables.
 * -----------------
 */
// Width and height of canvas.
// To height the maximal possible height increase due to recursion of one element is added.
final int W=640,H=320+(int)(Math.log(640/5)/Math.log(2))*20;
// Number of elements to be sorted.
final int N=W/5;
// Framerate of visualization.
final int FR = 30;
/*
 * -----------------
 **/

// The elements to sort.
private Element[] elements;
// Integer array representation of the elements values.
private int[] a;
// Object for synchronization of algorithm and visualization.
private Object lock;
// The mergesort thread.
private Mergesort sort;

public void settings()
{
    size(W, H);
}

void setup()
{
    // Define frame rate.
    frameRate(FR);
    // Initialize elements.
    //elements = getTestElements(getColors());
    elements = getElements(getColors());
    // Initialize integer array.
    a = getValues(elements);
    // Initialize lock object.
    lock = new Object();
    // Initialize mergesort thread.
    sort = new Mergesort(a,lock,elements);
    // Assert that implementation is sorting correctly.
    int[] test = getRndArr(N);
    test = sort.mergesort(test,Mergesort.NATIVE);
    assert(isSorted(test));
    // Start mergesort thread.
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
