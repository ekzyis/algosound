package algosound.data.audio;

import controlP5.Controller;

/**
 * OSCControllerWrapper. This class is used to wrap the information about audio input controllers
 * so that `Sonification` can pass these to `Algosound` which then draws them on the canvas.
 *
 * See https://github.com/ekzyis/algosound/issues/1 for further information.
 * ================================
 *
 * @author ekzyis
 * @date 14/08/2018
 */
public class OSCControllerWrapper {
    // type is a class which extends Controller of ControlP5.
    private Class<? extends Controller> type;
    // Name of the controller
    private String name;
    // OSC path of the controller
    private String path;
}
