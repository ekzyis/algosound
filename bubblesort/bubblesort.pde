/**
 * Bubblesort implementation.
 * ==========================
 * This class handles the execution of bubblesort
 * and notifying to draw new frames.
 * 
 * @author ekzyis
 * @date 09 January 2018
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
            // New frame needs to be calculated. Therefore, it has not been drawn yet.
            frameDrawn = false;
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
                        swap(i);
                    }
                    // Mark elements accessed by bubblesort.
                    mark(i);
                    frameReady = true;
                    // Notify since new frame is ready.
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
    void swap(int index)
    {
        /**
         * Elements need to swap their x-position AND their position in the array!
         * Otherwise, next iteration of the for-loop would cause severe bugs since
         * Bubblesort swaps the integers in the array (= change their index)
         * and assumes the corresponding element is at the same index in the 
         * elements array.
         */
        elements[index].swap(elements[index+1],Element.COORDINATES);
        Element tmp = elements[index];
        elements[index] = elements[index+1];
        elements[index+1] = tmp;
    }

    // Mark currently accessed elements.
    void mark(int index)
    {
        elements[index].mark();
        elements[index+1].mark();
        // Add those elements to list of elements which get unmarked next frame.
        unmarkMe.add(elements[index]);
        unmarkMe.add(elements[index+1]);
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
}