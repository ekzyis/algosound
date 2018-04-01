package algosound.ui;

import algosound.data.OSCKnob;
import algosound.data.algorithms.SortingThread;
import algosound.data.Element;
import algosound.net.OSC;
import algosound.util.AlgosoundUtil;
import controlP5.*;
import controlP5.Button;
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
    private SortingThread sort;
    // `GUI`-instance
    private ControlP5 cp5;
    private Button START;
    private Button EXIT;
    private Button RESET;
    private Button SONI;
    private Button ALGO;
    private OSCKnob[] knobs;
    @Override
    public void settings() {
        size(AlgosoundUtil.W + AlgosoundUtil.GUI_W + AlgosoundUtil.SOUNDCONTROL_W, AlgosoundUtil.H+INFO_H);
    }

    @Override
    public void setup() {
        frameRate(AlgosoundUtil.FRAMERATE);
        sort = AlgosoundUtil.SELECTED_ALGORITHM.getInstance();
        initGUI();
        Algosound.getInstance().getSurface().setResizable(true);
    }

    public void initGUI() {
        // Initialize the graphical user interface.
        cp5 = new ControlP5(this);// Create the y-coordinates for the buttons and save them in an array.
        Button.autoWidth = 50;
        Button.autoHeight = 20;
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
        SONI = cp5.addButton("change").setPosition(x0, yPos[2]).setLabel(sort.getSelectedSonification().NAME);
        ALGO = cp5.addButton("algo").setPosition(x0, yPos[3]).setLabel("ALGO");
        // Init the sound panel of selected sonification
        SELECTED_ALGORITHM.getInstance().getSelectedSonification().initSoundPanel(cp5);
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
            if (currentLabel.equals("Start")) {
                /**
                 * Did thread already start? If not, start it. (Execution never reaches this
                 * statement when it would be false since the label will never be again "Start"
                 * so it's actually unnecessary.)
                 */
                if (!sort.isAlive()) {
                    // println("---starting audio");
                    sort.start();
                    // Lock selected sonification.
                    SONI.lock();
                    // Lock selected algorithm.
                    ALGO.lock();
                }
                c.setLabel("Pause");
            } else if (currentLabel.equals("Pause")) {
                // println("---pause audio");
                sort.pause();
                c.setLabel("Resume");
            } else if (currentLabel.equals("Resume")) {
                // println("---resume audio");
                sort.unpause();
                c.setLabel("Pause");
            }
        } else if (c == RESET) {
            START.setLabel("Start");
            System.out.println("--- sort: reset");
            OSC.getInstance().sendMessage(sort.getSelectedSonification().FREEPATH);
            sort = SELECTED_ALGORITHM.getNewInstance();
            // Unlock selection of sonifications.
            SONI.unlock();
            // Unlock selection of algorithms.
            ALGO.unlock();
        } else if (c == SONI && !sort.isAlive()) {
            sort.getSelectedSonification().clearSoundPanel(cp5);
            sort.changeSonification();
            sort.getSelectedSonification().initSoundPanel(cp5);
            SONI.setLabel(sort.getSelectedSonification().NAME);
        }
        else if (c == ALGO && !sort.isAlive()) {
            AlgosoundUtil.changeAlgorithm();
            sort = SELECTED_ALGORITHM.getInstance();
            // Also update the label of the sonification button
            SONI.setLabel(sort.getSelectedSonification().NAME);
        }
        // Controller is a knob. Let it send its value to its osc path.
        else if(c.getClass() == OSCKnob.class)
        {
            OSCKnob knob = (OSCKnob) c;
            knob.fire();
        }
    }

    @Override
    public void draw() {
        synchronized (sort) {
            background(25);
            if (sort.isAlive() && !sort.isPaused() && !sort.isExiting()) {
                // Wait until new frame is ready.
                while (!sort.frameIsReady()) {
                    try {
                        sort.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            translate(0, INFO_H);
            for (Element e : sort.getElements()) {
                e.show();
            }
            translate(0, -INFO_H);
            drawInfo();
            drawSoundPanel();
            /**
             * Notify sort thread that frame has been drawn.
             */
            if (sort.isAlive() && !sort.isPaused()) {
                sort.notifyFrameDraw();
                sort.notify();
            }
        }
    }

    private void drawInfo() {
        drawIPCStatus();
        drawCurrentSortAlgoName();
        drawNumberOfElements();
    }

    private void drawNumberOfElements() {
        fill(255);
        text("Number of elements: "+AlgosoundUtil.N, 240, 15);
    }

    private void drawCurrentSortAlgoName() {
        fill(255);
        text(sort.getString(), 120, 15);
    }

    // Draw status of IPC.
    private void drawIPCStatus() {
        ellipseMode(CENTER);
        noStroke();
        fill(255);
        ellipse(10, 10, 10, 10);
        if (OSC.getInstance().getStatus()) {
            fill(0, 255, 0);
        } else {
            fill(255, 0, 0);
        }
        text("SC3-server", 20, 15);
        ellipse(10, 10, 8, 8);
        stroke(0);
    }

    private void drawSoundPanel()
    {

    }

   public SortingThread getSortingThread() {
        return sort;
   }

    // This function is called during exit.
    @Override
    public void exit() {
        // Exit the sorting thread using its own implemented exit-method.
        sort.exit();
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
            sort.join();
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

    public static void main(String[] passedArgs) {
        // Create a sketch
        String[] processingArgs = { "Algosound" };
        Algosound inst = Algosound.getInstance();
        PApplet.runSketch(processingArgs, inst);
    }
}
