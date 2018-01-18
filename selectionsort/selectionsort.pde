/**
 * Selectionsort implementation.
 * =============================
 * This class handles the execution of selectionsort
 * and notifying to draw new frames.
 *
 * @author ekzyis
 * @date 18 January 2017
 */
class Selectionsort extends Thread
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

    Selectionsort(int N)
    {
        elements = createElements(N);
        a = getValues(elements);
        // First frame is ready before first iteration.
        this.frameReady = true;
        this.frameDrawn = false;
        this.unmarkMe = new ArrayList<Element>();
    }

    @Override
    public void run()
    {
        // Gain access to monitor. If not possible, wait here.
        synchronized(this)
        {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            /**
             * ==================================
             * Start of actual sorting algorithm.
             * ==================================
             */
            int start = 0;
            do
            {
                int minIndex = start;
                for(int i=minIndex+1;i<a.length;++i)
                {
                    if(a[i]<a[minIndex])
                    {
                        minIndex = i;
                    }
                    // Mark element which is getting compared with current smallest element.
                    mark(minIndex);
                    mark(i);
                    notifyFrameReady();
                }
                int tmp = a[minIndex];
                a[minIndex] = a[start];
                a[start] = tmp;
                swap(minIndex,start);
                elements[start].setSorted();
                notifyFrameReady();
                start++;
            }while(start<a.length);
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

    // Notify main thread that new frame is ready.
    void notifyFrameReady()
    {
        frameReady = true;
        // Notify since new frame is ready.
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

    // Native selectionsort implementation.
    void sort(int[] a)
    {
        int start = 0;
        do
        {
            int minIndex = start;
            for(int i=minIndex+1;i<a.length;++i)
            {
                if(a[i]<a[minIndex])
                {
                    minIndex = i;
                }
            }
            int tmp = a[minIndex];
            a[minIndex] = a[start];
            a[start] = tmp;
            start++;
        }while(start<a.length);
    }
}
