package algosound.algorithms;

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
    INSERTIONSORT(new Insertionsort(N));

    private SortingThread sort;
    Algorithm(SortingThread sort) {
        this.sort  = sort;
    }

    public SortingThread getInstance() {
        // Create a new instance.
        /**
         * We need to get the correct constructor first
         * since we don't use the default constructor
         */
        Constructor<? extends SortingThread> constructor = null;
        try {
            constructor = (sort.getClass()).getConstructor(int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            sort = constructor.newInstance(N);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return sort;
    }
}
