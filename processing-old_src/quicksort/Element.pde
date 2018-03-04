/*
* @Author: ekzyis
* @Date:   30-01-2018 00:28:20
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:04:10
*/
/**
 * This class saves the data for visualization.
 * ============================================
 * Instances of this class are the objects you can
 * see getting sorted on the canvas.
 */
class Element
{
    /**
     * Values which are passed to swap function.
     * They define which member should be swapped.
     */
    // swap values
    final static byte VALUES = 1; // 2^0
    // swap colors
    final static byte COLORS = 2; // 2^1
    // swap coordinates
    final static byte COORDINATES = 4; // 2^2
    // (x,y)-position, height=value, width, color
    private int x,y,value,w;
    private color c;
    // Was this element accessed by the sorting algorithm during current frame?
    private boolean marked;

    // With what color is this element marked? (If it is.)
    private color markedColor;
    // Marking color for subsets
    private color subsetColor = color(0,0,255,75);
    // Marking color for merging elements
    private color swapColor = color(0,255,0,75);

    // Is this element in a subset currently marked by quicksort?
    private boolean inSubset;
    // Is this element in a sorted state?
    private boolean sorted;
    // Is this element currently being swapped by quicksort?
    private boolean swapping;

    // constructor
    Element(int _x, int _y, int _w, int _v, color _c)
    {
        this.x = _x;
        this.y = _y;
        this.w = _w;
        this.value = _v;
        this.c = _c;
    }

    // how to show this on canvas
    void show()
    {
        int realHeight = H;
        if(sorted == true)
        {
            // green transparent background
            fill(color(0,255,0,50));
            noStroke();
            rect(x,0,w,realHeight);
            stroke(0);
        }
        if(marked == true)
        {
            // red vertical line
            fill(markedColor);
            noStroke();
            rectMode(CENTER);
            PVector center = new PVector(x+w/2,(H-value)/2);
            rect(center.x,center.y,1,H-value);
            rectMode(CORNER);
            stroke(0);
        }
        if(inSubset == true)
        {
            // blueish transparent background
            noStroke();
            fill(subsetColor);
            rect(x,0,w,realHeight);
            stroke(0);
        }
        else if(swapping == true)
        {
            // greenish transparent background
            noStroke();
            fill(swapColor);
            rect(x,0,w,realHeight);
            stroke(0);
        }
        fill(c);
        rect(x,y,w,-value);
    }

    // string representation
    String string()
    {
        return "(v:"+value+", x:"+x+")";
    }

    /**
     * Swap function.
     * This function is meant to be used with the logical operator |.
     * This means, calling e1.swap(e2, Element.VALUES | Element.COLORS);
     * will swap values and colors between Element e1 and e2.
     */
    void swap(Element e, byte a)
    {
        // is bit 0 (=2^0) set?
        if((a & 1)==1)
        {
            int tmp = e.value;
            e.value = this.value;
            this.value = tmp;
        }
        // is bit 1 (=2^1) set?
        if(((a >> 1) & 1)==1)
        {
            color tmp = e.c;
            e.c = this.c;
            this.c = tmp;
        }
        // is bit 2 (=2^2) set?
        if(((a >> 2) & 1)==1)
        {
            int tmpX = e.x;
            int tmpY = e.y;
            e.x = this.x;
            e.y = this.y;
            this.x = tmpX;
            this.y = tmpY;
        }
    }

    // Set this element as marked.
    void mark(color c)
    {
        this.marked = true;
        this.markedColor = c;
    }
    // Unmark this element.
    void unmark()
    {
        this.marked = false;
    }

    // Set this element's state as sorted.
    void setSorted(boolean set)
    {
        this.sorted = set;
    }

    // Set this element's state as in a subset.
    void setInSubset(boolean set)
    {
        this.inSubset = set;
    }

    // Mark element as swapping.
    void setSwapping(boolean set)
    {
        this.swapping = set;
    }

    int getX() { return x; }

    void setX(int _x) { this.x = _x; }

    int getY() { return y; }

    void setY(int _y) { this.y = _y; }

    int getWidth() { return w; }

    void setWidth(int _w) { this.w = _w; }

    int getValue() { return value; }

    void setValue(int _v) { this.value = _v; }

    color getColor() { return c; }

    void setColor(color _c) { this.c = _c; }

}

/**
 * Return elements with random value and given color(s).
 */
Element[] createElements(int n)
{
    Element[] elements = new Element[n];
    int elementwidth = W/n;
    color[] c = getColors();
    // x offset
    int xd = 0;
    // color array offset
    int cd = 0;
    for(int i=0;i<elements.length;++i)
    {
        int value = (int)(Math.random()*H);
        elements[i] = new Element(xd,H,elementwidth,value,c[cd]);
        xd += elementwidth;
        cd++;
        if(cd==c.length) cd = 0;
    }
    return elements;
}

// Get some test elements for well ... testing purposes
Element[] getTestElements()
{
    color[] c = getColors();
    int n = 6;
    Element[] elements = new Element[n];
    int[] values = {254,200,310,87,112,44};
    int elementwidth = W/n;
    int xd = 0;
    int cd = 0;
    for(int i=0;i<elements.length;++i)
    {
        elements[i] = new Element(xd,H,elementwidth,values[i],c[cd]);
        xd += elementwidth;
        cd++;
        if(cd==c.length) cd = 0;
    }
    return elements;
}

// Return int[] from values of elements.
static int[] getValues(Element[] e)
{
    int[] values = new int[e.length];
    for(int i=0;i<e.length;++i)
    {
        values[i] = e[i].value;
    }
    return values;
}

// Print an element array.
static void printarr(Element[] e)
{
    print("{");
    for(Element el : e)
    {
        print(el.string());
    }
    print("}");
    println();
}

// Checks if an Element[] is in ascending order.
static boolean isSorted(Element[] e)
{
    for(int i=0;i<e.length-1;++i)
    {
        if(e[i].value>e[i+1].value || e[i].x>e[i+1].x) return false;
    }
    return true;
}
