/**
 * Quicksort implementation.
 * =============================
 * This class handles the execution of quicksort
 * and notifying to draw new frames.
 *
 * @author ekzyis
 * @date 18 January 2018
 */
class Quicksort extends Thread
{
    // Array which should be sorted
    private int[] a;
    // Elements which have to be swapped according to integers.
    private Element[] elements;
    // Is a new frame ready?
    private boolean frameReady;
    // Has the new frame been drawn?
    private boolean frameDrawn;
    // List of elements to unmark next frame.
    private ArrayList<Element> unmarkMe;

    Quicksort(int N)
    {
        elements = createElements(N);
        a = getValues(elements);
        // First frame is ready before first iteration.
        frameReady = true;
        frameDrawn = false;
        unmarkMe = new ArrayList<Element>();
    }

    @Override
    public void run()
    {
        // Gain access to monitor. If not possible, wait here.
        synchronized(this)
        {
            // Wait until first frame has been drawn.
            notifyFrameReady();
        }
    }

    boolean frameIsReady()
    {
        return frameReady;
    }

    boolean frameIsDrawn()
    {
        return frameDrawn;
    }

    // Notify thread waiting for lock that new frame is ready.
    void notifyFrameReady()
    {
        frameReady = true;
        this.notify();
        while(!frameIsDrawn())
        {
            try
            {
                this.wait();
            }
            catch(InterruptedException e)
            {
            }
        }
        // Clean markers from last frame.
        clearMarkers();
        frameDrawn = false;
    }

    // Notify this thread that new frame has been drawn.
    void notifyFrameDraw()
    {
        frameDrawn = true;
        // New frame has just been drawn. Next frame is not ready yet.
        frameReady = false;
    }

    // Return updated elements.
    Element[] getElements()
    {
        return elements;
    }

    // Mark currently accessed elements.
    void mark(int i)
    {
        elements[i].mark();
        // Add element to list of elements which get unmarked next frame.
        unmarkMe.add(elements[i]);
    }

    // Clear markers from last frame.
    void clearMarkers()
    {
        for(Element e : unmarkMe)
        {
            e.unmark();
        }
        // remove all elements from list since a new frame will begin now.
        unmarkMe.clear();
    }

    // Native quicksort implementation.
    void quicksort(int a[])
    {
        int lower = 0;
        int upper = a.length-1;
        quicksort(a, lower, upper);
    }
    void quicksort(int a[], int lower, int upper)
    {
        int l = lower;
        int r = upper;
        int pivot = a[(int)((l + r)/2)];
        do
        {
            while (a[l]<pivot)
            {
                l++;
            }
            while (a[r]>pivot)
            {
                r--;
            }
            if (l<=r)
            {
                int tmp = a[l];
                a[l] = a[r];
                a[r]=tmp;
                l++;
                r--;
            }
        }while(l<=r);
        if (lower<r)
        {
            quicksort(a,lower, r);
        }
        if (l<upper)
        {
            quicksort(a, l, upper);
        }
    }
}
