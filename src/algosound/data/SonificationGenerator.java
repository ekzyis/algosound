package algosound.data;

import algosound.data.algorithms.Bubblesort;
import algosound.data.algorithms.SortingAlgorithm;
import algosound.net.OSC;
import algosound.net.OSCKnob;
import algosound.ui.Algosound;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Knob;

import static algosound.util.AlgosoundUtil.KNOBSIZE;

/**
 * Constructs the individual sonifcations for the algorithms.
 * ================================
 *
 * @author ekzyis
 * @date 18/04/2018
 */
class SonificationGenerator {

    /**
     * BUBBLESORT WAVE
     * ===============
     * TODO
     * Do Controllers need ControlP5 at instantiation?
     * If so, fix the issue that controlP5 is null since sonifications do get created before the object Algosound
     * of type PApplet gets initialized. Is there a workaround for this?
     * -> 1. Create static methods which return the sonifications when needed. Add ControlP5 as argument.
     * This makes sure that there is a initialized controlP5 instance given. (Check for null)
     * -> 2.
     */
    public static Sonification getBubblesortWAVE(ControlP5 cp5) {
        Controller[] c;
        String name = "WAVE";
        String start = "/wave_start" + Bubblesort.SUFFIX;
        String pause = "/wave_pause" + Bubblesort.SUFFIX;
        String resume = "/wave_resume" + Bubblesort.SUFFIX;
        String mod = "/wave_set" + Bubblesort.SUFFIX;
        String free = "/wave_free" + Bubblesort.SUFFIX;
        String status = "/hellowave" + Bubblesort.SUFFIX;
        String boot = "/boot_wave" + Bubblesort.SUFFIX;
        String[] oscpaths = {"/wave_set_amp" + Bubblesort.SUFFIX, "wave_set_freqlag" + Bubblesort.SUFFIX, "/wave_set_amplag"};
        String[] cnames = {"AMP", "FREQLAG", "AMPLAG"};
        float[] cvalues = {0f, 3f, 0.2f,
                0f, 2f, 0.1f,
                0f, 5f, 0.1f};
        c = new Controller[5];
        // First three controllers are osc controllers.
        for (int i = 0; i < 3; ++i) {
            int j = i * 3;
            c[i] = new OSCKnob(cp5, cnames[i], oscpaths[i])
                    .setRadius(KNOBSIZE / 2)
                    .setDragDirection(Knob.HORIZONTAL)
                    .setRange(cvalues[j], cvalues[j + 1])
                    .setValue(cvalues[j + 2])
                    // Do not show them on the canvas since we are currently on creating them
                    .hide();
        }
        // Not "real" osc controllers since they don't send osc messages. But overwriting method is easy.
        c[4] = new OSCKnob(cp5, "MINFREQ", "") {
            @Override
            public void fire() {
                SortingAlgorithm s = (SortingAlgorithm) Algosound.getInstance().getAlgorithm();
                int value = (int) super.getValue();
                if (value < s.FREQ_MAX) {
                    s.FREQ_MIN = value;
                }
            }
        }
                .setRadius(KNOBSIZE / 2)
                .setDragDirection(Knob.HORIZONTAL)
                .setRange(1, 8000)
                .setValue(200)
                .hide();
        c[5] = new OSCKnob(cp5, "MAXFREQ", "") {
            @Override
            public void fire() {
                SortingAlgorithm s = (SortingAlgorithm) Algosound.getInstance().getAlgorithm();
                int value = (int) super.getValue();
                if (value > s.FREQ_MIN) {
                    s.FREQ_MAX = value;
                }
            }
        }
                .setRadius(KNOBSIZE / 2)
                .setDragDirection(Knob.HORIZONTAL)
                .setRange(1, 8000)
                .setValue(4000)
                .hide();
        return new Sonification(name, start, pause, resume, mod, free, status, boot, c);
    }

    static Sonification BUBBLESORT_SCALE;

    {
        Controller[] c;
        String name = "SCALE";
        String start = "/scale_start" + Bubblesort.SUFFIX;
        String pause = "";
        String resume = "";
        String mod = "/scale_play" + Bubblesort.SUFFIX;
        String free = "";
        String status = "/helloscale" + Bubblesort.SUFFIX;
        String boot = "/boot_scale" + Bubblesort.SUFFIX;
        String[] oscpaths = {"/scale_set_amp" + Bubblesort.SUFFIX, "/scale_set_MINFREQ" + Bubblesort.SUFFIX, "/scale_set_MAXFREQ" + Bubblesort.SUFFIX};
        String[] cnames = {"AMP", "MINFREQ", "MAXFREQ"};
        float[] cvalues = {0f, 3f, 0.2f,
                0f, 2f, 0.1f,
                0f, 5f, 0.1f};
        ControlP5 cp5 = Algosound.getInstance().getCP5();
        c = new Controller[3];
        // First controller is normal osc knob. Last two need overriden methods.
        c[0] = new OSCKnob(cp5, cnames[0], oscpaths[0])
                .setRadius(KNOBSIZE / 2)
                .setDragDirection(Knob.HORIZONTAL)
                .setRange(cvalues[0], cvalues[1])
                .setValue(cvalues[2])
                // Do not show them on the canvas since we are currently on creating them
                .hide();
        c[1] = new OSCKnob(cp5, cnames[1], oscpaths[1]) {
            @Override
            public void fire() {
                SortingAlgorithm s = (SortingAlgorithm) Algosound.getInstance().getAlgorithm();
                int value = (int) super.getValue();
                if (value < s.FREQ_MAX) {
                    s.FREQ_MIN = value;
                    float[] args = {super.getValue()};
                    OSC.getInstance().sendMessage(OSCPATH, args);
                }
            }
        }.setRadius(KNOBSIZE / 2)
        .setDragDirection(Knob.HORIZONTAL)
        .setRange(cvalues[3], cvalues[4])
        .setValue(cvalues[5])
        // Do not show them on the canvas since we are currently on creating them
        .hide();
        c[2] = new OSCKnob(cp5, cnames[2], oscpaths[2]) {
            @Override
            public void fire() {
                SortingAlgorithm s = (SortingAlgorithm) Algosound.getInstance().getAlgorithm();
                int value = (int) super.getValue();
                if (value > s.FREQ_MIN) {
                    s.FREQ_MAX = value;
                    float[] args = {super.getValue()};
                    OSC.getInstance().sendMessage(OSCPATH, args);
                }
            }
        }.setRadius(KNOBSIZE / 2)
        .setDragDirection(Knob.HORIZONTAL)
        .setRange(cvalues[6], cvalues[7])
        .setValue(cvalues[8])
        // Do not show them on the canvas since we are currently on creating them
        .hide();
        BUBBLESORT_SCALE = new Sonification(name, start, pause, resume, mod, free, status, boot, c);
    }
}
