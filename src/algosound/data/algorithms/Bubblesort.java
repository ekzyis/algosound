package algosound.data.algorithms;

import algosound.data.Sonification;
import algosound.net.OSC;
import algosound.util.AlgosoundUtil;

import static algosound.data.Sonification.BUBBLESORT_SCALE;
import static algosound.data.Sonification.BUBBLESORT_WAVE;
import static processing.core.PApplet.map;

/**
 * Bubblesort implementation.
 * Handling of new frames and sending osc messages for sonification of bubblesort.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class Bubblesort extends SortingAlgorithm {

    public static final String SUFFIX = "_BUBBLESORT";

    public Bubblesort(int N) {
        super(N);
        name = "Bubblesort";
        sonifications.add(BUBBLESORT_WAVE);
        sonifications.add(BUBBLESORT_SCALE);
        selected_sonification = sonifications.get(0);
    }

    @Override
    public void run() {
        System.out.println("--- bubblesort-thread started.");

        OSC osc = OSC.getInstance();
        Sonification sel = selected_sonification;

        if (sel == BUBBLESORT_WAVE) {
            osc.sendMessage(sel.STARTPATH);
        } else if (sel == BUBBLESORT_SCALE) {
            int[] args = {FREQ_MIN, FREQ_MAX};
            osc.sendMessage(sel.STARTPATH, args);
        }
        // Gain access to monitor. If not possible, wait here.
        synchronized (this) {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            /**
             * Start of actual sorting algorithm.
             */
            boolean swap;
            do {
                swap = false;
                /**
                 * Notice that thread will terminate when interrupted-flag is (still) set when
                 * the boolean expression is evaluated.
                 */
                for (int i = 0; i < a.length - 1 && !isInterrupted(); ++i) {
                    // They are in false order. Swap them.
                    if (a[i] > a[i + 1]) {
                        int tmp = a[i + 1];
                        a[i + 1] = a[i];
                        a[i] = tmp;
                        swap = true;
                        // Elements need to swap their x-position to ensure visualization.
                        swap(i, i + 1);
                    }
                    // Mark elements accessed by bubblesort.
                    mark(i);
                    mark(i + 1);
                    notifyFrameReady();
                    // Send osc message for sonification.
                    int value = a[i];
                    // System.out.println("value to map: " + value);
                    float pan = map(i, 0, elements.length - 1, -1, 1);
                    float[] args = {AlgosoundUtil.expmap(value, 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX), pan};
                    // System.out.println("mapped values: " + args[0]);
                    osc.sendMessage(sel.MODPATH, args);
                }
            } while (swap);
            /**
             * Bubblesort keeps iterating through the whole array until not a single time a
             * swap has happened or the thread has been interrupted.
             */
        }
        osc.sendMessage(sel.FREEPATH);
        System.out.println("--- bubblesort-thread terminated.");
    }
}
