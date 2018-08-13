package algosound.data.algorithms;

import algosound.data.visuals.Element;
import algosound.data.visuals.QuicksortElement;
import algosound.data.Sonification;
import algosound.net.OSC;
import algosound.ui.Algosound;
import algosound.util.AlgosoundUtil;

import java.awt.*;
import java.util.ArrayList;

import static algosound.data.Sonification.QUICKSORT_SCALE;
import static algosound.data.Sonification.QUICKSORT_WAVE;
import static algosound.util.AlgosoundUtil.expmap;
import static processing.core.PApplet.map;
import static processing.core.PApplet.subset;

/**
 * Quicksort implementation.
 * Handling of new frames and sending osc messages for sonification of quicksort.
 * ================================
 *
 * @author ekzyis
 * @date 08/03/2018
 */
public class Quicksort extends SortingAlgorithm {

    public static final String SUFFIX = "_QUICKSORT";

    private QuicksortElement[] elements;
    private ArrayList<QuicksortElement> unmarkMe;
    // Color of pivot element
    private Color pivotColor = new Color(255, 0, 0);
    // Color of elements which are getting compared.
    private Color compareColor = new Color(0, 255, 0);

    public Quicksort(int N) {
        super(N);
        name = "Quicksort";
        elements = QuicksortElement.createElements(N, Algosound.getInstance());
        a = Element.getValues(elements);
        unmarkMe = new ArrayList<QuicksortElement>();
        sonifications.add(QUICKSORT_WAVE);
        sonifications.add(QUICKSORT_SCALE);
        selected_sonification = sonifications.get(0);
    }

    @Override
    public void run() {
        System.out.println("--- quicksort-thread has started.");

        OSC osc = OSC.getInstance();
        Sonification sel = selected_sonification;
        if (sel == QUICKSORT_WAVE) {
            osc.sendMessage(sel.STARTPATH);
        } else if (sel == QUICKSORT_SCALE) {
            int[] args = {FREQ_MIN, FREQ_MAX};
            osc.sendMessage(sel.STARTPATH, args);
        }
        // Gain access to monitor. If not possible, wait here.
        synchronized (this) {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            int lower = 0;
            int upper = a.length - 1;
            quicksortVisual(a, lower, upper);
        }
        osc.sendMessage(sel.FREEPATH);
        System.out.println("--- quicksort-thread has terminated.");
    }

    // Class member to be able to return the pivot height for visualization.
    int pivot;

    void quicksortVisual(int[] a, int lower, int upper) {
        OSC osc = OSC.getInstance();
        Sonification sel = selected_sonification;
        // This indizes will iterate over the set and find elements to swap.
        int l = lower;
        int r = upper;
        int pivotIndex = (int) ((l + r) / 2);
        /**
         * After one do-loop, the element at the pivot index will be inserted
         * where the iterating indizes l and r met. The pivot element will be sorted then
         * since all elements left to it will be smaller and right to it larger than itself.
         */
        pivot = a[pivotIndex];
        QuicksortElement[] subset = (QuicksortElement[]) (subset(elements, l, r - l + 1));
        // Mark pivot element.
        mark(pivotIndex, pivotColor);
        // Mark subset.
        markInSubset(subset);
        // Mark iterator indizes.
        mark(l, compareColor);
        mark(r, compareColor);

        // Sonification.
        int arg1 = expmap(a[l], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
        int arg2 = expmap(a[pivotIndex], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
        int arg3 = expmap(a[r], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
        float pan = map(l, 0, a.length - 1, -1, 1);
        float[] args = {arg1, pan};
        osc.sendMessage(sel.MODPATH.split("~")[0], args);
        args[0] = arg2;
        pan = map(pivotIndex, 0, a.length - 1, -1, 1);
        args[1] = pan;
        osc.sendMessage(sel.MODPATH.split("~")[1], args);
        args[0] = arg3;
        pan = map(r, 0, a.length - 1, -1, 1);
        args[1] = pan;
        osc.sendMessage(sel.MODPATH.split("~")[2], args);

        notifyFrameReady();
        do {
            while (a[l] < pivot) {
                l++;
                mark(pivotIndex, pivotColor);
                markInSubset(subset);
                mark(l, compareColor);
                mark(r, compareColor);

                arg1 = expmap(a[l], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
                args[0] = arg1;
                pan = map(l, 0, a.length - 1, -1, 1);
                args[1] = pan;
                osc.sendMessage(sel.MODPATH.split("~")[0], args);

                notifyFrameReady();
            }
            while (a[r] > pivot) {
                r--;
                mark(pivotIndex, pivotColor);
                markInSubset(subset);
                mark(l, compareColor);
                mark(r, compareColor);

                arg3 = expmap(a[r], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
                args[0] = arg3;
                pan = map(r, 0, a.length - 1, -1, 1);
                args[1] = pan;
                osc.sendMessage(sel.MODPATH.split("~")[2], args);

                notifyFrameReady();
            }
            if (l <= r) {
                int tmp = a[l];
                a[l] = a[r];
                a[r] = tmp;
                elements[l].swap(elements[r], (byte) (Element.VALUES | Element.COLORS));
                mark(pivotIndex, pivotColor);
                markInSubset(subset);
                elements[l].setSwapping(true);
                elements[r].setSwapping(true);
                mark(l, compareColor);
                mark(r, compareColor);

                // Probably no change in sonification after sending of messages.
                arg1 = expmap(a[l], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
                args[0] = arg1;
                pan = map(l, 0, a.length - 1, -1, 1);
                args[1] = pan;
                osc.sendMessage(sel.MODPATH.split("~")[0], args);
                arg3 = expmap(a[r], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
                args[0] = arg3;
                pan = map(r, 0, a.length - 1, -1, 1);
                args[1] = pan;
                osc.sendMessage(sel.MODPATH.split("~")[2], args);

                notifyFrameReady();
                l++;
                r--;
            }
        } while (l <= r & !isExiting());
        if (lower < r & !isExiting()) {
            quicksortVisual(a, lower, r);
        }
        if (l < upper & !isExiting()) {
            quicksortVisual(a, l, upper);
        }
    }

    @Override
    // Clear markers from last frame.
    public void clearMarkers() {
        for (QuicksortElement e : unmarkMe) {
            e.unmark();
            e.setInSubset(false);
            e.setSwapping(false);
        }
        // remove all elements from list since a new frame will begin now.
        unmarkMe.clear();
    }

    /**
     * Mark elements as being in a subset on which mergesort is currently operating.
     * This also increases level of recursion by one.
     */
    void markInSubset(QuicksortElement[] e) {
        for (QuicksortElement el : e) {
            el.setInSubset(true);
            //el.incrementRecursionLvl();
            unmarkMe.add(el);
        }
    }

    // Mark currently accessed elements with given color
    public void mark(int i, Color c) {
        elements[i].mark(c);
        // Add those elements to list of elements which get unmarked next frame.
        unmarkMe.add(elements[i]);
    }

    @Override
    public QuicksortElement[] getVisuals() {
        return elements;
    }
}
