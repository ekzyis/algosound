/**
 * Mainfile of quicksort visualization.
 * =====================================
 * This sketch produces a visualization of quicksort
 * by creating a "quicksort-thread" which periodically notifies
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
final int FR = 30;
/*
 * -----------------
 **/

// Font of frame text.
private PFont font;
// The quicksort thread.
private Quicksort sort;
// Width of one element.
int elementwidth = W/N;

public void settings()
{
    size(W+elementwidth, H);
}

void setup()
{
    // Define frame rate.
    frameRate(FR);
    // Initialize quicksort thread.
    sort = new Quicksort(N);
    // Assert that implementation is sorting correctly.
    int[] test = getRndArr(N);
    sort.quicksort(test);
    assert(isSorted(test));
    // Set font for drawing later.
    font = createFont("Courier",12,true);
    // Start quicksort thread.
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
        translate(elementwidth,0);
        for(Element e : sort.getElements()) e.show();
        translate(-elementwidth,0);
        // Draw pivot line.
        fill(255);
        rect(0,H,elementwidth,-sort.getPivot());
        text("Pivot",0,H-sort.getPivot()-2);
        fill(255);
        // Notify sorting thread that frame has been drawn.
        sort.notifyFrameDraw();
        sort.notify();
        //noLoop();
    }
}
void mousePressed()
{
    loop();
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
