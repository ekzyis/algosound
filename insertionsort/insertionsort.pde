/**
 * Insertionsort implementation.
 * =============================
 * This class handles the execution of insertionsort
 * and notifying to draw new frames.
 * 
 * @author ekzyis
 * @date 10 January 2017
 */
class Insertionsort extends Thread
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

    Insertionsort(int[] _a, Object _lock, Element[] _elements)
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
            for(int i=1;i<a.length;++i)
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
                while(j>0 && a[j-1]>value)
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
                }
                // Place to insert has been found!
                a[j] = value;
                // Visualize inserting by modified element to look like the element which should be inserted.
                elements[j].setValue(value);
                elements[j].setColor(insertColor);
                notifyFrameReady();
            }
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

    // Notify thread waiting for lock that new frame is ready.
    void notifyFrameReady()
    {
        frameReady = true;
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