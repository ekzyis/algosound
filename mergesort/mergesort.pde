import java.util.Stack;

/**
 * Mergesort implementation.
 * =============================
 * This class handles the execution of mergesort
 * and notifying to draw new frames.
 * 
 * @author ekzyis
 * @date 13 January 2018
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
     * Native mergesort implementation with mode NATIVE.
     * Visual mergesort implementation with mode THREAD.
     */
    int[] mergesort(int[] a, byte MODE)                                                      
    {
        if(a.length>1)
        {            
            int cut = a.length/2;         
            int realCut = cutStack.peek() + cut;       
            int[] left = subset(a,0,cut);            
            print("a=");printarr(a);
            println("len="+a.length+", cut="+cut+", realCut="+realCut);
            if(MODE==THREAD) 
            {
                /**
                 * TODO:
                 * First (logical) frame:
                 * Mark cut index.
                 */
                // println(realCut);
                mark(realCut);                                
                notifyFrameReady();
            }         
            println("go left.");
            if(MODE==THREAD) 
            {
                /**
                 * TODO:
                 * Second (logical) frame:
                 * Mark cut index and left subset.
                 */            
                //println(realCut);
                mark(realCut);
                int l = cutStack.peek();
                int len = floor(a.length/2.0);
                Element[] subset = (Element[])(subset(elements,l,len));
                markInSubset(subset);
                println("l="+l+", len="+len);
                notifyFrameReady();
            }
            left = mergesort(left,MODE);
            int[] right = subset(a,cut);
            print("a=");printarr(a);
            println("len="+a.length+", cut="+cut+", realCut="+realCut);
            println("stack.push("+realCut+") & go right.");
            // Will go right now. Push current cut index to stack.
            cutStack.push(realCut);
            if(MODE==THREAD) 
            {
                /**
                 * TODO:
                 * Third (logical) frame:
                 * Mark cut index and right subset.
                 */
                //println(realCut);            
                mark(realCut);
                int l = realCut;
                int len = ceil(a.length/2.0);
                Element[] subset = (Element[])(subset(elements,l,len));
                markInSubset(subset);
                println("l="+l+", len="+len);
                notifyFrameReady();
            }
            right = mergesort(right,MODE);
            println("stack.pop()");
            cutStack.pop();
            if(MODE==THREAD) 
            {
                /**
                 * TODO:
                 * First (logical) frame:
                 * Mark cut index.
                 */
                // println(realCut);
                mark(realCut);                                
                notifyFrameReady();
            }
            /**
             * TODO:
             * Define frames in merge().
             */
            return merge(left,right,MODE);
        }
        else
        {
            println("return");
            return a;
        }
    }
    // Merge sets together into a.
    private int[] merge(int[] left, int[] right, byte MODE)
    {
        println("### MERGING ###");
        /** 
        * If one list should be empty (which shouldn't be the case),
        * return the other one since we can assume it's already sorted.
        */
        if(left.length == 0) return right;
        else if(right.length == 0) return left;

        // Length of resulting set.
        int len = left.length + right.length;
        // Cut index.
        int cut = len/2;
        // Real startindex of left subset.
        int leftStart = cutStack.peek();
        // Length of left subset.
        int leftLen = floor(len/2.0);
        println("leftStart="+leftStart+", leftLength="+leftLen);
        // Real startindex of right subset.
        int rightStart = cutStack.peek() + cut;
        // Length of right subset.
        int rightLen = ceil(len/2.0);
        println("rightstart="+rightStart+", rightLength="+rightLen);
        // Get subsets.
        Element[] leftSub = (Element[])(subset(elements,leftStart,leftLen));
        Element[] rightSub = (Element[])(subset(elements,rightStart,rightLen));
        // Create new set to contain merged elements.
        Element[] merged = new Element[len];
        // Fill with elements from subsets for now.
        for(int i=0;i<leftLen;++i)
        {
            merged[i] = leftSub[i];
        }
        {
            int k=0;
            for(int j=leftLen;j<len;++j)
            {
                merged[j] = rightSub[k];
                k++; 
            }
        }
        /** 
         * Create a new list big enough to hold all elements 
         * of both lists.
         */
        int[] newlist = new int[left.length + right.length];
        assert(newlist.length>=2);
        // "pointer" of lists
        int i=0,j=0,k=0;
        // Real indizes of subset elements.
        int realJ = leftStart, realK=rightStart;
        // The sorting part:
        do
        {  
            if(left[j]>right[k]) 
            {
                newlist[i] = right[k];
                if(MODE==THREAD)
                {
                    swap(realJ,realK);
                    merged[i] = rightSub[k];
                    realK++;
                }
                k++;
            }
            else 
            {
                newlist[i] = left[j];
                if(MODE==THREAD)
                {
                    merged[i] = leftSub[j];
                    realJ++;
                }
                j++;
            }
            if(MODE==THREAD)
            {
                updateElements(merged,leftStart,len);
                notifyFrameReady();
            }
            i++;
        }while(j<left.length && k<right.length);
        // put the rest of the elements in newlist
        if(j<left.length)
        {
            for(;j<left.length;++j)
            {
                newlist[i] = left[j];
                merged[i] = leftSub[j];
                i++;
                if(MODE==THREAD)
                {
                    updateElements(merged,leftStart,len);
                    notifyFrameReady();
                }
            }
        }
        else if(k<right.length)
        {
            for(;k<right.length;++k)
            {
                newlist[i] = right[k];
                merged[i] = rightSub[k];
                if(MODE==THREAD)
                {
                    updateElements(merged,leftStart,len);
                    notifyFrameReady();
                }
                i++;
            }
        }
        else
        {
            // this should never be reached
            assert(false);
        }
        // newlist should be sorted now
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
        for(int i=left;i<left+len;++i)
        {
            elements[i] = merged[j];
            j++;
        }
    }

    // Return updated elements.
    Element[] getElements()
    {
        return elements;
    }

    // Swap element at given indizes to ensure visualization.
    void swap(int i, int j)
    {
        /**
         * Elements need to swap their x-position AND their position in the array!
         * Otherwise, next iteration would cause severe bugs since implementation
         * assumes the corresponding element is at the same index in the 
         * elements array.
         */
        elements[i].swap(elements[j],Element.COORDINATES);
        Element tmp = elements[i];
        elements[i] = elements[j];
        elements[j] = tmp;
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

    /**
     * Mark elements as being merged by mergesort.
     * This also decreases level of recursion by one.
     */
    void markMerging(Element[] e)
    {
        for(Element el : e)
        {
            el.setMerging(true);
            el.decrementRecursionLvl();
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