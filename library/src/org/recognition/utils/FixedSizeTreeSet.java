package org.recognition.utils;

import java.util.TreeSet;

/*
 * The class represents a sorted fixed-length set.
 * When it is initialized the length and the comparator
 * has to be specified. The ascSorted parameter tells
 * the class what to do when an exceeding element is added:
 * the set is sorted, but the right-most or the left-most
 * element has to be removed.
 *
 * For instance, if an ascending comparator is used, then
 * to store the maximum element we need to set ascSorted
 * to true, in order to remove the lowest element.
 */

public class FixedSizeTreeSet<T> extends TreeSet<T> {
    private boolean ascSorted;
    private int size;

    /**
     * Constructor of <b>FixedSizeTreeSet</b>.
     * Added elements need to implement the Comparable interface.
     *
     * @param size      The size of the set
     * @param ascSorted Tells if the Comparable interface sorts
     *                  element ascending or descending
     */
    public FixedSizeTreeSet(int size, boolean ascSorted) {
        this.size = size;
        this.ascSorted = ascSorted;
    }

    @Override
    public boolean add(T e) {
        boolean b = super.add(e);
        if (super.size() > this.size)
            if (ascSorted)
                b = b && (super.pollFirst() == null);
            else
                b = b && (super.pollLast() == null);

        return b;
    }
}
