/*
* @Author: ekzyis
* @Date:   29-12-2017 02:19:42
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:02:16
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
    // Is this element in a sorted state?
    private boolean sorted;

    // Marking color for subsets
    private color subsetColor = color(0,0,255,75);
    // Marking color for merging elements
    private color mergingColor = color(0,255,0,75);
    // Y-offset per level of recursion
    private int recursionYOffset = 20;
    // How deep in recursion is this element?
    private int recursionLevel;
    // Is this element in a subset currently marked by mergesort?
    private boolean inSubset;
    // Is this element in a subset being merged?
    private boolean merging;

    // constructor
    Element(int _x, int _y, int _w, int _v, color _c)
    {
        this.x = _x;
        this.y = _y;
        this.w = _w;
        this.value = _v;
        this.c = _c;
        this.marked = false;
        this.sorted = false;
        this.subsetColor = color(0,0,255,75);
        this.mergingColor = color(0,255,0,75);
        this.recursionYOffset = 20;
        this.recursionLevel = 0;
        this.merging = false;
    }

    // how to show this on canvas
    void show()
    {
        int realHeight = H-(recursionLevel*recursionYOffset);
        if(sorted == true)
        {
            // green transparent background
            fill(color(0,255,0,50));
            noStroke();
            rect(x,0,w,H);
            stroke(0);
        }
        if(marked == true)
        {
            // red vertical line
            fill(color(255,0,0));
            noStroke();
            rect(x+3*w/4,0,w/2,realHeight);
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
        else if(merging == true)
        {
            // greenish transparent background
            noStroke();
            fill(mergingColor);
            rect(x,0,w,realHeight);
            stroke(0);
        }
        fill(c);
        rect(x,realHeight,w,-value);
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

    // Set this elements 'marked'-state.
    void setMark(boolean set)
    {
        this.marked = set;
    }

    // Set this element's state as sorted.
    void setSorted()
    {
        this.sorted = true;
    }

    // Mark element as being in a subset on which mergesort currently operates on.
    void setInSubset(boolean set)
    {
        this.inSubset = set;
    }

    // Mark element as merging.

    void setMerging(boolean set)
    {
        this.merging = set;
    }

    // Increment level of recursion.
    void incrementRecursionLvl()
    {
        this.recursionLevel++;
    }

    // Decrement level of recursion.
    void decrementRecursionLvl()
    {
        this.recursionLevel--;
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

    // Returns a copy of this instance.
    Element copy()
    {
        Element e = new Element(x,y,w,value,c);
        e.marked = this.marked;
        e.sorted = this.sorted;
        e.recursionLevel = this.recursionLevel;
        e.merging = this.merging;
        return e;
    }
}

/**
 * Return n elements with random values.
 */
Element[] createElements(int n)
{
    Element[] elements = new Element[n];
    color[] c = getColors();
    int elementwidth = W/n;
    // x offset
    int xd = 0;
    // color array offset
    int cd = 0;
    /**
     * Heightlimit for elements. Without this, elements could go higher
     * than actual canvas height due to recursion lifting the elements.
     */
    int maxHeight = (int)(H-(Math.log(n)/Math.log(2))*20);
    for(int i=0;i<elements.length;++i)
    {
        int value = (int)(Math.random()*maxHeight);
        elements[i] = new Element(xd,H,elementwidth,value,c[cd]);
        xd += elementwidth;
        cd++;
        if(cd==c.length) cd = 0;
    }
    return elements;
}

// Get some test elements for well ... testing purposes
Element[] getTestElements(color[] c)
{
    int n = 6;
    Element[] elements = new Element[n];
    int[] values = {340,234,53,83,405,147};
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
