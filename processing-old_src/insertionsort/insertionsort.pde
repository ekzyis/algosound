/*
* @Author: ekzyis
* @Date:   28-12-2017 02:37:58
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:01:00
*/
/**
 * Insertionsort implementation.
 * =============================
 * This class handles the execution of insertionsort
 * and notifying to draw new frames.
 */
class Insertionsort extends Thread
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
    final int FREQ_MAX = 4000;

    Insertionsort(int N)
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
        println("---insertionsort-thread starting.");
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
            // First frame of insertionsort consists only of marking first element as sorted.
            elements[0].setSorted();
            notifyFrameReady();
            /**
             * ==================================
             * Start of actual sorting algorithm.
             * ==================================
             */
            /**
             * Starts with second element because insertionsort
             * assumes the first element as sorted.
             * (If you only have one element, it can not not be sorted.)
             */
            for(int i=1;i<a.length && !isInterrupted();++i)
            {
                /**
                 * All elements to the left of current element are sorted.
                 * To visualize this, everytime the left element will be marked as sorted.
                 * This leads to all element to the left of the current element being sorted.
                 */
                elements[i-1].setSorted();
                // Save value of element to insert.
                int value = a[i];
                // Also save color of visual element to insert.
                color insertColor = elements[i].getColor();
                // Also save index of that element.
                int j = i;
                /**
                 * Iterate through the array to the left until
                 * correct place to insert element is found.
                 */
                while(j>0 && a[j-1]>value && !isInterrupted())
                {
                    /**
                     * The integers don't swap places;
                     * the left element moves to the right
                     * overwriting the previous element on their right
                     * until the right place is found for current element.
                     */
                    a[j] = a[j-1];
                    // Also move visual elements to the right.
                    moveRight(j-1);
                    mark(j-1);
                    j = j-1;
                    // Notify since new frame is ready.
                    notifyFrameReady();
                    println("values to map: "+a[j]+", "+value);
                    int[] args = { expmap(a[j]), expmap(value) };
                    println("mapped values: "+args[0]+", "+args[1]);
                    sendMessage(OSC_MODAUDIO, args);
                }
                // Place to insert has been found!
                a[j] = value;
                // Visualize inserting by modified element to look like the element which should be inserted.
                elements[j].setValue(value);
                elements[j].setColor(insertColor);
                notifyFrameReady();
            }
        }
        sendMessage(OSC_FREEAUDIO);
        println("--- insertionsort-thread has terminated.");
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
            println("---insertionsort-thread pausing.");
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
            println("---insertionsort-thread resuming.");
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

    /**
     * Copies element one step to the right, overriding the previous element.
     * On its previous place, there is still the same element.
     */
    void moveRight(int i)
    {
        /**
         * Change the right element to look like the left element
         * to visualize overriding.
         */
        elements[i+1].setValue(elements[i].getValue());
        elements[i+1].setColor(elements[i].getColor());
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

    // Native insertionsort implementation.
    void sort(int[] a)
    {
        for(int i=1;i<a.length;++i)
        {
            int value = a[i];
            int j = i;
            while(j>0 && a[j-1]>value)
            {
                a[j] = a[j-1];
                j = j-1;
            }
            a[j] = value;
        }
    }
}
