import java.util.Stack;
import java.util.ArrayDeque;

/**
 * Mergesort implementation.
 * =========================
 * This class handles the execution of mergesort
 * and notifying to draw new frames.
 * For more information see comments in this file.
 *
 * @author ekzyis
 * @date 06 February 2018
 */

class Mergesort extends Thread
{
    // Variables to pass mergesort() to determine mode.
    final static byte NATIVE = 1;
    final static byte THREAD = 2;
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
    // Keep track of stack of the cut indizes while sorting for proper visualization.
    private Stack<Integer> cutStack;
    // Is this thread marked as paused by the user?
    private boolean paused;
    // Should this thread exit?
    private boolean exiting;
    // Needed for sonification.
    final int FREQ_MIN = 200;
    final int FREQ_MAX = 1640;

    Mergesort(int N)
    {
        elements = createElements(N);
        a = getValues(elements);
        // First frame is ready before first iteration.
        this.frameReady = true;
        this.frameDrawn = false;
        this.unmarkMe = new ArrayList<Element>();
        this.cutStack = new Stack<Integer>();
        // Needed for proper cut index and subset visualization.
        this.cutStack.push(0);
        paused = false;
        exiting = false;
    }

    @Override
    public void run()
    {
        println("--- mergesort-thread has started.");
        sendMessage(OSC_STARTAUDIO);
        // Gain access to monitor. If not possible, wait here.
        synchronized(this)
        {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            // Start sorting.
            a = mergesort(a,Mergesort.THREAD);
        }
        sendMessage(OSC_PAUSEAUDIO);
        println("--- mergesort-thread has terminated.");

    }

