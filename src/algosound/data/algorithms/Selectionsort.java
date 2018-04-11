package algosound.data.algorithms;

import algosound.data.Sonification;
import algosound.net.OSC;
import algosound.util.AlgosoundUtil;

import static algosound.util.AlgosoundUtil.expmap;
import static processing.core.PApplet.map;

/**
 * Selectionsort implementation.
 * Handling of new frames and sending osc messages for sonification of selectionsort.
 * NOTE: The mod path does include two paths. They are meant to be splitted with the regex-pattern "~".
 * ================================
 *
 * @author ekzyis
 * @date 08/03/2018
 */
public class Selectionsort extends SortingAlgorithm {

    private static final String suffix = "_SELECTIONSORT";
    // Sonification variants for selectionsort.
    private static final Sonification WAVE = new Sonification(
            "WAVE",
            "/wave_start" + suffix,
            "/wave_pause" + suffix,
            "/wave_resume" + suffix,
            "/wave_set_SELECTIONSORT~/min_set" + suffix,
            "/wave_free" + suffix,
            "/hellowave" + suffix,
            "/boot_wave" + suffix,
            "wave_set_amp" + suffix + "~/wave_set_freqlag" + suffix + "~/wave_set_amplag" + suffix + "~/min_set_pulsefreq" + suffix + "~/min_set_amp" + suffix,
            "AMP~FREQLAG~AMPLAG~MINFREQ~MINAMP",
            new float[]{0f, 3f, 0.2f,
                    0f, 2f, 0.1f,
                    0f, 5f, 0.1f,
                    0f, 10f, 0f,
                    0f, 1f, 0.3f});
    private static final Sonification SCALE = new Sonification(
            "SCALE",
            "/scale_start" + suffix,
            "",
            "",
            "/scale_play_SELECTIONSORT~/scale_play" + suffix,
            "",
            "/helloscale" + suffix,
            "/boot_scale" + suffix,
            "/scale_set_amp" + suffix + "~/scale_set_MINFREQ" + suffix + "~/scale_set_MAXFREQ" + suffix,
            "AMP~MINFREQ~MAXFREQ",
            new float[]{0f, 0.3f, 0.2f,
                    100f, 8000f, 200f,
                    100f, 8000f, 4000f});
    private final int FREQ_MIN = 200, FREQ_MAX = 4000;

    public Selectionsort(int N) {
        super(N);
        name = "Selectionsort";
        sonifications.add(WAVE);
        sonifications.add(SCALE);
        selected_sonification = WAVE;
    }

    @Override
    public void run() {
        System.out.println("--- selectionsort-thread has started.");

        OSC osc = OSC.getInstance();
        Sonification sel = selected_sonification;
        if (sel == WAVE) {
            osc.sendMessage(sel.STARTPATH);
        } else if (sel == SCALE) {
            int[] args = {FREQ_MIN, FREQ_MAX};
            osc.sendMessage(sel.STARTPATH, args);
        }
        // Gain access to monitor. If not possible, wait here.
        synchronized (this) {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            /**
             * ==================================
             * Start of actual sorting algorithm.
             * ==================================
             */
            int start = 0;
            do {
                int minIndex = start;
                for (int i = minIndex + 1; i < a.length & !isInterrupted(); ++i) {
                    int arg1 = expmap(a[i], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
                    float pan = map(i, 0, elements.length - 1, -1, 1);
                    if (a[i] < a[minIndex]) {
                        minIndex = i;
                        int arg2 = expmap(a[minIndex], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
                        float[] args = {arg2, 0};
                        osc.sendMessage(sel.MODPATH.split("~")[1], args);
                    }
                    // Mark element which is getting compared with current smallest element.
                    mark(minIndex);
                    mark(i);
                    float[] args = {arg1, pan};
                    osc.sendMessage(sel.MODPATH.split("~")[0], args);
                    notifyFrameReady();
                }
                int tmp = a[minIndex];
                a[minIndex] = a[start];
                a[start] = tmp;
                swap(minIndex, start);
                elements[start].setSorted();
                notifyFrameReady();
                start++;
            } while (start < a.length & !isInterrupted());
        }
        osc.sendMessage(sel.FREEPATH);
        System.out.println("--- selectionsort-thread has terminated.");
    }
}
