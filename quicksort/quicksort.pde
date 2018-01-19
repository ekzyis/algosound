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
    // Color of pivot element
    private color pivotColor = color(255,0,0);
    // Color of elements which are getting compared.
    private color compareColor = color(0,255,0);
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
            int lower = 0;
            int upper = a.length-1;
            quicksortVisual(a, lower, upper);
        }
    }

    void quicksortVisual(int[] a, int lower, int upper)
    {
        // This indizes will iterate over the set and find elements to swap.
        int l = lower;
        int r = upper;
        int pivotIndex = (int)((l+r)/2);
        /**
         * After one do-loop, the element at the pivot index will be inserted
         * where the iterating indizes l and r met. The pivot element will be sorted then
         * since all elements left to it will be smaller and right to it larger than itself.
         */
        int pivot = a[pivotIndex];
        mark(pivotIndex,pivotColor);
        mark(l,compareColor);
        mark(r,compareColor);
        notifyFrameReady();
        do
        {
            while (a[l]<pivot)
            {
                l++;
                mark(pivotIndex,pivotColor);
                mark(l,compareColor);
                mark(r,compareColor);
                notifyFrameReady();
            }
            while (a[r]>pivot)
            {
                r--;
                mark(pivotIndex,pivotColor);
                mark(l,compareColor);
                mark(r,compareColor);
                notifyFrameReady();
            }
            if (l<=r)
            {
                int tmp = a[l];
                a[l] = a[r];
                a[r]=tmp;
                elements[l].swap(elements[r], (byte)(Element.VALUES | Element.COLORS));
                l++;
                r--;
            }
        }while(l<=r);
        if (lower<r)
        {
            quicksortVisual(a,lower, r);
        }
        if (l<upper)
        {
            quicksortVisual(a, l, upper);
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

    // Mark currently accessed elements with given color.
    void mark(int i, color c)
    {
        elements[i].mark(c);
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
