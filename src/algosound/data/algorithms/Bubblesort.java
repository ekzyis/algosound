package algosound.data.algorithms;

import algosound.data.Sonification;
import algosound.net.OSC;
import algosound.util.AlgosoundUtil;

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

    // Sonification variants for bubblesort.
    private static final String suffix = "_BUBBLESORT";
    private static final Sonification WAVE = new Sonification(
            "WAVE",
            "/wave_start" + suffix,
            "/wave_pause" + suffix,
            "/wave_resume" + suffix,
            "/wave_set" + suffix,
            "/wave_free" + suffix,
            "/hellowave" + suffix,
            "/boot_wave" + suffix,
            "/wave_set_amp"+suffix+"~/wave_set_freqlag"+suffix+"~/wave_set_amplag"+suffix,
            "AMP~FREQLAG~AMPLAG",
            new float[]{0f,3f,0.2f,
                    0f,2f,0.1f,
                    0f,5f,0.1f});
    private static final Sonification SCALE = new Sonification(
            "SCALE",
            "/scale_start" + suffix,
            "",
            "",
            "/scale_play" + suffix,
            "",
            "/helloscale" + suffix,
            "/boot_scale_BUBBLESORT",
            "/scale_set_amp"+suffix+"~/scale_set_MINFREQ"+suffix+"~/scale_set_MAXFREQ"+suffix,
            "AMP~MINFREQ~MAXFREQ",
            new float[]{0f,0.3f,0.2f,100f,8000f,200f,100f,8000f,4000f});
    private final int FREQ_MIN = 200, FREQ_MAX = 4000;

    public Bubblesort(int N) {
        super(N);
        name = "Bubblesort";
        sonifications.add(WAVE);
        sonifications.add(SCALE);
        selected_sonification = WAVE;
    }

    @Override
    public void run() {
        System.out.println("--- bubblesort-thread started.");

        OSC osc = OSC.getInstance();
        Sonification sel = selected_sonification;
        if (sel == WAVE) {
            osc.sendMessage(sel.STARTPATH);
        } else if (sel == SCALE) {
            int[] args = { FREQ_MIN, FREQ_MAX };
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
                    float pan = map(i, 0, elements.length-1, -1, 1);
                    float[] args = { AlgosoundUtil.expmap(value, 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX), pan };
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