    /**
     * ===================================================
     *  Native mergesort implementation with mode NATIVE.
     * ===================================================
     *   This will skip visualization steps.
     *   The given array will be sorted and be returned.
     *   -----------------------------------------------
     * ===================================================
     *  Visual mergesort implementation with mode THREAD.
     * ===================================================
     *   To visualize the dividing, algorithm will
     *   keep track of the cut index to be able to tell
     *   the 'real cut index'. The real cut index is the index,
     *   on which the corresponding element is located in the
     *   base array (=start array at zero recursion).
     *   --Example:
     *   We have a set of length 5 and this will be our base array. Below
     *   are the indizes of the elements.
     *          {5,2,12,8,6}
     *           0 1  2 3 4
     *   When we want to cut this set into two subsets, the cut index will be
     *   2 since 5/2=2.5 and cast to integer makes 2.
     *          {5,2,12,8,6}
     *           0 1  2 3 4
     *                c
     *   This will lead to two subsets:
     *         {5,2} {12,8,6}
     *          0 1    2 3 4
     *   After the left subset is sorted, the right subset will start getting sorted.
     *   Notice that we now dismissed the previous indizes and are now starting again with index 0.
     *         {12,8,6}
     *           0 1 2
     *   Since the subset's size is greater than 1, it will be cut again into two subsets.
     *   This time, the cut index is 1.
     *         {12,8,6}
     *           0 1 2
     *             c
     *   But the real cut index is not 1, it is 3.
     *   If you go into the right subset, the element at the cut index becomes the first element
     *   of the subset. This means, everytime you go right, that real index for the start element is
     *   the real cut index of the superset. Everytime you go left, the real index of the first element stays
     *   the same since the first element in the subset is also the first element in the superset.
     *   To keep track of the previous real cut index during recursion, a stack (FILO) will help us.
     *   -------------------------------------------------------------------------------------------
     *   To visualize the merging, the subsets with their corresponding real indizes will be stored in arrays.
     *   Just like mergesort creates a array to contain the merged ints, a array of elements will be created
     *   which will contain the (visual) elements after merging. For now, it will be filled with the elements in its current
     *   state before merging. While merging, they will be manipulated to look sorted. The following example probably explains
     *   this issue better:
     *   --Example: We have the base array of the previous example; a set of length 5.
     *         {5,2,12,8,6}
     *          0 1  2 3 4
     *   We 'fast-forward' to the point, when mergesort will merge the right subset {12,8,6} into one.
     *   At this point, 8 and 6 will have swapped places.
     *          {12} {6,8}
     *   Mergesort will now compare the first two elements and insert the smaller one into a new array.
     *   merged = {6}
     *   remaining = {12} {8}
     *   At this step, the element at real index 3, which is the 6 here (and was 8), will override the element
     *   at real index 2 since that is the start of the merging set and 6 is the smallest number in this set.
     *   Since we saved the subsets individually, overriding the elements during merging is not a problem.
     *   Frame after frame all elements will reappear sorted.
     *   This means, after the first swap, the base array which is visualized will look like this:
     *         {5,2,6,8,6};
     *   Second swap: {5,2,6,8,6} -Notice that nothing changed, because 8 was already at correct place.;
     *   Last swap: {5,2,6,8,12}
     *   Since for the visualization the index does not matter but the assigned x-Position of the elements
     *   (If you swap two elements in a array, when drawing, there will be no difference since the same elements
     *    do still have the same positions), a queue (FIFO) will assign the correct x-Position to ensure a sorted order
     *   when drawing.
     *   -------------
     * ====================
     *  Visualization code
     * ====================
     * Code which accesses the elements array passed when constructing
     * this instance is always in an if-statement which checks for the mode.
     * This ensure that running this method at mode NATIVE doesn't cause
     * any problems when sorting array is not equal to the array consisting
     * of the values of the elements.
     */
    int[] mergesort(int[] a, byte MODE)
    {
        if(a.length>1)
        {
            int cut = a.length/2;
            // Real cut index of current set. Will be initialized later.
            int realCut = -1;
            int[] left = subset(a,0,cut);
            if(MODE==THREAD)
            {
                /**
                 * First (logical) frame:
                 * Mark cut index element.
                 */
                realCut = cutStack.peek() + cut;
                mark(realCut);
                // times two to compensate for height limit.
                int arg1 = expmap(a[cut]*2);
                int[] args = { arg1 };
                sendMessage(OSC_MODAUDIO,args);
                notifyFrameReady();
            }
            if(MODE==THREAD)
            {
                /**
                 * Second (logical) frame:
                 * Mark cut index element and left subset.
                 */
                mark(realCut);
                // Startindex of left subset is at previous realCut Index.
                int l = cutStack.peek();
                int len = floor(a.length/2.0);
                Element[] subset = (Element[])(subset(elements,l,len));
                markInSubset(subset);
                // TODO: Sonificate subset.
                notifyFrameReady();
            }
            left = mergesort(left,MODE);
            int[] right = subset(a,cut);
            if(MODE==THREAD)
            {
                /**
                 * Third (logical) frame:
                 * Mark cut index element and right subset.
                 */
                 /**
                  * Will go right now. Push current cut index to stack to know
                  * where the index is at base level (=real cut index).
                  */
                  // After left subset got merged,
                realCut = cutStack.peek() + cut;
                cutStack.push(realCut);
                mark(realCut);
                // Startindex of right subset is at index=realCut.
                int r = realCut;
                int len = ceil(a.length/2.0);
                Element[] subset = (Element[])(subset(elements,r,len));
                markInSubset(subset);
                // TODO: Sonificate subset.
                notifyFrameReady();
            }
            right = mergesort(right,MODE);
            if(MODE==THREAD)
            {
                /**
                 * Third (logical) frame:
                 * Mark cut index.
                 */
                mark(realCut);
                int arg1 = expmap(a[cut]*2);
                int[] args = { arg1 };
                sendMessage(OSC_MODAUDIO,args);
                notifyFrameReady();
            }
            /**
             * Next frames:
             * See comments in merge().
             */
            return merge(left,right,MODE);
        }
        else
        {
            return a;
        }
    }
    // Merge sets together into a.
    private int[] merge(int[] left, int[] right, byte MODE)
    {
        /**
        * If one array should be empty (which shouldn't be the case),
        * return the other one since we can assume it's already sorted.
        */
        if(left.length == 0) return right;
        else if(right.length == 0) return left;

        /**
         * ======================
         *   VISUALIZATION CODE
         * ======================
         */
        // This will contain the new merged visual elements.
        Element[] merged = new Element[0];
        // Subsets which are going to merge.
        Element[] leftSub = new Element[0];
        Element[] rightSub = new Element[0];
        // Save a ordered state of posX to assign correct x-values to sorted elements.
        ArrayDeque<Integer> posXqueue = new ArrayDeque<Integer>();
        // (Real) Index at which the subsets start. Is initialized correctly later.
        int leftStart = 0, rightStart = 0;
        // Length of resulting set.
        int len = left.length + right.length;
        if(MODE==THREAD)
        {
            // Real startindex of right subset is last pushed real cut index.
            rightStart = cutStack.pop();
            // Length of right subset.
            int rightLen = ceil(len/2.0);
            /**
             * Real startindex of left subset is now - after pop() at initializing rightStart -
             * the last pushed real cut index.
             */
            leftStart = cutStack.peek();
            // Length of left subset.
            int leftLen = floor(len/2.0);
            // Get subsets as copies.
            leftSub = new Element[leftLen];
            for(int i=0;i<leftLen;++i)
            {
                leftSub[i] = elements[leftStart+i].copy();
            }
            rightSub = (Element[])(subset(elements,rightStart,rightLen));
            for(int i=0;i<rightLen;++i)
            {
                rightSub[i] = elements[rightStart+i].copy();
            }
            // Init new set to contain merged elements.
            merged = new Element[0];
            // Fill it with old (current) elements for now and push all xPos to queue.
            for(Element el : leftSub)
            {
                posXqueue.add(el.getX());
                merged = (Element[])append(merged, el);
            }
            for(Element el : rightSub)
            {
                posXqueue.add(el.getX());
                merged = (Element[])append(merged, el);
            }
            // Some checks to identify bugs ASAP.
            assert(leftSub.length==left.length);
            assert(rightSub.length==right.length);
            assert(merged.length==left.length+right.length);
            for(int i=0;i<leftSub.length;++i)
            {
                assert(leftSub[i].value==left[i]);
            }
            for(int i=0;i<rightSub.length;++i)
            {
                assert(rightSub[i].value==right[i]);
            }
        }
        /**
         * ================
         *  MERGESORT CODE
         * ================
         * Create a new list big enough to hold all elements
         * of both lists.
         */
        int[] newlist = new int[left.length + right.length];
        assert(newlist.length>=2);
        // "Pointer" of lists
        int i=0,j=0,k=0;
        // The sorting part:
        do
        {
            if(left[j]>right[k])
            {
                // Next element in sorted order is in the right subset.
                newlist[i] = right[k];
                if(MODE==THREAD)
                {
                    Element e = rightSub[k].copy();
                    // Assing correct x-position for correct visualization of sorting.
                    e.setX(posXqueue.poll());
                    // Mark element as merging but not unmark next frame.
                    e.setMerging(true);
                    merged[i] = e;
                }
                k++;
            }
            else
            {
                // Next element in sorted order is in the left subset.
                newlist[i] = left[j];
                if(MODE==THREAD)
                {
                    Element e = leftSub[j].copy();
                    e.setX(posXqueue.poll());
                    e.setMerging(true);
                    merged[i] = e;
                }
                j++;
            }
            if(MODE==THREAD)
            {
                // Every swap is one frame.
                updateElements(merged,leftStart,len);
                int arg1 = expmap(newlist[i]*2);
                int[] args = { arg1 };
                sendMessage(OSC_MODAUDIO, args);
                notifyFrameReady();
            }
            i++;
        }while(j<left.length && k<right.length);
        // Put the rest of the elements in the merged list since they are already sorted.
        if(j<left.length)
        {
            for(;j<left.length;++j)
            {
                newlist[i] = left[j];
                if(MODE==THREAD)
                {
                    Element e = leftSub[j].copy();
                    e.setX(posXqueue.poll());
                    e.setMerging(true);
                    merged[i] = e;
                    updateElements(merged,leftStart,len);
                    int arg1 = expmap(newlist[i]*2);
                    int[] args = { arg1 };
                    sendMessage(OSC_MODAUDIO, args);
                    notifyFrameReady();
                }
                i++;
            }
        }
        else if(k<right.length)
        {
            for(;k<right.length;++k)
            {
                newlist[i] = right[k];
                if(MODE==THREAD)
                {
                    Element e = rightSub[k].copy();
                    e.setX(posXqueue.poll());
                    e.setMerging(true);
                    merged[i] = e;
                    updateElements(merged,leftStart,len);
                    int arg1 = expmap(newlist[i]*2);
                    int[] args = { arg1 };
                    sendMessage(OSC_MODAUDIO, args);
                    notifyFrameReady();
                }
                i++;
            }
        }
        else
        {
            // This should never be reached.
            assert(false);
        }
        if(MODE==THREAD)
        {
            /**
             * Merging complete. Now unmark merge markers from elements next frame
             * and decrement level of recursion.
             */
            for(Element el : merged)
            {
                unmarkMe.add(el);
                el.decrementRecursionLvl();
            }
            // Show new frame.
            updateElements(merged,leftStart,len);
            notifyFrameReady();
        }
        // Newlist should be sorted; consisting all elements from both subsets.
        return newlist;
    }

