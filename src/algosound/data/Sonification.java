package algosound.data;

import algosound.util.AlgosoundUtil;
import controlP5.ControlP5;
import controlP5.Knob;

import java.awt.*;
import java.util.List;

import static algosound.util.AlgosoundUtil.*;
import static algosound.util.AlgosoundUtil.KNOBSIZE;
import static algosound.util.AlgosoundUtil.SELECTED_ALGORITHM;

/**
 * Sonification-class. This class is used in the implemented algorithms
 * to define the paths for the specific sonifications.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class Sonification {
    // Name of this sonification and paths for the osc listeners.
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, MODPATH, FREEPATH, STATUSPATH, BOOTPATH, REALTIMEPATH, REALTIMENAME;
    // Default values of knobs. Three floats per path/name: min, max and default value.
    float[] DEFAULTVALUES;
    public Sonification(String name, String start, String pause, String resume, String mod, String free, String status,
            String boot, String realtime, String rtname, float[] defvalues) {
        this.NAME = name;
        this.STARTPATH = start;
        this.PAUSEPATH = pause;
        this.RESUMEPATH = resume;
        this.MODPATH = mod;
        this.FREEPATH = free;
        this.STATUSPATH = status;
        this.BOOTPATH = boot;
        this.REALTIMEPATH = realtime;
        this.REALTIMENAME = rtname;
        this.DEFAULTVALUES = defvalues;
    }

    public Sonification(String name, String start, String pause, String resume, String mod, String free, String status,
                        String boot) {
        this(name,start,pause,resume,mod,free,status,boot,"","", null);
    }

    /**
     * Init knobs for realtime modulating of synths.
     * @return 0 if successful, else 1.
     */

    public int initSoundPanel(ControlP5 cp5) {
        if(DEFAULTVALUES == null)
        {
            System.err.println("CAN NOT INITIALIZE SOUND PANEL: DEFAULT VALUES NULL");
        }
        final int XINSET = 5;
        final int YINSET = 20;
        // Return if knob does not fit panelsize.
        if(SOUNDCONTROL_W - KNOBSIZE < 2*XINSET) {
            System.err.println("CAN NOT INITIALIZE SOUND PANEL: KNOB DOES NOT FIT");
            return 1;
        }
        // Return if count of given arguments do match. (2 names but only one path given etc.)
        else if (!(REALTIMENAME.split("~").length == REALTIMEPATH.split("~").length && 3*REALTIMEPATH.split("~").length == DEFAULTVALUES.length))
        {
            System.err.println("CAN NOT INITIALIZE SOUND PANEL: AMOUNT OF NAMES AND PATHS ETC. DO NOT MATCH");
            return 1;
        }
        int modpathcounter = REALTIMEPATH.split("~").length;
        int x0 = XINSET;
        int y0 = 10;
        // Calculate position of coming knobs.
        Point[] pos = new Point[modpathcounter];
        int x = x0;
        int y = y0;
        for(int i = 0; i<pos.length; ++i) {
            if(x >= SOUNDCONTROL_W - KNOBSIZE) {
                x = x0;
                y += KNOBSIZE + YINSET;
            }
            pos[i] = new Point(x+ AlgosoundUtil.W+GUI_W,y);
            System.out.println(pos[i]);
            x += KNOBSIZE + XINSET;
        }
        OSCKnob[] knobs = new OSCKnob[modpathcounter];
        String[] names = REALTIMENAME.split("~");
        String[] paths = REALTIMEPATH.split("~");
        for(int i=0;i<paths.length;++i) {
            int j = i*3;
            knobs[i] = (OSCKnob) new OSCKnob(cp5, names[i], paths[i])
                    .setPosition(pos[i].x,pos[i].y)
                    .setLabel(names[i])
                    .setRadius(KNOBSIZE/2)
                    .setDragDirection(Knob.HORIZONTAL)
                    .setRange(DEFAULTVALUES[j], DEFAULTVALUES[j+1])
                    .setValue(DEFAULTVALUES[j+2]);
            System.out.println(DEFAULTVALUES[j] + " " + DEFAULTVALUES[j+1] + " " + DEFAULTVALUES[j+2]);
        }
        return 0;
    }
}
