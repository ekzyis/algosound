package algosound.ui;

import algosound.data.audio.OSCControllerWrapper;
import algosound.data.audio.OSCControllerWrapper.*;
import algosound.data.visuals.Visual;
import algosound.data.algorithms.Algorithm;
import algosound.data.audio.OSC;
import algosound.util.AlgosoundUtil;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Button;
import controlP5.Controller;
import processing.core.PApplet;

import java.awt.*;

import static algosound.util.AlgosoundUtil.*;

/**
 * Mainfile of algosound project. This is a singleton class.
 * Base class for the program as PApplet to run Processing code.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class Algosound extends PApplet {

    private static Algosound instance;
    private Algorithm algorithm;
    // `GUI`-instance
    private ControlP5 cp5;
    private Button START;
    private Button EXIT;
    private Button RESET;
    private Button SONI;
    private Button ALGO;
    private Controller SPEED;
    // Audio controllers
    private Controller[] controllers;

    @Override
    public void settings() {
        size(AlgosoundUtil.W + AlgosoundUtil.GUI_W + AlgosoundUtil.SOUNDCONTROL_W, AlgosoundUtil.H + INFO_H);
    }

    @Override
    public void setup() {
        frameRate(AlgosoundUtil.FRAMERATE);
        algorithm = AlgosoundUtil.SELECTED_ALGORITHM;
        initGUI();
        Algosound.getInstance().getSurface().setResizable(true);
    }

    public void initGUI() {
        // Initialize the graphical user interface.
        cp5 = new ControlP5(this);

        // Create the y-coordinates for the buttons and save them in an array.
        Button.autoWidth = 65;
        Button.autoHeight = 25;
        int yInset = 10;
        int len = (int) ((AlgosoundUtil.H + INFO_H) / (yInset + Button.autoHeight));
        int[] yPos = new int[len];
        int y0 = yInset + Button.autoHeight;
        int x0 = AlgosoundUtil.W + 10;
        for (int i = 0; i < len; ++i) {
            yPos[i] = (i + 1) * y0 - Button.autoHeight;
        }
        START = cp5.addButton("start/pause").setPosition(x0, yPos[0]).setLabel("Start");
        /**
         * Naming the button like the exit()-function triggers the function when
         * pressing thus no need of defining a if-Statement for this button in
         * controlEvent().
         */
        EXIT = cp5.addButton("exit").setPosition(x0, yPos[len - 1]).setLabel("Exit");
        RESET = cp5.addButton("reset").setPosition(x0, yPos[1]).setLabel("Reset");
        SONI = cp5.addButton("change").setPosition(x0, yPos[2]).setLabel(algorithm.getSelectedSonification().NAME);
        ALGO = cp5.addButton("algo").setPosition(x0, yPos[3]).setLabel("ALGO");
        // Initialize the controller for algorithm speed.
        SPEED = cp5.addSlider("algofps").setPosition(x0, yPos[4]).setLabel("FPS").setWidth(45).setRange(1f, 1000f).setValue(FRAMERATE);

        // Init the sound panel of selected sonification
        initSoundPanel();
    }

    private void initSoundPanel() {
        OSCControllerWrapper[] wrappers = algorithm.getSelectedSonification().getWrappers();
        // Get the knobs from wrapper
        controllers = new OSCKnob[wrappers.length];
        for(int i=0; i<controllers.length; ++i) {
            controllers[i] = wrappers[i].getKnob(cp5);
        }
        final int INSET_X = 20, INSET_Y = 18;
        // Set locations for knobs
        int x0 = INSET_X;
        int y0 = 10;
        int x = x0;
        int y = y0;
        for(int i=0; i<controllers.length; ++i) {
            //System.out.println("before check: " + x + ", " + y);
            if(x > SOUNDCONTROL_W - KNOBSIZE - INSET_X ) {
                x = x0;
                y += KNOBSIZE + INSET_Y;
            }
            //System.out.println("after check: " + x + ", " + y);
            controllers[i].setPosition(W + GUI_W + x,y);
            x += KNOBSIZE + INSET_X;
        }
    }

    private void resetSoundPanel() {
        OSCControllerWrapper[] wrappers = algorithm.getSelectedSonification().getWrappers();
        assert(controllers.length == wrappers.length);
        for(int i=0; i<controllers.length; ++i) {
            controllers[i].setValue(wrappers[i].getDefaultValue());
        }
    }

    private void clearSoundPanel() {
        for(int i=0; i<controllers.length; ++i) {
            controllers[i].remove();
        }
    }

    /**
     * Eventhandling of user interface.
     * TODO: ---Bugfix#2 When immediately pressing a button after opening the sketch, a InvocationTargetException is thrown but sketch keeps running fine after that.
     */
    public void controlEvent(ControlEvent event) {
        Controller c = event.getController();
        if (c == START) {
            String currentLabel = c.getLabel();
            // Do action corresponding to current label.
            switch (currentLabel) {
                case "Start":
                    /**
                     * Did thread already start? If not, start it. (Execution never reaches this
                     * statement when it would be false since the label will never be again "Start"
                     * so it's actually unnecessary.)
                     */
                    if (!algorithm.isAlive()) {
                        // println("---starting audio");
                        algorithm.start();
                        // Lock selected sonification.
                        SONI.lock();
                        // Lock selected algorithm.
                        ALGO.lock();
                    }
                    c.setLabel("Pause");
                    break;
                case "Pause":
                    // println("---pause audio");
                    algorithm.pause();
                    c.setLabel("Resume");
                    break;
                case "Resume":
                    // println("---resume audio");
                    algorithm.resumeAlgorithm();
                    c.setLabel("Pause");
                    break;
            }
            // ### RESET
        } else if (c == RESET) {
            START.setLabel("Start");
            System.out.println("--- algorithm: reset");
            OSC.getInstance().sendMessage(algorithm.getSelectedSonification().FREEPATH);
            algorithm = algorithm.reset();
            // Unlock selection of sonifications.
            SONI.unlock();
            // Unlock selection of algorithms.
            ALGO.unlock();
            // Reset framerate slider
            FRAMERATE = PREFERRED_FRAMERATE;
            SPEED.setValue(PREFERRED_FRAMERATE);
            ALGORITHMFPS = PREFERRED_FRAMERATE;
            frameRate(PREFERRED_FRAMERATE);
            // Reset sonification sound panel.
            resetSoundPanel();
        } else if (c == SONI && !algorithm.isAlive()) {
            clearSoundPanel();
            algorithm.changeSonification();
            initSoundPanel();
            SONI.setLabel(algorithm.getSelectedSonification().NAME);
        } else if (c == ALGO && !algorithm.isAlive()) {
            clearSoundPanel();
            AlgosoundUtil.changeAlgorithm();
            algorithm = SELECTED_ALGORITHM;
            // Also update the label of the sonification button
            SONI.setLabel(algorithm.getSelectedSonification().NAME);
            // Init sound panel
            initSoundPanel();
        } else if (c == SPEED) {
            // Only change framerate of sorting! Don't change framerate of actual redrawing.
            ALGORITHMFPS = c.getValue();
            if (ALGORITHMFPS > FRAMERATE ||
                    (ALGORITHMFPS < FRAMERATE && ALGORITHMFPS >= PREFERRED_FRAMERATE)) {
                frameRate(ALGORITHMFPS);
                FRAMERATE = (int) (ALGORITHMFPS);
            } else if (ALGORITHMFPS < PREFERRED_FRAMERATE) {
                frameRate(PREFERRED_FRAMERATE);
                FRAMERATE = PREFERRED_FRAMERATE;
            }
            // Not needed. Added nonetheless.
        } else if (c == EXIT) {
            exit();
        } else if (c instanceof ControllerInterface) {
            ControllerInterface i = (ControllerInterface) c;
            i.fire();
        }
    }

    @Override
    public void draw() {
        synchronized (algorithm) {
            background(25);
            if (algorithm.isAlive() && !algorithm.isPaused() && !algorithm.isExiting() && !algorithm.isWaitingDueToFPS()) {
                // Wait until new frame is ready.
                while (!algorithm.frameIsReady()) {
                    try {
                        algorithm.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            translate(0, INFO_H);
            for (Visual v : algorithm.getVisuals()) {
                v.show();
            }
            if (algorithm.isPaused()) {
                drawPause();
            }
            translate(0, -INFO_H);
            drawInfo();
            /**
             * Notify algorithm thread that frame has been drawn.
             */
            if (algorithm.isAlive() && !algorithm.isPaused() && !algorithm.isWaitingDueToFPS()) {
                algorithm.notifyFrameDraw();
                algorithm.notify();
            }
        }
    }

    // Needed for drawing of pause.
    private int phaseIndex = 0;
    private Color[] pauseColors = new Color[]{
            new Color(255, 0, 0),
            new Color(0, 255, 0),
            new Color(0, 0, 255),
            new Color(128, 128, 0),
            new Color(0, 128, 128),
            new Color(128, 0, 128),
            new Color(64, 255, 128),
            new Color(255, 128, 64),
            new Color(128, 64, 255)
    };
    private Color pauseColor = pauseColors[0];

    private void drawPause() {
        fill(25, 200);
        noStroke();
        rect(0, 0, AlgosoundUtil.W, AlgosoundUtil.H);
        // Every 5 frames, the color of the pause symbol changes randomly.
        if (frameCount % 5 == 0) {
            phaseIndex = (int) random(0, pauseColors.length);
            pauseColor = pauseColors[phaseIndex];
        }
        fill(pauseColor);
        int centerx = (AlgosoundUtil.W / 2);
        int centery = (AlgosoundUtil.H / 2);
        stroke(0);
        strokeWeight(1);
        noStroke();
        rectMode(CENTER);
        rect(centerx - 12, centery - 20, 12, 40);
        rect(centerx + 12, centery - 20, 12, 40);
        rectMode(CORNER);
        textAlign(CENTER, CENTER);
        text("Paused", centerx, centery + 12);
        textAlign(CORNER, CORNER);
    }

    private void drawInfo() {
        drawIPCStatus();
        drawCurrentSortAlgoName();
        drawNumberOfElements();
    }

    private void drawNumberOfElements() {
        fill(255);
        text("Number of elements: " + AlgosoundUtil.N, 240, 15);
    }

    private void drawCurrentSortAlgoName() {
        fill(255);
        text(algorithm.getString(), 120, 15);
    }

    // Draw status of IPC.
    private void drawIPCStatus() {
        ellipseMode(CENTER);
        noStroke();
        fill(255);
        ellipse(10, 10, 10, 10);
        String infolabel = "sc3 server";
        Rectangle inforec = new Rectangle(10,5,(int)(10+textWidth(infolabel)), 10);
        // Draw more info when mouse is over icon
        if(mouseOver(inforec)) {
            ellipse(10,25,10,10);
        }
        if (OSC.getInstance().getStatus()) {
            fill(0, 255, 0);
        } else {
            fill(255, 0, 0);
        }
        text(infolabel, 20, 15);
        ellipse(10, 10, 8, 8);
        stroke(0);
    }

    // Check if mouse is over circle
    private boolean mouseOver(int x, int y, int diameter) {
        float disX = x - mouseX;
        float disY = y - mouseY;
        if(sqrt(sq(disX) + sq(disY)) < diameter/2 ) {
            return true;
        } else {
            return false;
        }
    }
    // Check if mouse is over rectangle
    private boolean mouseOver(Rectangle rec) {
        if (mouseX >= rec.x && mouseX <= rec.x+rec.width &&
                mouseY >= rec.y && mouseY <= rec.y+rec.height) {
            return true;
        } else {
            return false;
        }
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    // This function is called during exit.
    @Override
    public void exit() {
        // Exit the sorting thread using its own implemented exit-method.
        algorithm.exit();
        /**
         * Interrupt status-thread which checks connection between OSC and sc3-server.
         * This will terminate the status-thread in a clean way.
         * TODO: Set status-thread as daemon thread so JVM will exit even when status is still running.
         * UPDATE:
         * If thread should be terminated by JVM when no other none-daemon-threads are
         * running, a NullPointer because of the OSC-instance is thrown. This leads to a
         * decision between using daemon thread convenience but needing a if-statement
         * (osc!=null) in status-thread or interrupting and waiting for termination.
         * STATUS: not decided yet.
         */
        OSC.getInstance().getStatusThread().interrupt();
        try {
            // Wait for threads to terminate.
            algorithm.join();
            OSC.getInstance().getStatusThread().join();
        } catch (Exception e) {
        }
        // Close OSC after execution to prevent blocking of OSC_PORT.
        OSC.getInstance().dispose();
        // Call exit() of PApplet to properly exit this sketch.
        super.exit();
    }

    public static Algosound getInstance() {
        if (instance == null) {
            instance = new Algosound();
        }
        return instance;
    }

    public ControlP5 getCP5() {
        return cp5;
    }

    // PApplet.fill() method modified for java.awt.Color
    public void fill(java.awt.Color c) {
        fill(c.getRed(), c.getGreen(), c.getBlue());
    }

    public static void main(String[] passedArgs) {
        // Create a sketch
        String[] processingArgs = {"Algosound"};
        Algosound inst = Algosound.getInstance();
        PApplet.runSketch(processingArgs, inst);
    }
}
