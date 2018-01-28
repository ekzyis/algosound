/**
 * Bubblesort implementation.
 * ==========================
 * This class handles the execution of bubblesort
 * and notifying to draw new frames.
 *
 * @author ekzyis
 * @date 28 January 2018
 */
class Bubblesort extends Thread
{
    // Array which should be sorted.
    private int[] a;
    // Elements which have to be swapped according to integers.
    private Element[] elements;
    // Is a new frame ready?
    private boolean frameReady;
    // Has the new frame been drawn?
    private boolean frameDrawn;
    // List of elements to unmark next frame.
    private ArrayList<Element> unmarkMe;

    Bubblesort(int N)
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
            /**
             * ==================================
             * Start of actual sorting algorithm.
             * ==================================
             */
            boolean swap;
            do
            {
                swap = false;
                for(int i=0; i<a.length-1 && !isInterrupted(); ++i)
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
                    // Send osc message for sonification.
                    int[] args = {a[i]};
                    sendMessage(OSC_SWAP,args);
                }
            }while(swap);
            /**
             * Bubblesort keeps iterating through the whole array
             * until not a single time a swap has happened
             * or the thread has been interrupted.
             */
        }
        println("--- bubblesort-thread has terminated.");
    }

    /**
     * Send a message to an osc listener with given path and arguments.
     */
    void sendMessage(String path, int[] args)
    {
        OscMessage msg = new OscMessage(path);
        for(int n : args)
        {
            msg.add(n);
        }
        if(OSC!=null) OSC.send(msg,SUPERCOLLIDER);
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
                // Exception clears the interrupted flag. Reset it to check it later.
                this.interrupt();
                // Set frameDrawn to escape the while-loop.
                frameDrawn = true;
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
