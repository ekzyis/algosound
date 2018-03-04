package algosound.data;

import java.awt.Color;

import algosound.util.AlgosoundUtil;
import processing.core.PApplet;

/**
 * Rectangle-shaped visual object.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class Element extends Visual {
    /**
     * Values which are passed to swap function. They define which member should be
     * swapped.
     */
    // swap values
    public final static byte VALUES = 1; // 2^0
    // swap colors
    public final static byte COLORS = 2; // 2^1
    // swap coordinates
    public final static byte COORDINATES = 4; // 2^2
    private Color c;
    // Was this element accessed by the sorting algorithm during current frame?
    private boolean marked;
    // Is this element in a sorted state?
    private boolean sorted;

    public Element(int _x, int _y, int _w, int _v, PApplet _inst) {
        super(_x, _y, _w, _v, _inst);
        c = new Color(255, 255, 255);
        marked = false;
        sorted = false;
    }

    public Element(int _x, int _y, int _w, int _h, PApplet _inst, Color _c) {
        this(_x, _y, _w, _h, _inst);
        c = _c;
    }

    // How to show this on canvas.
    @Override
    public void show() {
        if (sorted == true) {
            // green transparent background
            fill(new Color(0, 255, 0, 50));
            inst.noStroke();
            inst.rect(x, 0, w, AlgosoundUtil.H);
            inst.stroke(0);
        }
        if (marked == true) {
            // red vertical line
            fill(new Color(255, 0, 0));
            inst.noStroke();
            inst.rect(x + w / 4, 0, w / 2, AlgosoundUtil.H);
            inst.stroke(0);
        }
        fill(c);
        inst.rect(x, y, w, -h);
    }

    // Convenience method.
    public void fill(java.awt.Color c) {
        inst.fill(c.getRed(), c.getGreen(), c.getBlue());
    }

    // Set this element as marked.
    public void mark() {
        this.marked = true;
    }

    // Unmark this element.
    public void unmark() {
        this.marked = false;
    }

    // Set this element's state as sorted.
    public void setSorted() {
        this.sorted = true;
    }

    /**
     * Swap function. This function is meant to be used with the logical operator |.
     * This means, calling e1.swap(e2, Element.VALUES | Element.COLORS); will swap
     * values and colors between Element e1 and e2.
     */
    public void swap(Element e, byte a) {
        // is bit 0 (=2^0) set?
        if ((a & 1) == 1) {
            int tmp = e.h;
            e.h = this.h;
            this.h = tmp;
        }
        // is bit 1 (=2^1) set?
        if (((a >> 1) & 1) == 1) {
            Color tmp = e.c;
            e.c = this.c;
            this.c = tmp;
        }
        // is bit 2 (=2^2) set?
        if (((a >> 2) & 1) == 1) {
            int tmpX = e.x;
            int tmpY = e.y;
            e.x = this.x;
            e.y = this.y;
            this.x = tmpX;
            this.y = tmpY;
        }
    }

    /**
     * Return n random elements as an array.
     *
     * @param n
     *            number of elements
     * @param sketch
     *            PApplet instance
     * @return array with random elements
     */
    public static Element[] createElements(int n, PApplet sketch) {
        Element[] elements = new Element[n];
        int elementwidth = AlgosoundUtil.W / n;
        Color[] c = new Color[] {
                new Color(255, 50, 50),
                new Color(50, 255, 50),
                new Color(50, 50, 255),
                new Color(200, 50, 200) };
        // x offset
        int xd = 0;
        // color array offset
        int cd = 0;
        for (int i = 0; i < elements.length; ++i) {
            int value = (int) (Math.random() * AlgosoundUtil.H);
            elements[i] = new Element(xd, AlgosoundUtil.H, elementwidth, value, sketch, c[cd]);
            xd += elementwidth;
            cd++;
            cd = cd % c.length;
        }
        return elements;
    }

    /**
     * Return an int-array of elements' values.
     *
     * @param e
     * @return values as int-array.
     */
    public static int[] getValues(Element[] e) {
        int[] values = new int[e.length];
        for (int i = 0; i < e.length; ++i) {
            values[i] = e[i].h;
        }
        return values;
    }

    public Color getColor() {
        return c;
    }

    public void setColor(Color c) {
        this.c = c;
    }


}