    boolean frameIsReady()
    {
        return frameReady;
    }

    boolean frameIsDrawn()
    {
        return frameDrawn;
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

    // Notify main thread that new frame is ready and clears all markers after drawing.
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
    /**
     * NOTE:
     * Mergesort is a recursive algorithm. There is no check during sorting if mergesort should exit.
     * In iterative algorithms, the check can easily be implemented.
     * But for some unknown reason this works anyway without checking. Mergesort does exit as expected.
     * CHECK: Does it run so fast without visualizing (the notify() function does have a check for exit)
     * that it seems to exit immediately? This could very well be but not sure yet.
     * UPDATE: After sonification, it was audible that sorting first finishes (without visualization)
     * before exiting. => Assumption was correct.
     * Since this is nothing causing a fatal error, I won't do something about it immediately.
     */
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

    // Update elements with merge step.
    void updateElements(Element[] merged, int left, int len)
    {
        int j = 0;
        for(int i=0;i<len;++i)
        {
            elements[left+i] = merged[j];
            j++;
        }
    }

    // Return updated elements.
    Element[] getElements()
    {
        return elements;
    }

    // Mark currently accessed elements in elements array.
    void mark(int i)
    {
        /**
         * Mark left element since in show(), the marker will be between
         * the cut index (=i) and its left element. When left element is marked,
         * order of drawing is like following:
         * 1. Mark between left element and element at cut index.
         * 2. Left element.
         * 3. Cut index element.
         */
        elements[i-1].setMark(true);
        // Add element to list of elements which get unmarked next frame.
        unmarkMe.add(elements[i-1]);
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
            el.incrementRecursionLvl();
            unmarkMe.add(el);
        }
    }

    // Clear markers from last frame.
    void clearMarkers()
    {
        for(Element e : unmarkMe)
        {
            e.setMark(false);
            e.setInSubset(false);
            e.setMerging(false);
        }
        // remove all elements from list since a new frame will begin now.
        unmarkMe.clear();
    }
}
