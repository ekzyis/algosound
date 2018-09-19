package algosound.data.algorithms;

import algosound.data.audio.Sonification;
import algosound.data.visuals.Visual;

/**
 * Interface for algorithms.
 * All implementation of algorithms have to implement this interface to guarantee they can be visualized and listened to.
 * ================================
 *
 * @author ekzyis
 * @date 10/04/2018
 */
public interface Algorithm {
    // Name of algorithm
    String getString();

    // Suffix of algorithm
    String getSuffix();

    // Start the algorithm
    void start();

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
    void resumeAlgorithm();

    // Should algorithm exit?
    boolean isExiting();

    // Start exit sequence.
    void exit();

    // Should algorithm wait due to fps reasons?
    boolean isWaitingDueToFPS();

    // Get the elements for drawing.
    Visual[] getVisuals();

    // Reinitialize algorithm.
    Algorithm reset();

    // Return selected sonification.
    Sonification getSelectedSonification();

    // Change sonification
    void changeSonification();

    // Is thread alive?
    boolean isAlive();

    // Wait for thread to die.
    void join() throws InterruptedException;

}
