package algosound.data.audio;

/**
 * Interface for controlP5.controllers to do something during a control event.
 * This makes my own controllers easily detectable since they all implement this interface.
 * ================================
 *
 * @author ekzyis
 * @date 01/04/2018
 */
public interface ControllerInterface {
    // Send an osc message.
    public abstract void fire();
}
