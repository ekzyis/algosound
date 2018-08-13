package algosound.data.algorithms;

import algosound.data.visuals.Element;
import algosound.data.Sonification;
import algosound.net.OSC;
import algosound.ui.Algosound;
import algosound.util.AlgosoundUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static algosound.util.AlgosoundUtil.ALGORITHMFPS;
import static algosound.util.AlgosoundUtil.N;

/**
 * Abstract class for all sorting algorithms.
 * - type of visual: Element
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public abstract class SortingAlgorithm extends Thread implements Algorithm {
    public final static SortingAlgorithm BUBBLESORT = new Bubblesort(N);
    public final static SortingAlgorithm INSERTIONSORT = new Insertionsort(N);
    public final static SortingAlgorithm SELECTIONSORT = new Selectionsort(N);
    public final static SortingAlgorithm MERGESORT = new Mergesort(N);
    public final static SortingAlgorithm QUICKSORT = new Quicksort(N);
    // Name of sorting algorithm to display in info area.
    protected String name;
    // Array which should be sorted.
    protected int[] a;
    // Elements which have to be swapped according to integers.
    protected Element[] elements;
    // Is a new frame ready?
    private boolean frameReady;
    // Has the new frame been drawn?
    private boolean frameDrawn;
    // List of elements to unmark next frame.
    protected ArrayList<Element> unmarkMe;
    // Is this thread marked as paused by the user?
    private boolean paused;
    // Should this thread exit?
    private boolean exiting;
    // Is thread currently limited by FPS?
    private boolean waitDueToFPS;

    // List of available sonifications.
    protected List<Sonification> sonifications;
    private int index;
    // Selected sonification
    protected Sonification selected_sonification;
    // Frequencies for sonification.
    public int FREQ_MIN = 200, FREQ_MAX = 4000;

    public SortingAlgorithm(int N) {
        name = "abstract";
        elements = Element.createElements(N, Algosound.getInstance());
        a = Element.getValues(elements);
        // First frame is ready before first iteration.
        frameReady = true;
        frameDrawn = false;
        unmarkMe = new ArrayList<Element>();
        paused = false;
        exiting = false;
        // Set as daemon thread.
        setDaemon(true);
        sonifications = new ArrayList<>();
        index = 0;
        waitDueToFPS = false;
    }

    public String getString() {
        return name;
    }

    public boolean frameIsReady() {
        return frameReady;
    }

    public boolean frameIsDrawn() {
        return frameDrawn;
    }

    // Notify thread waiting for lock that new frame is ready.
    public void notifyFrameReady() {
        frameReady = true;
        this.notify();
        /**
         * Boolean expressions make sure that thread will only wait in this loop if it
         * is actually waiting for a new frame. If thread is paused, it will wait in the
         * next loop. If it's exiting, it will not wait but exit.
         */
        while (!frameIsDrawn() && !isPaused() && !isExiting()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                // Exception clears the interrupted flag. Reset it to check it later.
                this.interrupt();
            }
        }
        /**
         * Check if this frame the user did press the pause button. If yes and thread is
         * not exiting, the thread will pause until the user wants to resume.
         */
        if (isPaused() && !isExiting()) {
            System.out.println("--- sort: pausing.");
            while (isPaused() && !isExiting()) {
                try {

                    OSC.getInstance().sendMessage(selected_sonification.PAUSEPATH);
                    this.wait();
                    OSC.getInstance().sendMessage(selected_sonification.RESUMEPATH);
                } catch (InterruptedException e) {
                    // Exception clears the interrupted flag. Reset it to check it later.
                    this.interrupt();
                }
            }
            System.out.println("--- sort: resuming.");
        }
        while (isWaitingDueToFPS()) {
            try {
                System.out.println("--- sort: waiting due to choosen fps.");
                this.wait();
            } catch (InterruptedException e) {
                // Exception clears the interrupted flag. Reset it to check it later.
                this.interrupt();
            }
        }
        // Clear markers from last frame.
        clearMarkers();
        frameDrawn = false;

        /**
         * If thread is exiting, the interrupt-flag will be still set at this point.
         * This causes to escape from the for-loop, set swap to false, and then leave
         * the do-while-loop and finally terminate this thread. If thread was paused or
         * waiting for a new frame and then a InterruptedException happens, the flag
         * will be reset but the thread will continue to wait for a resume or a new
         * frame since the wait()-statement is in a while-loop. This following wait()
         * unsets the interrupted-flag so the thread will not exit when interrupted
         * while pausing or waiting for a new frame.
         */
        /**
         * Measure time of frame calculating for waiting to match ALGORITHMFPS
         * @see ALGORITHMFPS
         */
        if (ALGORITHMFPS > 0) {
            double delay = (1 / ALGORITHMFPS) * 1000;
            Timer t = new Timer();
            SortingAlgorithm inst = this;
            // System.out.println("--- sort: delay = "+delay);
            waitDueToFPS = true;
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    waitDueToFPS = false;
                    synchronized (inst) {
                        inst.notify();
                    }
                }
            }, (long) delay);
        }
    }

    // Notify thread that new frame has been drawn.
    public void notifyFrameDraw() {
        frameDrawn = true;
        // New frame has just been drawn. Next frame is not ready yet.
        frameReady = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        this.paused = true;
        // Notify thread so it will always wait in the correct expected loop.
        synchronized (this) {
            this.notify();
        }
    }

    public void resumeAlgorithm() {
        this.paused = false;
        // Notify thread since it should no longer be paused.
        synchronized (this) {
            this.notify();
        }
    }

    public boolean isExiting() {
        return exiting;
    }

    public boolean isWaitingDueToFPS() {
        return waitDueToFPS;
    }

    // Set the exit-flag and interrupt the thread; waking it up from eventual
    // waiting.
    public void exit() {
        this.exiting = true;
        // This wakes thread up from waiting, making it able to exit.
        this.interrupt();
    }

    // Return elements.
    public Element[] getVisuals() {
        return elements;
    }

    // Mark currently accessed elements.
    public void mark(int i) {
        elements[i].mark();
        // Add those elements to list of elements which get unmarked next frame.
        unmarkMe.add(elements[i]);
    }

    // Clear markers from last frame.
    public void clearMarkers() {
        for (Element e : unmarkMe) {
            e.unmark();
        }
        // remove all elements from list since a new frame will begin now.
        unmarkMe.clear();
    }

    // Swap element at given index with neighbour to ensure visualization.
    void swap(int i, int j) {
        /**
         * Elements need to swap their x-position AND their position in the array!
         * Otherwise, next iteration of the for-loop would cause severe bugs since
         * Bubblesort swaps the integers in the array (= change their index) and assumes
         * the corresponding element is at the same index in the elements array.
         */
        elements[i].swap(elements[j], Element.COORDINATES);
        Element tmp = elements[i];
        elements[i] = elements[j];
        elements[j] = tmp;
    }

    public Sonification getSelectedSonification() {
        return selected_sonification;
    }

    public void changeSonification() {
        index = (index + 1) % sonifications.size();
        selected_sonification = sonifications.get(index);
    }

    public void setSonification(int index) {
        this.index = index;
        selected_sonification = sonifications.get(index);
    }

    public int getIndex() {
        return index;
    }

    /**
     * Reset the sorting process. Create a new random array. Maintain selected sonification.
     *
     * @return a instance which has been reset
     */
    public SortingAlgorithm reset() {
        // Save current selected sonification index.
        int index = this.getIndex();
        /**
         * We need to get the concrete constructor first
         * since we can't use the abstract constructor.
         */
        Constructor<? extends SortingAlgorithm> constructor = null;
        try {
            constructor = this.getClass().getConstructor(int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        SortingAlgorithm sort;
        try {
            sort = constructor.newInstance(N);
            sort.setSonification(index);
            // Overwrite instance in algorithm list with this new one.
            AlgosoundUtil.updateAlgorithm(sort);
            return sort;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
