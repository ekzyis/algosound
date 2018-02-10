/**
 * Quicksort implementation.
 * =============================
 * This class handles the execution of quicksort
 * and notifying to draw new frames.
 *
 * @author ekzyis
 * @date 10 February 2018
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
    // Is this thread marked as paused by the user?
    private boolean paused;
    // Should this thread exit?
    private boolean exiting;
    // Needed for sonification.
    final int FREQ_MIN = 200;
    final int FREQ_MAX = 4000;

    Quicksort(int N)
    {
        //elements = getTestElements();
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
        println("--- quicksort-thread has started.");
        if(s == Sonification.WAVE)
        {
            sendMessage(OSC_STARTAUDIO);
        }
        else if(s == Sonification.SCALE)
        {
            int[] args = {FREQ_MIN, FREQ_MAX};
            sendMessage(OSC_STARTAUDIO, args);
        }
        // Gain access to monitor. If not possible, wait here.
        synchronized(this)
        {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            int lower = 0;
            int upper = a.length-1;
            quicksortVisual(a, lower, upper);
        }
        sendMessage(OSC_FREEAUDIO);
        println("--- quicksort-thread has terminated.");
    }

    // Class member to be able to return the pivot height for visualization.
    int pivot;
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
        pivot = a[pivotIndex];
        Element[] subset = (Element[])(subset(elements,l,r-l+1));
        // Mark pivot element.
        mark(pivotIndex,pivotColor);
        // Mark subset.
        markInSubset(subset);
        // Mark iterator indizes.
        mark(l,compareColor);
        mark(r,compareColor);

        // Sonification.
        int arg1 = expmap(a[l]);
        int arg2 = expmap(a[pivotIndex]);
        int arg3 = expmap(a[r]);
        int[] args = { arg1 };
        sendMessage(OSC_MODAUDIO1, args);
        args[0] = arg2;
        sendMessage(OSC_MODAUDIO2, args);
        args[0] = arg3;
        sendMessage(OSC_MODAUDIO3, args);

        notifyFrameReady();
        do
        {
            while (a[l]<pivot)
            {
                l++;
                mark(pivotIndex,pivotColor);
                markInSubset(subset);
                mark(l,compareColor);
                mark(r,compareColor);

                arg1 = expmap(a[l]);
                args[0] = arg1;
                sendMessage(OSC_MODAUDIO1, args);

                notifyFrameReady();
            }
            while (a[r]>pivot)
            {
                r--;
                mark(pivotIndex,pivotColor);
                markInSubset(subset);
                mark(l,compareColor);
                mark(r,compareColor);

                arg3 = expmap(a[r]);
                args[0] = arg3;
                sendMessage(OSC_MODAUDIO3, args);

                notifyFrameReady();
            }
            if (l<=r)
            {
                int tmp = a[l];
                a[l] = a[r];
                a[r]=tmp;
                elements[l].swap(elements[r], (byte)(Element.VALUES | Element.COLORS));
                mark(pivotIndex,pivotColor);
                markInSubset(subset);
                elements[l].setSwapping(true);
                elements[r].setSwapping(true);
                mark(l,compareColor);
                mark(r,compareColor);

                // Probably no change in sonification after sending of messages.
                arg1 = expmap(a[l]);
                args[0] = arg1;
                sendMessage(OSC_MODAUDIO1, args);
                arg3 = expmap(a[r]);
                args[0] = arg3;
                sendMessage(OSC_MODAUDIO3, args);

                notifyFrameReady();
                l++;
                r--;
            }
        }while(l<=r & !isExiting());
        if (lower<r & !isExiting())
        {
            quicksortVisual(a,lower, r);
        }
        if (l<upper & !isExiting())
        {
            quicksortVisual(a, l, upper);
        }
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

    // Get pivot height.
    int getPivot()
    {
        return pivot;
    }

    // Mark currently accessed elements with given color.
    void mark(int i, color c)
    {
        elements[i].mark(c);
        // Add element to list of elements which get unmarked next frame.
        unmarkMe.add(elements[i]);
    }

    /**
     * Mark elements as being in a subset on which mergesort is currently operating.
     * This also increases level of recursion by one.
     */
    void markInSubset(Element[] e)
    {
        for(Element el : e)
        {
            el.setInSubset(true);
            //el.incrementRecursionLvl();
            unmarkMe.add(el);
        }
    }

    // Clear markers from last frame.
    void clearMarkers()
    {
        for(Element e : unmarkMe)
        {
            e.unmark();
            e.setInSubset(false);
            e.setSwapping(false);
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
