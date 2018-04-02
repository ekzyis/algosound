package algosound.net;

import controlP5.ControlP5;
import controlP5.Slider;

/**
 * This class handles the sliders for realtime modulating of the synths.
 * ================================
 *
 * @author ekzyis
 * @date 02/04/2018
 */
public class OSCSlider extends Slider implements OSCInterface {
    // Path where msg should be send to.
    final String OSCPATH;

    public OSCSlider(ControlP5 controlP5, String s, String path) {
        super(controlP5, s);
        this.OSCPATH = path;
    }

    @Override
    public void send() {
        float[] args = {super.getValue()};
        OSC.getInstance().sendMessage(OSCPATH, args );
    }
}