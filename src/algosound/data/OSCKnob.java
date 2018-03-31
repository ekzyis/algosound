package algosound.data;

import algosound.net.OSC;
import controlP5.ControlP5;
import controlP5.Knob;

/**
 * This class handles the knobs for realtime modulating of the synths.
 * ================================
 *
 * @author ekzyis
 * @date 30/03/2018
 */
public class OSCKnob extends Knob {
    final String OSCPATH;

    public OSCKnob(ControlP5 controlP5, String s, String path) {
        super(controlP5, s);
        this.OSCPATH = path;
    }

    public void fire() {
        System.out.println("VALUE: " + super.getValue());
        float[] args = {super.getValue()};
        OSC.getInstance().sendMessage(OSCPATH, args );
    }
}
