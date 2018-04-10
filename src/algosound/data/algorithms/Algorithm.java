package algosound.data.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static algosound.util.AlgosoundUtil.N;

/**
 * Algorithms. This file wraps the algorithms into enums to easily refer to them.
 * ================================
 *
 * @author ekzyis
 * @date 04/03/2018
 */
public enum Algorithm {
    BUBBLESORT(new Bubblesort(N)),
    INSERTIONSORT(new Insertionsort(N)),
    SELECTIONSORT(new Selectionsort(N)),
    MERGESORT(new Mergesort(N)),
    QUICKSORT(new Quicksort(N));

    private SortingAlgorithm sort;
    Algorithm(SortingAlgorithm sort) {
        this.sort  = sort;
    }

    /**
     * Creates a new instance of the sorting thread and returns it.
     * @return new sorting thread instance
     */
    public SortingAlgorithm getNewInstance() {
        // Create a new instance.
        /**
         * We need to get the correct constructor first
         * since we don't use the default constructor
         */
        Constructor<? extends SortingAlgorithm> constructor = null;
        try {
            constructor = (sort.getClass()).getConstructor(int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            // Save current selected sonification index.
            int index = sort.getIndex();
            sort = constructor.newInstance(N);
            // Reset to previously selected sonification.
            sort.setSonification(index);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return sort;
    }

    /**
     *
     * @return current instance of sorting thread
     */
    public SortingAlgorithm getInstance() {
        return sort;
    }
}
