/**
 * Bubblesort implementation.
 * ==========================
 * This class handles the execution of bubblesort
 * and notifying to draw new frames.
 *
 * @author ekzyis
 * @date 31 January 2018
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
    // Is this thread marked as paused by the user?
    private boolean paused;
    // Should this thread exit?
    private boolean exiting;
    // Needed for sonification.
    final int FREQ_MIN = 200;
    final int FREQ_MAX = 1640;

    Bubblesort(int N)
    {
        elements = createElements(N);
        a = getValues(elements);
        // First frame is ready before first iteration.
        frameReady = true;
        frameDrawn = false;
        unmarkMe = new ArrayList<Element>();
        paused = false;
        exiting = false;
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
                /**
                 * Notice that thread will terminate when interrupted-flag
                 * is (still) set when the boolean expression is evaluated.
                 */
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
                    int[] args = {map(a[i],0,H,FREQ_MIN,FREQ_MAX),map(a[i+1],0,H,FREQ_MIN,FREQ_MAX),FREQ_MIN,FREQ_MAX};
                    sendMessage(OSC_MODAUDIO,args);
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

    // Overriding map function since old function didn't give expected results.
    int map(int value, int s1, int e1, int s2, int e2)
    {
        // Available amount of target numbers
        int targets = e2-s2;
        // Amount of input numbers
        int n = e1-s1;
        // Space between mapped input numbers.
        int dFreq = targets/n;
        int res = s2 + value*dFreq;
        return res;
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
        /**
         * Boolean expressions make sure that thread will only wait in this loop
         * if it is actually waiting for a new frame. If thread is paused,
         * it will wait in the next loop. If it's exiting, it will not wait but exit.
         */
        while(!frameIsDrawn() && !isPaused() && !isExiting())
        {
            try
            {
                this.wait();
            }
            catch(InterruptedException e)
            {
                // Exception clears the interrupted flag. Reset it to check it later.
                this.interrupt();
            }
        }
        /**
         * Check if this frame the user did press the pause button. If yes and thread is not exiting,
         * the thread will pause until the user wants to resume.
         */
        while(isPaused() && !isExiting())
        {
            try
            {
                this.wait();
            }
            catch(InterruptedException e)
            {
                // Exception clears the interrupted flag. Reset it to check it later.
                this.interrupt();
            }
        }
        // Clean markers from last frame.
        clearMarkers();
        frameDrawn = false;

        /**
         * If thread is exiting, the interrupt-flag will be still set at this point.
         * This causes to escape from the for-loop, set swap to false, and then leave the do-while-loop
         * and finally terminate this thread.
         * If thread was paused or waiting for a new frame and then a InterruptedException happens,
         * the flag will be reset but the thread will continue to wait for a resume or a new frame since the
         * wait()-statement is in a while-loop. This following wait() unsets the interrupted-flag
         * so the thread will not exit when interrupted while pausing or waiting for a new frame.
         */
    }

    boolean isPaused()
    {
        return paused;
    }
    void pause()
    {
        this.paused = true;
        // Notify thread so it will always wait in the correct expected loop.
        synchronized(this)
        {
            this.notify();
        }
    }
    void unpause()
    {
        this.paused = false;
        // Notify thread since it should no longer be paused.
        synchronized(this)
        {
            this.notify();
        }
    }

    boolean isExiting()
    {
        return exiting;
    }
    // Set the exit-flag and interrupt the thread; waking it up from eventual waiting.
    void exit()
    {
        this.exiting = true;
        // This wakes thread up from waiting, making it able to exit.
        this.interrupt();
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
