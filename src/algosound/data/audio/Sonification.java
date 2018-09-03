package algosound.data.audio;

import algosound.data.algorithms.*;
import algosound.ui.Algosound;
import algosound.util.AlgosoundUtil;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Knob;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static algosound.util.AlgosoundUtil.*;

/**
 * Sonification class. This class is used in the implemented algorithms
 * to define the paths for the specific sonifications.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */


public class Sonification {
    // Uniquifies given paths
    private LambdaInterface uniquifyer;
    private interface LambdaInterface {
        String call(String s1);
    }

    private static enum Type {
        WAVE {
            public String getName() {
                return "WAVE";
            }
        },
        SCALE {
            @Override
            public String getName() {
                return "SCALE";
            }
        };
        // Get name of Type
        public abstract String getName();
    }

    public static final Sonification BUBBLESORT_WAVE = new Sonification(
            Type.WAVE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP", "set_amp", 0f, 3f, 0.2f),
                    new OSCControllerWrapper("FREQLAG", "set_freqlag", 0f, 2f, 0.1f),
                    new OSCControllerWrapper("AMPLAG", "set_amplag", 0f, 5f, 0.1f)
            },
            Bubblesort.SUFFIX
    );
    public static final Sonification BUBBLESORT_SCALE = new Sonification(
            Type.SCALE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP", "set_amp", 0f, 0.3f, 0.2f),
                    new OSCControllerWrapper("MINFREQ", "set_MINFREQ", 100f, 8000f, 200f),
                    new OSCControllerWrapper("MAXFREQ", "set_MAXFREQ", 100f, 8000f, 4000f)
            },
            Bubblesort.SUFFIX
    );
    public static final Sonification INSERTIONSORT_WAVE = new Sonification(
            Type.WAVE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp", 0f, 3f, 0.2f),
                    new OSCControllerWrapper("FREQLAG", "set_freqlag", 0f, 2f, 0.1f),
                    new OSCControllerWrapper("AMPLAG","set_amplag",0f,5f,0.1f),
                    new OSCControllerWrapper("PULSEFREQ","pulse_set_freq",1f, 100f, 10f),
                    new OSCControllerWrapper("PULSEAMP","pulse_set_amp",0f,1f,0.2f)
            },
            Insertionsort.SUFFIX
    );
    public static final Sonification INSERTIONSORT_SCALE = new Sonification(
            Type.SCALE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f, 0.3f, 0.2f),
                    new OSCControllerWrapper("MINFREQ","set_MINFREQ",100f,8000f,200f),
                    new OSCControllerWrapper("MAXFREQ","set_MAXFREQ",100f,8000f,4000f)
            },
            Insertionsort.SUFFIX
    );
    public static final Sonification SELECTIONSORT_WAVE = new Sonification(
            Type.WAVE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,3f,0.2f),
                    new OSCControllerWrapper("FREQLAG","set_freqlag",0f,2f,0.1f),
                    new OSCControllerWrapper("AMPLAG","set_amplag",0f,5f,0.1f),
                    new OSCControllerWrapper("MINFREQ","min_set_pulsefreq",0f,10f,0f),
                    new OSCControllerWrapper("MINAMP","min_set_amp",0f,1f,0.3f)
            },
            Selectionsort.SUFFIX
    );
    public static final Sonification SELECTIONSORT_SCALE = new Sonification(
            Type.SCALE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,0.3f,0.2f),
                    new OSCControllerWrapper("MINFREQ","set_MINFREQ",100f,8000f,200f),
                    new OSCControllerWrapper("MAXFREQ","set_MAXFREQ",100f,8000f,4000f)
            },
            Selectionsort.SUFFIX
    );
    public static final Sonification MERGESORT_WAVE = new Sonification(
            Type.WAVE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,3f,0.2f),
                    new OSCControllerWrapper("FREQLAG","set_freqlag",0f,2f,0.1f),
                    new OSCControllerWrapper("AMPLAG","set_amplag",0f,5f,0.1f)
            },
            Mergesort.SUFFIX
    );
    public static final Sonification MERGESORT_SCALE = new Sonification(
            Type.SCALE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,3f,0.2f),
                    new OSCControllerWrapper("MINFREQ","set_MINFREQ",100f,8000f,200f),
                    new OSCControllerWrapper("MAXFREQ","set_MAXFREQ",100f,8000f,4000f)
            },
            Mergesort.SUFFIX
    );
    public static final Sonification QUICKSORT_WAVE = new Sonification(
            Type.WAVE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,3f,0.2f),
                    new OSCControllerWrapper("FREQLAG","set_freqlag",0f,2f,0.1f),
                    new OSCControllerWrapper("AMPLAG","set_freqlag",0f,5f,0.1f)
            },
            Quicksort.SUFFIX,
            new ArrayList<String>(Arrays.asList("set1","set2","set3"))
    );
    public static final Sonification QUICKSORT_SCALE = new Sonification(
            Type.SCALE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,0.3f,0.2f),
                    new OSCControllerWrapper("MINFREQ","set_MINFREQ",100f,8000f,200f),
                    new OSCControllerWrapper("MAXFREQ","set_MAXFREQ",100f,8000f,4000f)
            },
            Quicksort.SUFFIX
    );
    // Name of this sonification and paths for the OSC listeners in SuperCollider.
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, FREEPATH, STATUSPATH, BOOTPATH;
    // Modulating the synths can be multiple paths since sonification can have multiple synths
    public List<String> MODPATHS;
    // Wrappers of the input controllers for the GUI
    private OSCControllerWrapper[] wrappers;

    public Sonification(Type type, OSCControllerWrapper[] wrappers, String suffix) {
        this.NAME = type.getName();
        this.uniquifyer = (String x) -> "/" + NAME.toLowerCase() + "_" + x + "_" + suffix;
        this.STARTPATH = uniquifyer.call("start");
        this.PAUSEPATH = uniquifyer.call("pause");
        this.RESUMEPATH = uniquifyer.call("resume");
        this.MODPATHS = new ArrayList<String>();
        this.MODPATHS.add(uniquifyer.call("set"));
        this.FREEPATH = uniquifyer.call("free");
        this.STATUSPATH = uniquifyer.call("hello");
        this.BOOTPATH = uniquifyer.call("boot");
        for(int i=0; i<wrappers.length; ++i) {
            wrappers[i].setPath(uniquifyer.call(wrappers[i].getPath()));
        }
        this.wrappers = wrappers;
    }

    public Sonification(Sonification.Type type, OSCControllerWrapper[] wrappers, String suffix, List<String> modpaths) {
        this(type,wrappers,suffix);
        this.MODPATHS = modpaths;
        for(int i=0; i<MODPATHS.size(); ++i) {
            MODPATHS.set(i, uniquifyer.call(MODPATHS.get(i)));
        }
    }

    /**
     * Init controllers for realtime modulating of synths.
     *
     * @return 0 if successful, else 1.
     */
    /*
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
            // FIXME
            // THIS SHOULD BE IN ALGOSOUND NOT IN SONIFICATION! -> SONIFICATION OVERHAUL
            //
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
            System.err.println("CAN NOT INITIALIZE PANEL: NO STYLE SET");
            return 1;
        }
        return 0;
    }
    */

    // Reset controllers to default.
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
