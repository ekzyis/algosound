package algosound.util;

import algosound.data.algorithms.Algorithm;
import processing.core.PApplet;

/**
 * Utility functions and members are here.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class AlgosoundUtil {
    // Width and height of canvas.
    public static final int W = 640, H = 320;
    // Number of elements to be sorted.
    public static final int N = W / 5;
    // Width of GUI area.
    public static final int GUI_W = 70;
    // Height of Info area.
    public static final int INFO_H = 35;
    // Framerate of visualization.
    public static final int FRAMERATE = 60;
    // List of available algorithms.
    private static final Algorithm[] ALGORITHMS = {Algorithm.BUBBLESORT, Algorithm.INSERTIONSORT, Algorithm.SELECTIONSORT, Algorithm.MERGESORT, Algorithm.QUICKSORT};
    // Selected algorithm
    public static Algorithm SELECTED_ALGORITHM = Algorithm.BUBBLESORT;
    private static int algo_index = 0;

    /**
     * Exponential map function: f(x) = a*e^(b*x)
     * This function must satisfy following two equations: f(x1) = y1, f(x2) = y2
     * Rearrangment of equations leads to following solution
     * ==> b = ln(y2/y1)/(x2-x1) a = y2/( e^(b*x2) ) = y1/( e^(b*x1) )
     */
    public static int expmap(int value, int x1, int x2, int y1, int y2) {
        float b = PApplet.log(y2 / y1) / (x2 - x1);
        float a = y2 / (PApplet.exp(b * x2));
        return (int) (a * PApplet.exp(value * b));
    }

    // Change algorithm
    public static void changeAlgorithm() {
        algo_index = (algo_index + 1) % ALGORITHMS.length;
        SELECTED_ALGORITHM = ALGORITHMS[algo_index];
    }
}
