package algosound.data;

import algosound.data.algorithms.*;
import algosound.net.OSC;
import algosound.net.OSCKnob;
import algosound.net.OSCSlider;
import algosound.ui.Algosound;
import algosound.util.AlgosoundUtil;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Knob;

import java.awt.*;

import static algosound.util.AlgosoundUtil.*;

/**
 * Sonification-class. This class is used in the implemented algorithms
 * to define the paths for the specific sonifications.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class Sonification {
    public static final Sonification BUBBLESORT_WAVE = new Sonification(
            "WAVE",
            "/wave_start" + Bubblesort.SUFFIX,
            "/wave_pause" + Bubblesort.SUFFIX,
            "/wave_resume" + Bubblesort.SUFFIX,
            "/wave_set" + Bubblesort.SUFFIX,
            "/wave_free" + Bubblesort.SUFFIX,
            "/hellowave" + Bubblesort.SUFFIX,
            "/boot_wave" + Bubblesort.SUFFIX,
            "/wave_set_amp" + Bubblesort.SUFFIX + "~/wave_set_freqlag" + Bubblesort.SUFFIX + "~/wave_set_amplag" + Bubblesort.SUFFIX,
            "AMP~FREQLAG~AMPLAG",
            new float[]{0f, 3f, 0.2f,
                    0f, 2f, 0.1f,
                    0f, 5f, 0.1f});
    public static final Sonification BUBBLESORT_SCALE = new Sonification(
            "SCALE",
            "/scale_start" + Bubblesort.SUFFIX,
            "",
            "",
            "/scale_play" + Bubblesort.SUFFIX,
            "",
            "/helloscale" + Bubblesort.SUFFIX,
            "/boot_scale_BUBBLESORT",
            "/scale_set_amp" + Bubblesort.SUFFIX + "~/scale_set_MINFREQ" + Bubblesort.SUFFIX + "~/scale_set_MAXFREQ" + Bubblesort.SUFFIX,
            "AMP~MINFREQ~MAXFREQ",
            new float[]{0f, 0.3f, 0.2f,
                    100f, 8000f, 200f,
                    100f, 8000f, 4000f});
    public static final Sonification INSERTIONSORT_WAVE = new Sonification(
            "WAVE",
            "/wave_start" + Insertionsort.SUFFIX,
            "/wave_pause" + Insertionsort.SUFFIX,
            "/wave_resume" + Insertionsort.SUFFIX,
            "/wave_set" + Insertionsort.SUFFIX,
            "/wave_free" + Insertionsort.SUFFIX,
            "/hellowave" + Insertionsort.SUFFIX,
            "/boot_wave" + Insertionsort.SUFFIX,
            "/wave_set_amp" + Insertionsort.SUFFIX + "~/wave_set_freqlag" + Insertionsort.SUFFIX + "~/wave_set_amplag" + Insertionsort.SUFFIX + "~/wave_pulse_set_freq" + Insertionsort.SUFFIX + "~/wave_pulse_set_amp" + Insertionsort.SUFFIX,
            "AMP~FREQLAG~AMPLAG~PULSEFREQ~PULSEAMP",
            new float[]{0f, 3f, 0.2f,
                    0f, 2f, 0.1f,
                    0f, 5f, 0.1f,
                    1f, 100f, 10f,
                    0f, 1f, 0.2f,});
    public static final Sonification INSERTIONSORT_SCALE = new Sonification(
            "SCALE",
            "/scale_start" + Insertionsort.SUFFIX,
            "",
            "",
            "/scale_play" + Insertionsort.SUFFIX,
            "",
            "/helloscale" + Insertionsort.SUFFIX,
            "/boot_scale" + Insertionsort.SUFFIX,
            "/scale_set_amp" + Insertionsort.SUFFIX + "~/scale_set_MINFREQ" + Insertionsort.SUFFIX + "~/scale_set_MAXFREQ" + Insertionsort.SUFFIX,
            "AMP~MINFREQ~MAXFREQ",
            new float[]{0f, 0.3f, 0.2f,
                    100f, 8000f, 200f,
                    100f, 8000f, 4000f});
    public static final Sonification SELECTIONSORT_WAVE = new Sonification(
            "WAVE",
            "/wave_start" + Selectionsort.SUFFIX,
            "/wave_pause" + Selectionsort.SUFFIX,
            "/wave_resume" + Selectionsort.SUFFIX,
            "/wave_set_SELECTIONSORT~/min_set" + Selectionsort.SUFFIX,
            "/wave_free" + Selectionsort.SUFFIX,
            "/hellowave" + Selectionsort.SUFFIX,
            "/boot_wave" + Selectionsort.SUFFIX,
            "/wave_set_amp" + Selectionsort.SUFFIX + "~/wave_set_freqlag" + Selectionsort.SUFFIX + "~/wave_set_amplag" + Selectionsort.SUFFIX + "~/min_set_pulsefreq" + Selectionsort.SUFFIX + "~/min_set_amp" + Selectionsort.SUFFIX,
            "AMP~FREQLAG~AMPLAG~MINFREQ~MINAMP",
            new float[]{0f, 3f, 0.2f,
                    0f, 2f, 0.1f,
                    0f, 5f, 0.1f,
                    0f, 10f, 0f,
                    0f, 1f, 0.3f});
    public static final Sonification SELECTIONSORT_SCALE = new Sonification(
            "SCALE",
            "/scale_start" + Selectionsort.SUFFIX,
            "",
            "",
            "/scale_play_SELECTIONSORT~/scale_play" + Selectionsort.SUFFIX,
            "",
            "/helloscale" + Selectionsort.SUFFIX,
            "/boot_scale" + Selectionsort.SUFFIX,
            "/scale_set_amp" + Selectionsort.SUFFIX + "~/scale_set_MINFREQ" + Selectionsort.SUFFIX + "~/scale_set_MAXFREQ" + Selectionsort.SUFFIX,
            "AMP~MINFREQ~MAXFREQ",
            new float[]{0f, 0.3f, 0.2f,
                    100f, 8000f, 200f,
                    100f, 8000f, 4000f});
    public static final Sonification MERGESORT_WAVE = new Sonification(
            "WAVE",
            "/wave_start" + Mergesort.SUFFIX,
            "/wave_pause" + Mergesort.SUFFIX,
            "/wave_resume" + Mergesort.SUFFIX,
            "/wave_set" + Mergesort.SUFFIX,
            "/wave_free" + Mergesort.SUFFIX,
            "/hellowave" + Mergesort.SUFFIX,
            "/boot_wave" + Mergesort.SUFFIX,
            "/wave_set_amp" + Mergesort.SUFFIX + "~/wave_set_freqlag" + Mergesort.SUFFIX + "~/wave_set_amplag" + Mergesort.SUFFIX,
            "AMP~FREQLAG~AMPLAG",
            new float[]{0f, 3f, 0.2f,
                    0f, 2f, 0.1f,
                    0f, 5f, 0.1f});
    public static final Sonification MERGESORT_SCALE = new Sonification(
            "SCALE",
            "/scale_start" + Mergesort.SUFFIX,
            "",
            "",
            "/scale_play" + Mergesort.SUFFIX,
            "",
            "/helloscale" + Mergesort.SUFFIX,
            "/boot_scale" + Mergesort.SUFFIX,
            "/scale_set_amp" + Mergesort.SUFFIX + "~/scale_set_MINFREQ" + Mergesort.SUFFIX + "~/scale_set_MAXFREQ" + Mergesort.SUFFIX,
            "AMP~MINFREQ~MAXFREQ",
            new float[]{0f, 0.3f, 0.2f,
                    100f, 8000f, 200f,
                    100f, 8000f, 4000f});
    public static final Sonification QUICKSORT_WAVE = new Sonification(
            "WAVE",
            "/wave_start" + Quicksort.SUFFIX,
            "/wave_pause" + Quicksort.SUFFIX,
            "/wave_resume" + Quicksort.SUFFIX,
            "/wave_set1_QUICKSORT~/wave_set2_QUICKSORT~/wave_set3" + Quicksort.SUFFIX,
            "/wave_free" + Quicksort.SUFFIX,
            "/hellowave" + Quicksort.SUFFIX,
            "/boot_wave" + Quicksort.SUFFIX,
            "/wave_set_amp" + Quicksort.SUFFIX + "~/wave_set_freqlag" + Quicksort.SUFFIX + "~/wave_set_amplag" + Quicksort.SUFFIX,
            "AMP~FREQLAG~AMPLAG",
            new float[]{0f, 3f, 0.2f,
                    0f, 2f, 0.1f,
                    0f, 5f, 0.1f});
    public static final Sonification QUICKSORT_SCALE = new Sonification(
            "SCALE",
            "/scale_start" + Quicksort.SUFFIX,
            "",
            "",
            "/scale_play_QUICKSORT~/scale_play_QUICKSORT~/scale_play" + Quicksort.SUFFIX,
            "",
            "/helloscale" + Quicksort.SUFFIX,
            "/boot_scale" + Quicksort.SUFFIX,
            "/scale_set_amp" + Quicksort.SUFFIX + "~/scale_set_MINFREQ" + Quicksort.SUFFIX + "~/scale_set_MAXFREQ" + Quicksort.SUFFIX,
            "AMP~MINFREQ~MAXFREQ",
            new float[]{0f, 0.3f, 0.2f,
                    100f, 8000f, 200f,
                    100f, 8000f, 4000f});
    // Name of this sonification and paths for the osc listeners.
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, MODPATH, FREEPATH, STATUSPATH, BOOTPATH, REALTIMEPATH, REALTIMENAME;
    // Default values of controllers. Three floats per path/name: min, max and default value.
    private final float[] DEFAULTVALUES;
    // The controllers.
    private Controller[] controllers;
    // Style of sonification
    private int STYLE;
    // Types of styles
    public static int KNOBSTYLE = 0;
    public static int SLIDERSTYLE = 1;

    public Sonification(String name, String start, String pause, String resume, String mod, String free, String status,
                        String boot, String rtpaths, String rtname, float[] defvalues) {
        this.NAME = name;
        this.STARTPATH = start;
        this.PAUSEPATH = pause;
        this.RESUMEPATH = resume;
        this.MODPATH = mod;
        this.FREEPATH = free;
        this.STATUSPATH = status;
        this.BOOTPATH = boot;
        this.REALTIMEPATH = rtpaths;
        this.REALTIMENAME = rtname;
        this.DEFAULTVALUES = defvalues;
        this.controllers = null;
        this.STYLE = SLIDERSTYLE;
    }

    public Sonification(String name, String start, String pause, String resume, String mod, String free, String status, String boot, Controller[] controllers) {
        this.NAME = name;
        this.STARTPATH = start;
        this.PAUSEPATH = pause;
        this.RESUMEPATH = resume;
        this.MODPATH = mod;
        this.FREEPATH = free;
        this.STATUSPATH = status;
        this.BOOTPATH = boot;
        this.controllers = controllers;
        this.STYLE = SLIDERSTYLE;
        this.REALTIMEPATH = null;
        this.REALTIMENAME = null;
        this.DEFAULTVALUES = null;
    }

    public Sonification(String name, String start, String pause, String resume, String mod, String free, String status,
                        String boot) {
        this(name, start, pause, resume, mod, free, status, boot, "", "", null);
    }

    /**
     * Init controllers for realtime modulating of synths.
     *
     * @return 0 if successful, else 1.
     */
    public int initSoundPanel(ControlP5 cp5) {
        if (DEFAULTVALUES == null) {
            System.err.println("CAN NOT INITIALIZE SOUND PANEL: DEFAULT VALUES NULL");
            return 1;
        }
        int XINSET, YINSET;
        if (STYLE == KNOBSTYLE) {
            XINSET = 20;
            YINSET = 15;
        } else if (STYLE == SLIDERSTYLE) {
            XINSET = 5;
            YINSET = 10;
        } else {
            XINSET = 5;
            YINSET = 5;
        }
        // Return if knob does not fit panelsize.
        if (SOUNDCONTROL_W - KNOBSIZE < 2 * XINSET) {
            System.err.println("CAN NOT INITIALIZE SOUND PANEL: KNOB DOES NOT FIT");
            return 1;
        }
        // Return if count of given arguments do match. (2 names but only one path given etc.)
        else if (!(REALTIMENAME.split("~").length == REALTIMEPATH.split("~").length && 3 * REALTIMEPATH.split("~").length == DEFAULTVALUES.length)) {
            System.err.println("CAN NOT INITIALIZE SOUND PANEL: AMOUNT OF NAMES AND PATHS ETC. DO NOT MATCH");
            return 1;
        }
        int modpathcounter = REALTIMEPATH.split("~").length;
        if (STYLE == KNOBSTYLE) {
            /**
             * FIXME
             * THIS SHOULD BE IN ALGOSOUND NOT IN SONIFICATION! -> SONIFICATION OVERHAUL
             */
            // Calculate position of coming controllers.
            Point[] pos = new Point[modpathcounter];
            int x0 = 15;
            int y0 = 10;
            int x = x0;
            int y = y0;
            String[] names = REALTIMENAME.split("~");
            String[] paths = REALTIMEPATH.split("~");
            for (int i = 0; i < pos.length; ++i) {
                if (x >= SOUNDCONTROL_W - KNOBSIZE) {
                    x = x0;
                    y += KNOBSIZE + YINSET;
                }
                pos[i] = new Point(x + AlgosoundUtil.W + GUI_W, y);
                x += KNOBSIZE + XINSET;
            }
            controllers = new OSCKnob[modpathcounter];
            for (int i = 0; i < paths.length; ++i) {

                // MAXFREQ and MINFREQ Knob of SCALE sonificiation need special treatment here.
                boolean isMinfreqKnob = names[i].equals("MINFREQ");
                boolean isMaxfreqKnob = names[i].equals("MAXFREQ");
                int j = i * 3;
                if (isMinfreqKnob || isMaxfreqKnob) {
                    final int index = i;
                    if (isMinfreqKnob) {
                        controllers[i] = (OSCKnob) new OSCKnob(cp5, names[i], paths[i]) {
                            @Override
                            public void fire() {
                                SortingAlgorithm s = (SortingAlgorithm) Algosound.getInstance().getAlgorithm();
                                int value = (int) super.getValue();
                                if (value < s.FREQ_MAX) {
                                    float[] args = {value};
                                    OSC.getInstance().sendMessage(paths[index], args);
                                    s.FREQ_MIN = value;
                                }
                            }
                        };
                    } else {
                        controllers[i] = (OSCKnob) new OSCKnob(cp5, names[i], paths[i]) {
                            @Override
                            public void fire() {
                                SortingAlgorithm s = (SortingAlgorithm) Algosound.getInstance().getAlgorithm();
                                int value = (int) super.getValue();
                                if (value > s.FREQ_MIN) {
                                    float[] args = {value};
                                    OSC.getInstance().sendMessage(paths[index], args);
                                    s.FREQ_MAX = value;
                                }
                            }
                        };
                    }
                    OSCKnob k = (OSCKnob) controllers[i];
                    k.setPosition(pos[i].x, pos[i].y)
                            .setLabel(names[i])
                            .setRadius(KNOBSIZE / 2)
                            .setDragDirection(Knob.HORIZONTAL)
                            .setRange(DEFAULTVALUES[j], DEFAULTVALUES[j + 1])
                            .setValue(DEFAULTVALUES[j + 2]);
                    controllers[i] = k;
                } else {
                    controllers[i] = new OSCKnob(cp5, names[i], paths[i])
                            .setPosition(pos[i].x, pos[i].y)
                            .setLabel(names[i])
                            .setRadius(KNOBSIZE / 2)
                            .setDragDirection(Knob.HORIZONTAL)
                            .setRange(DEFAULTVALUES[j], DEFAULTVALUES[j + 1])
                            .setValue(DEFAULTVALUES[j + 2]);
                }
            }

        } else if (STYLE == SLIDERSTYLE) {
            // Calculate position of coming controllers.
            Point[] pos = new Point[modpathcounter];
            int x0 = XINSET;
            int y0 = YINSET;
            int x = x0;
            int y = y0;
            String[] names = REALTIMENAME.split("~");
            String[] paths = REALTIMEPATH.split("~");
            for (int i = 0; i < pos.length; ++i) {
                if (x >= SOUNDCONTROL_W - SLIDERWIDTH) {
                    x = x0;
                    y += SLIDERHEIGHT + YINSET;
                }
                pos[i] = new Point(x + AlgosoundUtil.W + GUI_W, y);
                int charwidth = 0;
                for (int j = 0; j < names[i].length(); j++) {
                    charwidth += 2;
                }
                x += SLIDERWIDTH + XINSET + charwidth;
            }
            controllers = new OSCSlider[modpathcounter];
            for (int i = 0; i < paths.length; ++i) {
                int j = i * 3;
                controllers[i] = (OSCSlider) new OSCSlider(cp5, names[i], paths[i])
                        .setPosition(pos[i].x, pos[i].y)
                        .setLabel(names[i])
                        .setWidth(SLIDERWIDTH)
                        .setHeight(SLIDERHEIGHT)
                        .setRange(DEFAULTVALUES[j], DEFAULTVALUES[j + 1])
                        .setValue(DEFAULTVALUES[j + 2]);
            }
        } else {
            System.err.println("CAN NOT INITALIZE PANEL: NO STYLE SET");
            return 1;
        }
        return 0;
    }

    // Set style of sonification
    public void setStyle(int x) {
        this.STYLE = x;
    }

    // Reset sliders to default.
    public void reset() {
        int i = 0;
        for (Controller c : controllers) {
            c.setValue(DEFAULTVALUES[(3 * i) + 2]);
            i++;
        }
    }

    // Clear controllers from controlP5-instance thus making room for another sound panel.
    public void clearSoundPanel(ControlP5 cp5) {
        if (controllers != null) {
            for (Controller c : controllers) {
                c.remove();
            }
        }
    }
}
