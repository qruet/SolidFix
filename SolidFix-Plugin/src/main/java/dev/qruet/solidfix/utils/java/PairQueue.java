package dev.qruet.solidfix.utils.java;

/**
 * @author qruet
 * @param <T>
 */
public class PairQueue<T> {

    private final T[] pair_array;

    public PairQueue(T t1, T t2) {
        this.pair_array = (T[]) new Object[2];
    }

    public void add(T t) {
        pair_array[0] = pair_array[1];
        pair_array[1] = t;
    }

    public T getRecent() {
        return pair_array[1];
    }

    public T getOld() {
        return pair_array[0];
    }

}
