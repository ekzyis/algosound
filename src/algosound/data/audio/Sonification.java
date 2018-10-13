package algosound.data.audio;

import algosound.data.algorithms.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
            @Override
            String getName() {
                return "WAVE";
            }
        },
        SCALE {
            @Override
            String getName() {
                return "SCALE";
            }
        };
        abstract String getName();
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
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MINFREQ,100f,8000f,200f),
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MAXFREQ,100f,8000f,4000f)
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
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MINFREQ,100f,8000f,200f),
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MAXFREQ,100f,8000f,4000f)
            },
            Insertionsort.SUFFIX
    );
    public static final Sonification SELECTIONSORT_WAVE = new Sonification(
            Type.WAVE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,3f,0.2f),
                    new OSCControllerWrapper("FREQLAG","set_freqlag",0f,2f,0.1f),
                    new OSCControllerWrapper("AMPLAG","set_amplag",0f,5f,0.1f),
                    new OSCControllerWrapper("MINAMP","min_set_amp",0f,1f,0.3f)
            },
            Selectionsort.SUFFIX,
            new ArrayList<String>(Arrays.asList("set","min_set"))
    );
    public static final Sonification SELECTIONSORT_SCALE = new Sonification(
            Type.SCALE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,0.3f,0.2f),
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MINFREQ,100f,8000f,200f),
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MAXFREQ,100f,8000f,4000f)
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
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MINFREQ,100f,8000f,200f),
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MAXFREQ,100f,8000f,4000f)
            },
            Mergesort.SUFFIX
    );
    public static final Sonification QUICKSORT_WAVE = new Sonification(
            Type.WAVE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,3f,0.2f),
                    new OSCControllerWrapper("FREQLAG","set_freqlag",0f,2f,0.1f),
                    new OSCControllerWrapper("AMPLAG","set_amplag",0f,5f,0.1f)
            },
            Quicksort.SUFFIX,
            new ArrayList<String>(Arrays.asList("set1","set2","set3"))
    );
    public static final Sonification QUICKSORT_SCALE = new Sonification(
            Type.SCALE,
            new OSCControllerWrapper[]{
                    new OSCControllerWrapper("AMP","set_amp",0f,0.3f,0.2f),
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MINFREQ,100f,8000f,200f),
                    new OSCFreqControllerWrapper(OSCFreqControllerWrapper.Type.MAXFREQ,100f,8000f,4000f)
            },
            Quicksort.SUFFIX
    );
    // Name of this sonification and paths for the OSC listeners in SuperCollider.
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, FREEPATH, STATUSPATH, BOOTPATH;
    // Modulating the synths can be multiple paths since sonification can have multiple synths
    public List<String> MODPATHS;
    // Wrappers of the input controllers for the GUI
    private OSCControllerWrapper[] wrappers;

    /**
     * Constructs a Sonification instance. (no, really?)
     *
     * Uses the lambda interface to uniquify all given mod paths.
     * Uses default paths for starting, resuming, pausing, and more
     * to prevent redundant code and make maintaining this code easier.
     *
     * @param type          Type of instance (used to uniquify paths)
     * @param wrappers      controllers for user input
     * @param suffix        suffix of Algorithm for which this sonification is created (used to uniquify paths)
     */
    private Sonification(Type type, OSCControllerWrapper[] wrappers, String suffix) {
        this.NAME = type.getName();
        this.uniquifyer = (String x) -> "/" + NAME.toLowerCase() + "_" + x + "_" + suffix;
        this.STARTPATH = uniquifyer.call("start");
        // SCALE type does not have PAUSE, RESUME or FREE path
        if(type == Type.SCALE) {
            this.PAUSEPATH = null;
            this.RESUMEPATH = null;
            this.FREEPATH = null;
        }
        else {
            this.PAUSEPATH = uniquifyer.call("pause");
            this.RESUMEPATH = uniquifyer.call("resume");
            this.FREEPATH = uniquifyer.call("free");
        }
        this.MODPATHS = new ArrayList<String>();
        this.MODPATHS.add(uniquifyer.call("set"));
        this.STATUSPATH = uniquifyer.call("hello");
        this.BOOTPATH = uniquifyer.call("boot");
        for(int i=0; i<wrappers.length; ++i) {
            wrappers[i].setPath(uniquifyer.call(wrappers[i].getPath()));
        }
        this.wrappers = wrappers;

    }

    /**
     * Uses upper constructor.
     * Used when an Algorithm wants to use multiple mod paths.
     *
     * @param type          Type of instance (used to uniquify paths)
     * @param wrappers      controllers for user input
     * @param suffix        suffix of Algorithm for which this sonification is created (used to uniquify paths)
     * @param modpaths      multiple mod paths inside a List
     */
    private Sonification(Sonification.Type type, OSCControllerWrapper[] wrappers, String suffix, List<String> modpaths) {
        this(type,wrappers,suffix);
        this.MODPATHS = modpaths;
        for(int i=0; i<MODPATHS.size(); ++i) {
            MODPATHS.set(i, uniquifyer.call(MODPATHS.get(i)));
        }
    }

    public OSCControllerWrapper[] getWrappers() {
        return wrappers;
    }

    // Return all used OSC paths
    public String[] getPaths() {
        ArrayList<String> paths = new ArrayList<>();
        String[] defaultpaths = new String[]{STARTPATH, PAUSEPATH, RESUMEPATH, FREEPATH, STATUSPATH, BOOTPATH};
        for(String p : defaultpaths) {
            // Default paths can be null when they are not used
            if(p!=null) {
                paths.add(p);
            }
        }
        for(String p : MODPATHS) {
            paths.add(p);
        }
        for(OSCControllerWrapper w : wrappers)  {
            paths.add(w.getPath());
        }
        return paths.toArray(new String[paths.size()]);
    }
}
