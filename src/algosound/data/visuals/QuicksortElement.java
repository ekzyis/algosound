package algosound.data.visuals;

import algosound.util.AlgosoundUtil;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.*;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNER;

/**
 * Elements with members and methods specifically used by quicksort.
 * ================================
 *
 * @author ekzyis
 * @date 08/03/2018
 */
public class QuicksortElement extends Element {

    // With what color is this element marked? (If it is.)
    private Color markedColor;
    // Marking color for subsets
    private Color subsetColor = new Color(0, 0, 255, 50);
    // Marking color for merging elements
    private Color swapColor = new Color(0, 255, 0, 75);

    // Is this element in a subset currently marked by quicksort?
    private boolean inSubset;
    // Is this element in a sorted state?
    private boolean sorted;
    // Is this element currently being swapped by quicksort?
    private boolean swapping;

    public QuicksortElement(int _x, int _y, int _w, int _h, PApplet _inst, Color _c) {
        super(_x, _y, _w, _h, _inst, _c);
    }

    @Override
    public void show() {
        int realHeight = AlgosoundUtil.H;
        if (inSubset == true) {
            // blueish transparent background
            inst.noStroke();
            fill(subsetColor);
            inst.rect(x, 0, w, realHeight);
            inst.stroke(0);
        } else if (swapping == true) {
            // greenish transparent background
            inst.noStroke();
            fill(swapColor);
            inst.rect(x, 0, w, realHeight);
            inst.stroke(0);
        }
        if (sorted == true) {
            // green transparent background
            fill(new Color(0, 255, 0, 50));
            inst.noStroke();
            inst.rect(x, 0, w, realHeight);
            inst.stroke(0);
        }
        if (marked == true) {
            // red vertical line
            fill(markedColor);
            inst.noStroke();
            inst.rectMode(CENTER);
            PVector center = new PVector(x + w / 2, (AlgosoundUtil.H - h) / 2);
            inst.rect(center.x, center.y, 1, AlgosoundUtil.H - h);
            inst.rectMode(CORNER);
            inst.stroke(0);
        }
        fill(c);
        inst.rect(x, y, w, -h);
    }

    /**
     * Return n random elements as an array.
     *
     * @param n      number of elements
     * @param sketch PApplet instance
     * @return array with random elements
     */
    public static QuicksortElement[] createElements(int n, PApplet sketch) {
        QuicksortElement[] elements = new QuicksortElement[n];
        int elementwidth = AlgosoundUtil.W / n;
        Color[] c = new Color[]{
                new Color(255, 50, 50),
                new Color(50, 255, 50),
                new Color(50, 50, 255),
                new Color(200, 50, 200)};
        // x offset
        int xd = 0;
        // color array offset
        int cd = 0;
        for (int i = 0; i < elements.length; ++i) {
            int value = (int) (Math.random() * AlgosoundUtil.H);
            elements[i] = new QuicksortElement(xd, AlgosoundUtil.H, elementwidth, value, sketch, c[cd]);
            xd += elementwidth;
            cd++;
            if (cd == c.length) cd = 0;
        }
        return elements;
    }

    // Set this element's state as sorted.
    public void setSorted(boolean set) {
        this.sorted = set;
    }

    // Set this element's state as in a subset.
    public void setInSubset(boolean set) {
        this.inSubset = set;
    }

    // Set this element as marked.
    public void mark(Color c) {
        this.marked = true;
        this.markedColor = c;
    }

    // Mark element as swapping.
    public void setSwapping(boolean set) {
        this.swapping = set;
    }
}
