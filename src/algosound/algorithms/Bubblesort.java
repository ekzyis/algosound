package algosound.algorithms;

import algosound.data.Sonification;
import algosound.net.OSC;
import algosound.util.AlgosoundUtil;

/**
 * Bubblesort implementation.
 * Handling of new frames and sending osc messages for sonification of bubblesort.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class Bubblesort extends SortingThread {

    public Bubblesort(int N) {
        super(N);
    }

    @Override
    public void run() {
        System.out.println("--- bubblesort-thread started.");

        OSC osc = OSC.getInstance();
        Sonification selected = osc.getSelectedSonification();
        if (selected == Sonification.WAVE) {
            osc.sendMessage(osc.STARTAUDIO);
        } else if (selected == Sonification.SCALE) {
            int[] args = { Sonification.FREQ_MIN, Sonification.FREQ_MAX };
            osc.sendMessage(osc.STARTAUDIO, args);
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
                    System.out.println("value to map: " + value);
                    int[] args = { AlgosoundUtil.expmap(value) };
                    System.out.println("mapped values: " + args[0]);
                    osc.sendMessage(osc.MODAUDIO, args);
                }
            } while (swap);
            /**
             * Bubblesort keeps iterating through the whole array until not a single time a
             * swap has happened or the thread has been interrupted.
             */
        }
        osc.sendMessage(osc.FREEAUDIO);
        System.out.println("--- bubblesort-thread terminated.");
    }
}
