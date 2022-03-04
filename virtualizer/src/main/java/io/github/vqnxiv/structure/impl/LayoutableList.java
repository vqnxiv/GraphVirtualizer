package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesIterator;
import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.geometry.Point2D;

import java.util.*;


/**
 * Layoutable extension of {@link CoordinatesList}.
 * 
 * @param <E> Type of elements.
 *           
 * @see CoordinatesList
 * @see LayoutableStructure
 */
public class LayoutableList<E> extends CoordinatesList<E> implements LayoutableStructure<E> {
    
    
    /**
     * Constructor.
     *
     * @param el Elements.
     */
    public LayoutableList(Collection<E> el) {
        super(el);
    }


    /**
     * {@inheritDoc}
     * 
     * @param e The elements with its old coordinates.
     * @param x New X coordinate.
     * @param y New Y coordinate.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, double x, double y) {
        int i = elements.indexOf(e);
        if(i < 0) {
            return;
        }
        
        var e2 = elements.get(i);
        e2.setX(x);
        e2.setY(y);
    }

    /**
     * {@inheritDoc}
     *
     * @param e The elements with its old coordinates.
     * @param p The new coordinates for the element.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, Point2D p) {
        repositionTo(e, p.getX(), p.getY());
    }

    /**
     * {@inheritDoc}
     *
     * @param m Elements with their old coordinates mapped to
     *          their new coordinates.
     */
    @Override
    public void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m) {
        List<CoordinatesElement<E>> changed = new ArrayList<>(m.size());
        
        for(var e : m.entrySet()) {
            int i = elements.indexOf(e.getKey());
            if(i < 0) {
                continue;
            }
            
            var e2 = elements.get(i);
            e2.setX(e.getValue().getX());
            e2.setY(e.getValue().getY());
            changed.add(e2);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return an Iterator.
     */
    @Override
    public CoordinatesIterator<CoordinatesElement<E>> iterator() {
        return new LayoutableIterator();
    }

    
    /**
     * Extension of {@link io.github.vqnxiv.structure.impl.CoordinatesList.CoordsListIterator}
     * to support repositioning elements.
     */
    protected class LayoutableIterator extends CoordsListIterator {

        /**
         * Last seen element.
         */
        private CoordinatesElement<E> lastElt;


        /**
         * Constructor.
         */
        protected LayoutableIterator() { }
        
        
        /**
         * Getter for the last seen element.
         *
         * @return The last seen element.
         */
        // not needed? for iterator.remove() can just itr.remove()
        // => then need protected getter for itr
        protected CoordinatesElement<E> lastElt() {
            return lastElt;
        }
        
        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public CoordinatesElement<E> next() {
            lastElt = super.next();
            return lastElt;
        }

        /**
         * Repositions the last element returned this iterator (optional operation).
         *
         * @param x New X coordinate.
         * @param y New Y coordinate.
         */
        @Override
        public void reposition(double x, double y) {
            repositionTo(lastElt, x, y);
        }

        /**
         * Repositions the last element returned this iterator (optional operation).
         *
         * @param p New X/Y coordinates.
         */
        @Override
        public void reposition(Point2D p) {
            repositionTo(lastElt, p);
        }
    }
}
