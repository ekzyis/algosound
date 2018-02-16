/*
* @Author: ekzyis
* @Date:   28-12-2017 20:11:17
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:05:21
*/
/**
 * Selectionsort implementation.
 * =============================
 * This class handles the execution of selectionsort
 * and notifying to draw new frames.
 *
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
    // Is this thread marked as paused by the user?
    private boolean paused;
    // Should this thread exit?
    private boolean exiting;
    // Needed for sonification.
    final int FREQ_MIN = 200;
    final int FREQ_MAX = 4000;

    Selectionsort(int N)
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
        println("--- selectionsort-thread has started.");
        if( s == Sonification.WAVE)
        {
            sendMessage(OSC_STARTAUDIO);
        }
        else if( s == Sonification.SCALE)
        {
            int[] args = {FREQ_MIN,FREQ_MAX};
            sendMessage(OSC_STARTAUDIO,args);
        }
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
                for(int i=minIndex+1;i<a.length & !isInterrupted();++i)
                {
                    int arg1 = expmap(a[i]);
                    if(a[i]<a[minIndex])
                    {
                        minIndex = i;
                        int arg2 = expmap(a[minIndex]);
                        int[] args = {arg2};
                        sendMessage(OSC_MODAUDIO2, args);
                    }
                    // Mark element which is getting compared with current smallest element.
                    mark(minIndex);
                    mark(i);
                    int[] args = {arg1};
                    sendMessage(OSC_MODAUDIO1, args);
                    notifyFrameReady();
                }
                int tmp = a[minIndex];
                a[minIndex] = a[start];
                a[start] = tmp;
                swap(minIndex,start);
                elements[start].setSorted();
                notifyFrameReady();
                start++;
            }while(start<a.length & !isInterrupted());
        }
        sendMessage(OSC_FREEAUDIO);
        println("--- selectionsort-thread has terminated.");
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
            sendMessage(OSC_PAUSEAUDIO);
            try
            {
                this.wait();
            }
            catch(InterruptedException e)
            {
                // Exception clears the interrupted flag. Reset it to check it later.
                this.interrupt();
            }
            sendMessage(OSC_RESUMEAUDIO);
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

    /**
     * Exponential map function: f(x) = a*e^(b*x)
     * This function must satisfy following two equations:
     * f(x1) = y1, f(x2) = y2
     * Rearrangment of equations leads to following solution ==>
     * b = ln(y2/y1)/(x2-x1)
     * a = y2/( e^(b*x2) ) = y1/( e^(b*x1) )
     */
    int expmap(int value, int x1, int x2, int y1, int y2)
    {
        float b = log(y2/y1)/(x2-x1);
        float a = y2/(exp(b*x2));
        return (int)(a*exp(value*b));
    }
    // Convenience method
    int expmap(int value)
    {
        return expmap(value,0,H,FREQ_MIN,FREQ_MAX);
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
