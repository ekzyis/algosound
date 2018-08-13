package algosound.data.visuals;

import algosound.util.AlgosoundUtil;
import processing.core.PApplet;

import java.awt.*;


/**
 * Elements with members and methods specifically used by mergesort.
 * ================================
 *
 * @author ekzyis
 * @date 08/03/2018
 */
public class MergesortElement extends Element {

    // Marking color for subsets
    private Color subsetColor;
    // Marking color for merging elements
    private Color mergingColor;
    // Y-offset per level of recursion
    private int recursionYOffset = 20;
    // How deep in recursion is this element?
    private int recursionLevel;
    // Is this element in a subset currently marked by mergesort?
    private boolean inSubset;
    // Is this element in a subset being merged?
    private boolean merging;

    public MergesortElement(int _x, int _y, int _w, int _v, PApplet _inst, Color c) {
        super(_x, _y, _w, _v, _inst, c);
        this.subsetColor = new Color(0, 0, 255, 75);
        this.mergingColor = new Color(0, 255, 0, 75);
        this.recursionYOffset = 20;
        this.recursionLevel = 0;
        this.merging = false;
    }

    // Returns a copy of this instance.
    public MergesortElement copy() {
        MergesortElement e = new MergesortElement(x, y, w, h, inst, c);
        e.marked = this.marked;
        e.sorted = this.sorted;
        e.recursionLevel = this.recursionLevel;
        e.merging = this.merging;
        return e;
    }

    @Override
    public void show() {
        int realHeight = AlgosoundUtil.H - (recursionLevel * recursionYOffset);
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
            inst.rect(x + 3 * w / 4, 0, w / 2, realHeight);
            inst.stroke(0);
        }
        if (inSubset == true) {
            // blueish transparent background
            inst.noStroke();
            fill(subsetColor);
            inst.rect(x, 0, w, realHeight);
            inst.stroke(0);
        } else if (merging == true) {
            // greenish transparent background
            inst.noStroke();
            fill(mergingColor);
            inst.rect(x, 0, w, realHeight);
            inst.stroke(0);
        }
        fill(c);
        inst.rect(x, realHeight, w, -h);
    }

    // Mark element as being in a subset on which mergesort currently operates on.
    public void setInSubset(boolean set) {
        this.inSubset = set;
    }

    // Mark element as merging.
    public void setMerging(boolean set) {
        this.merging = set;
    }

    // Increment level of recursion.
    public void incrementRecursionLvl() {
        this.recursionLevel++;
    }

    // Decrement level of recursion.
    public void decrementRecursionLvl() {
        this.recursionLevel--;
    }

    /**
     * Return n random elements as an array.
     *
     * @param n      number of elements
     * @param sketch PApplet instance
     * @return array with random elements
     */
    public static MergesortElement[] createElements(int n, PApplet sketch) {
        MergesortElement[] elements = new MergesortElement[n];
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
        /**
         * Heightlimit for elements. Without this, elements could go higher
         * than actual canvas height due to recursion lifting the elements.
         */
        int maxHeight = (int) (AlgosoundUtil.H - (Math.log(n) / Math.log(2)) * 20);
        for (int i = 0; i < elements.length; ++i) {
            int value = (int) (Math.random() * maxHeight);
            elements[i] = new MergesortElement(xd, AlgosoundUtil.H, elementwidth, value, sketch, c[cd]);
            xd += elementwidth;
            cd++;
            cd = cd % c.length;
        }
        return elements;
    }
}
