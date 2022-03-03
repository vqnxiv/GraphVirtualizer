package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.Layout;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesIterator;
import io.github.vqnxiv.structure.CoordinatesStructure;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * The most basic structure. All elements are in a list
 * which is iterated through.
 * 
 * @param <E> Type of elements.
 *           
 * @see CoordinatesStructure
 * @see CoordinatesElement
 * @see CoordinatesMatrix
 */
public class CoordinatesList<E> implements CoordinatesStructure<E> {

    /**
     * List which contains all elements.
     */
    protected final List<CoordinatesElement<E>> elements;

    /**
     * Maximum height in this structure.
     */
    private final DoubleProperty maxHeight = new SimpleDoubleProperty();

    /**
     * Maximum width in this structure.
     */
    private final DoubleProperty maxWidth = new SimpleDoubleProperty();

    
    /**
     * Constructor.
     * 
     * @param el Elements.
     */
    public CoordinatesList(Collection<E> el) {
        elements = new ArrayList<>();
        
        for(E e : el) {
            elements.add(new CoordinatesElement<>(e, 0, 0));
        }
    }

    /**
     * Constructor which lays out this structure.
     * 
     * @param el Elements.
     * @param layoutSupplier Layout factory.
     */
    public CoordinatesList(Collection<E> el, Function<CoordinatesStructure<E>, Layout<E>> layoutSupplier) {
        this(el);
        layoutSupplier.apply(this).apply(); // ????
    }


    /**
     * {@inheritDoc}
     * 
     * @param topLeftX      Top left corner X coordinate.
     * @param topLeftY      Top left corner Y coordinate.
     * @param bottomRightX  Bottom right corner X coordinate.
     * @param bottomRightY  Bottom right corner Y coordinate.
     * @return Collection of all elements within the area.
     */
    @Override
    public Collection<CoordinatesElement<E>> between(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        List<CoordinatesElement<E>> l = new ArrayList<>();
        
        // fail fast 
        if(topLeftX >= bottomRightX || topLeftY >= bottomRightY
        || topLeftX >= maxWidth.get() || topLeftY >= maxHeight.get()
        || bottomRightX <= 0 || bottomRightY <= 0) {
            return l;
        }
        
        // everything
        if(topLeftX <= 0 && topLeftY <= 0 && 
           bottomRightX >= maxWidth.get() && 
           bottomRightY >= maxHeight.get()) {
            l.addAll(elements);
            return l;
        }
        
        for(var e : elements) {
            if(e.isIn(topLeftX, topLeftY, bottomRightX, bottomRightY)) {
                l.add(e);
            }
        }

        return l;
    }

    /**
     * {@inheritDoc}
     * 
     * @param topLeft       Top left corner.
     * @param bottomRight   Bottom right corner.
     * @return Collection of all elements within the area.
     */
    @Override
    public Collection<CoordinatesElement<E>> between(Point2D topLeft, Point2D bottomRight) {
        return between(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
    }

    /**
     * {@inheritDoc}
     * 
     * @return Max height property.
     */
    @Override
    public DoubleProperty maximumHeight() {
        return maxHeight;
    }

    /**
     * {@inheritDoc}
     * 
     * @return Max width property.
     */
    @Override
    public DoubleProperty maximumWidth() {
        return maxWidth;
    }

    /**
     * {@inheritDoc}
     *
     * @return an Iterator.
     */
    @Override
    public CoordinatesIterator<CoordinatesElement<E>> iterator() {
        return new CoordsListIterator();
    }

    /**
     * {@inheritDoc}
     * 
     * @param action The action to be performed for each element
     */
    @Override
    public void forEach(Consumer<? super CoordinatesElement<E>> action) {
        elements.forEach(action);
    }


    /**
     * Iterator for this class. Protected so that extension of
     * this class can reuse this as a base (e.g add remove for mutable collection, etc).
     * <p>
     * No concurrent modification checking is done as this iterator is a simple wrapper
     * around a list iterator, just like this structure is a wrapper around an arraylist.
     */
    protected class CoordsListIterator implements CoordinatesIterator<CoordinatesElement<E>> {

        /**
         * Iterator backing up this iterator.
         */
        private final Iterator<CoordinatesElement<E>> itr;

        /**
         * Constructor.
         */
        CoordsListIterator() {
            itr = elements.listIterator();
        }
        
        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return itr.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public CoordinatesElement<E> next() {
            return itr.next();
        }
    }
}
