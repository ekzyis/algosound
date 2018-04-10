package algosound.data.algorithms;

import algosound.data.Visual;

/**
 * ---INSERT DESCRIPTION HERE---
 * ================================
 *
 * @author ekzyis
 * @date 10/04/2018
 */
public interface AnimatedAlgorithm {

    // Name of algorithm
    String getString();
    // Is frame ready for drawing?
    boolean frameIsReady();
    // Has frame been drawn?
    boolean frameIsDrawn();
    // Notify animation thread that frame is ready.
    void notifyFrameReady();
    // Get notified that frame has been drawn.
    void notifyFrameDraw();
    // Is algorithm currently paused?
    boolean isPaused();
    // Pause the algorithm.
    void pause();
    // Resume the algorithm
    void resume();
    // Should algorithm exit?
    boolean isExiting();
    // Start exit sequence.
    void exit();
    // Should algorithm wait due to fps reasons?
    boolean isWaitingDueToFPS();
    // Get the elements for drawing.
    Visual[] getVisuals();

}
