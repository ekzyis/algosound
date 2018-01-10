/**
 * Bubblesort implementation.
 * ==========================
 * This class handles the execution of bubblesort
 * and notifying to draw new frames.
 * 
 * @author ekzyis
 * @date 10 January 2018
 */
class Bubblesort extends Thread
{
    // Array which should be sorted
    private int[] a;
    // Elements which have to be swapped according to integers.
    private Element[] elements;
    // Is a new frame ready?
    private boolean frameReady;
    // Has the new frame been drawn?
    private boolean frameDrawn;
    // Object for synchronization of threads.
    private Object lock;
    // List of elements to unmark next frame.
    private ArrayList<Element> unmarkMe;

    Bubblesort(int[] _a, Object _lock, Element[] _elements)
    {
        this.a = _a;
        this.lock = _lock;
        this.elements = _elements;
        // First frame is ready before first iteration.
        frameReady = true;
        frameDrawn = false;
        unmarkMe = new ArrayList<Element>();
    }

    @Override
    public void run()
    {
        // Gain access to monitor. If not possible, wait here.
        synchronized(lock)
        {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            /** 
             * ==================================
             * Start of actual sorting algorithm.
             * ==================================
             */
            boolean swap;
            do
            {
                swap = false;
                for(int i=0; i<a.length-1; ++i)
                {
                    // They are in false order. Swap them.
                    if(a[i]>a[i+1])
                    {
                        int tmp = a[i+1];
                        a[i+1] = a[i];
                        a[i] = tmp;
                        swap = true;
                        // Elements need to swap their x-position to ensure visualization.
                        swap(i,i+1);
                    }
                    // Mark elements accessed by bubblesort.
                    mark(i);
                    mark(i+1);
                    notifyFrameReady();
                }
            }while(swap);
            /** 
             * Bubblesort keeps iterating through the whole array 
             * until not a single time a swap has happened.
             */
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
        lock.notify();
        while(!frameIsDrawn())
        {
            try
            {
                lock.wait();
            }
            catch(InterruptedException e)
            {
            }
        }
        // Clean markers from last frame.
        clearMarkers();
        frameDrawn = false;
    }

    // Notify thread that new frame has been drawn.
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

    // Swap element at given index with neighbour to ensure visualization.
    void swap(int i, int j)
    {
        /**
         * Elements need to swap their x-position AND their position in the array!
         * Otherwise, next iteration of the for-loop would cause severe bugs since
         * Bubblesort swaps the integers in the array (= change their index)
         * and assumes the corresponding element is at the same index in the 
         * elements array.
         */
        elements[i].swap(elements[j],Element.COORDINATES);
        Element tmp = elements[i];
        elements[i] = elements[j];
        elements[j] = tmp;
    }

    // Mark currently accessed elements.
    void mark(int i)
    {
        elements[i].mark();
        // Add those elements to list of elements which get unmarked next frame.
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

    // Native bubblesort implementation.
    void sort(int[] a)
    {
        boolean swap;
        do
        {
            swap = false;
            for(int i=0; i<a.length-1; ++i)
            {
                // They are in false order. Swap them.
                if(a[i]>a[i+1])
                {
                    int tmp = a[i+1];
                    a[i+1] = a[i];
                    a[i] = tmp;
                    swap = true;
                }
            }
        }while(swap);
    }
}