package algosound.data;

import algosound.net.OSCKnob;
import algosound.net.OSCSlider;
import algosound.util.AlgosoundUtil;
import controlP5.ControlP5;
import controlP5.ControlP5Constants;
import controlP5.Controller;
import controlP5.Knob;

import java.awt.*;

import static algosound.util.AlgosoundUtil.*;
import static algosound.util.AlgosoundUtil.KNOBSIZE;

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

    public Sonification(String name, String start, String pause, String resume, String mod, String free, String status,
                        String boot) {
        this(name,start,pause,resume,mod,free,status,boot,"","", null);
    }

    /**
     * Init controllers for realtime modulating of synths.
     * @return 0 if successful, else 1.
     */
    public int initSoundPanel(ControlP5 cp5) {
        if(DEFAULTVALUES == null)
        {
            System.err.println("CAN NOT INITIALIZE SOUND PANEL: DEFAULT VALUES NULL");
            return 1;
        }
        int XINSET, YINSET;
        if(STYLE==0) {
            XINSET = 15;
            YINSET = 15;
        }
        else if(STYLE==1) {
            XINSET = 5;
            YINSET = 10;
        }
        else {
            XINSET = 5;
            YINSET = 5;
        }
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
        if(STYLE==KNOBSTYLE) {
            // Calculate position of coming controllers.
            Point[] pos = new Point[modpathcounter];
            int x0 = 15;
            int y0 = 10;
            int x = x0;
            int y = y0;
            String[] names = REALTIMENAME.split("~");
            String[] paths = REALTIMEPATH.split("~");
            for(int i = 0; i<pos.length; ++i) {
                if(x >= SOUNDCONTROL_W - KNOBSIZE) {
                    x = x0;
                    y += KNOBSIZE + YINSET;
                }
                pos[i] = new Point(x+ AlgosoundUtil.W+GUI_W,y);
                System.out.println(pos[i]);
                x += KNOBSIZE + XINSET;
            }
            controllers = new OSCKnob[modpathcounter];

            for(int i=0;i<paths.length;++i) {
                int j = i*3;
                controllers[i] = (OSCKnob) new OSCKnob(cp5, names[i], paths[i])
                        .setPosition(pos[i].x,pos[i].y)
                        .setLabel(names[i])
                        .setRadius(KNOBSIZE/2)
                        .setDragDirection(Knob.HORIZONTAL)
                        .setRange(DEFAULTVALUES[j], DEFAULTVALUES[j+1])
                        .setValue(DEFAULTVALUES[j+2]);
                System.out.println(DEFAULTVALUES[j] + " " + DEFAULTVALUES[j+1] + " " + DEFAULTVALUES[j+2]);
            }
        }
        else if(STYLE==SLIDERSTYLE) {
            // Calculate position of coming controllers.
            Point[] pos = new Point[modpathcounter];
            int x0 = XINSET;
            int y0 = YINSET;
            int x = x0;
            int y = y0;
            String[] names = REALTIMENAME.split("~");
            String[] paths = REALTIMEPATH.split("~");
            for(int i = 0; i<pos.length; ++i) {
                if(x >= SOUNDCONTROL_W - SLIDERWIDTH) {
                    x = x0;
                    y += SLIDERHEIGHT + YINSET;
                }
                pos[i] = new Point(x+ AlgosoundUtil.W+GUI_W,y);
                int charwidth = 0;
                for(int j=0;j<names[i].length(); j++) {
                    charwidth += 2;
                }
                System.out.println(pos[i]);
                x += SLIDERWIDTH + XINSET  + charwidth;
            }
            controllers = new OSCSlider[modpathcounter];
            for(int i=0;i<paths.length;++i) {
                int j = i*3;
                controllers[i] = (OSCSlider) new OSCSlider(cp5, names[i], paths[i])
                        .setPosition(pos[i].x,pos[i].y)
                        .setLabel(names[i])
                        .setWidth(SLIDERWIDTH)
                        .setHeight(SLIDERHEIGHT)
                        .setRange(DEFAULTVALUES[j], DEFAULTVALUES[j+1])
                        .setValue(DEFAULTVALUES[j+2]);
            }
        }
        else {
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
        for(Controller c : controllers) {
            c.setValue(DEFAULTVALUES[(3*i)+2]);
            i++;
        }
    }

    // Clear controllers from controlP5-instance thus making room for another sound panel.
    public void clearSoundPanel(ControlP5 cp5) {
        if(controllers !=null) {
            for(Controller c : controllers) {
                c.remove();
            }
        }
    }
}
