package algosound.data.audio;

import controlP5.ControlP5;
import controlP5.Knob;
import controlP5.Slider;

/**
 * OSCControllerWrapper. This class is used to wrap the information about audio input controllers
 * so that `Sonification` can pass these to `Algosound` which then draws them on the canvas.
 * ================================
 *
 * First, I wanted to use Generics here but I didn't because:
 * - ControlP5.Knob and ControlP5.Slider do not not inherit `setRange()`
 * --> they are subclasses of Controller<Knob> / Controller<Slider> and not of a common parent
 * - I don't think I really want to change my controller style. I will just stick to Knobs.
 * - I can just put the classes `OSCKnob` and `OSCSlider` inside this class and make them private!
 * - or maybe it was just too hard for me ...
 *
 * See https://github.com/ekzyis/algosound/issues/1 for further information.
 * ================================
 *
 * @author ekzyis
 * @date 26/08/2018
 */
public class OSCControllerWrapper {

    // Name of the controller
    private String name;
    // OSC path of the controller
    private String path;
    // Min and max range
    private float min, max;
    // Default value
    private float def;

    public OSCControllerWrapper(String _name, String _path, float _min, float _max, float _def) {
        this.name = _name;
        this.path = _path;
        this.min = _min;
        this.max = _max;
        assert(_def <= max && min <= def);
        this.def = _def;
    }

    public OSCKnob getKnob(ControlP5 cp5) {
        return (OSCKnob) new OSCKnob(cp5, name, path).setRange(min , max).setDefaultValue(def);
    }

    public OSCSlider getSlider(ControlP5 cp5) {
        return (OSCSlider) new OSCSlider(cp5, name, path).setRange(min, max).setDefaultValue(def);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String _path) {
        this.path = _path;
    }

    /**
     * Interface for controlP5.controllers to do something during a control event.
     * This makes my own controllers easily detectable since they all implement this interface.
     * ================================
     *
     * @author ekzyis
     * @date 01/04/2018
     */
    private interface ControllerInterface {
        // Send an osc message.
        void fire();
    }

     /**
     * This class handles the knobs for real time modulating of the synths.
     * ================================
     *
     * @author ekzyis
     * @date 30/03/2018
     */
    private class OSCKnob extends Knob implements ControllerInterface {
        // Path where msg should be fire to.
        protected final String OSCPATH;

        private OSCKnob(ControlP5 controlP5, String s, String path) {
            super(controlP5, s);
            this.OSCPATH = path;
        }

        @Override
        public void fire() {
            float[] args = {super.getValue()};
            OSC.getInstance().sendMessage(OSCPATH, args);
        }
    }

    /**
     * This class handles the sliders for real time modulating of the synths.
     * ================================
     *
     * @author ekzyis
     * @date 02/04/2018
     */
    private class OSCSlider extends Slider implements ControllerInterface {
        // Path where msg should be fire to.
        protected final String OSCPATH;

        private OSCSlider(ControlP5 controlP5, String s, String path) {
            super(controlP5, s);
            this.OSCPATH = path;
        }

        @Override
        public void fire() {
            float[] args = {super.getValue()};
            OSC.getInstance().sendMessage(OSCPATH, args);
        }
    }
}
