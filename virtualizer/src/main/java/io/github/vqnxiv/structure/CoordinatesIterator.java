package io.github.vqnxiv.structure;


import javafx.geometry.Point2D;

import java.util.Iterator;


/**
 * An extension of {@link Iterator} which also allows
 * to reposition elements.
 * 
 * @param <E> Type of elements.
 */
public interface CoordinatesIterator<E> extends Iterator<E> {

    /**
     * Repositions the last element returned this iterator (optional operation).
     * Calling this method after calling {@link Iterator#remove()} will result
     * in a {@link IllegalStateException}.
     * 
     * @param x New X coordinate.
     * @param y New Y coordinate.
     */
    default void reposition(double x, double y) {
        throw new UnsupportedOperationException();
    }

    /**
     * Repositions the last element returned this iterator (optional operation).
     * Calling this method after calling {@link Iterator#remove()} will result
     * in a {@link IllegalStateException}.
     *      
     * @param p New X/Y coordinates.
     */
    default void reposition(Point2D p) {
        reposition(p.getX(), p.getY());
    }
}
