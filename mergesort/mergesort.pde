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
 * @date 14 January 2018
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
    // Object for synchronization of threads.
    private Object lock;
    // List of elements to unmark next frame.
    private ArrayList<Element> unmarkMe;
    // Keep track of stack of the cut indizes while sorting for proper visualization.
    private Stack<Integer> cutStack;

    Mergesort(int[] _a, Object _lock, Element[] _elements)
    {
        this.a = _a;
        this.lock = _lock;
        this.elements = _elements;
        // First frame is ready before first iteration.
        this.frameReady = true;
        this.frameDrawn = false;
        this.unmarkMe = new ArrayList<Element>();
        this.cutStack = new Stack<Integer>();
        // Needed for proper cut index and subset visualization.
        this.cutStack.push(0);
    }

    @Override
    public void run()
    {
        // Gain access to monitor. If not possible, wait here.
        synchronized(lock)
        {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            // Start sorting.
            a = mergesort(a,Mergesort.THREAD);
        }
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

    // Notify main thread that new frame is ready and clears all markers after drawing.
    void notifyFrameReady()
    {
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
        // Clear markers from last frame.
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
        elements[i].setMark(true);
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
