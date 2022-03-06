package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesIterator;
import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.geometry.Point2D;

import java.util.*;


/**
 * Layoutable extension of {@link CoordinatesMatrix}.
 * 
 * @param <E> Type of elements.
 */
public class LayoutableMatrix<E> extends CoordinatesMatrix<E> implements LayoutableStructure<E> {


    /**
     * Constructor.
     *
     * @param el Elements.
     */
    public LayoutableMatrix(Collection<E> el) {
        super(el);
    }

    /**
     * Constructor.
     *
     * @param el            Elements.
     * @param initialWidth  Width.
     * @param initialHeight Height.
     */
    public LayoutableMatrix(Collection<E> el, double initialWidth, double initialHeight) {
        super(el, initialWidth, initialHeight);
    }

    /**
     * Constructor.
     *
     * @param el               Elements.
     * @param initialWidth     Width.
     * @param initialHeight    Height.
     * @param initialRowNumber Row number.
     * @param initialColNumber Column number.
     */
    public LayoutableMatrix(Collection<E> el, double initialWidth, double initialHeight, 
                            int initialRowNumber, int initialColNumber) {
        super(
            el, initialWidth, initialHeight, 
            initialRowNumber, initialColNumber
        );
    }

    /**
     * Constructor.
     *
     * @param el                  Elements.
     * @param initialWidth        Width.
     * @param initialHeight       Height.
     * @param initialRowNumber    Row number.
     * @param initialColNumber    Column number.
     * @param maxRowRangeIncrease Maximum row range increase.
     * @param maxColRangeIncrease Maximum column range increase.
     */
    public LayoutableMatrix(Collection<E> el, double initialWidth, double initialHeight, 
                            int initialRowNumber, int initialColNumber, 
                            float maxRowRangeIncrease, float maxColRangeIncrease) {
        super(
            el, initialWidth, initialHeight, 
            initialRowNumber, initialColNumber, 
            maxRowRangeIncrease, maxColRangeIncrease
        );
    }

    /**
     * Constructor.
     *
     * @param el                  Elements.
     * @param initialWidth        Width.
     * @param initialHeight       Height.
     * @param initialRowNumber    Row number.
     * @param initialColNumber    Column number.
     * @param maxRowRangeIncrease Maximum row range increase.
     * @param maxColRangeIncrease Maximum column range increase.
     * @param maxRowNumber        Maximum rows.
     * @param maxColNumber        Maximum columns.
     */
    public LayoutableMatrix(Collection<E> el, double initialWidth, double initialHeight, 
                            int initialRowNumber, int initialColNumber, 
                            float maxRowRangeIncrease, float maxColRangeIncrease, 
                            int maxRowNumber, int maxColNumber) {
        super(
            el, initialWidth, initialHeight, 
            initialRowNumber, initialColNumber, 
            maxRowRangeIncrease, maxColRangeIncrease, 
            maxRowNumber, maxColNumber
        );
    }

    /**
     * Reposition one element to the given coordinates.
     *
     * @param e The elements with its old coordinates.
     * @param x New X coordinate.
     * @param y New Y coordinate.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, double x, double y) {
        move(e, x, y);
    }

    /**
     * Reposition one element to the given coordinates.
     *
     * @param e The elements with its old coordinates.
     * @param p The new coordinates for the element.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, Point2D p) {
        move(e, p);
    }

    /**
     * Reposition multiple elements to the given coordinates.
     *
     * @param m Elements with their old coordinates mapped to
     *          their new coordinates.
     */
    @Override
    public void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m) {
        List<CoordinatesElement<E>> changed = new ArrayList<>(m.size());

        for(var e : m.entrySet()) {
            if(move(e.getKey(), e.getValue())) {
                var p = e.getKey();
                // not needed
                p.setX(e.getValue().getX());
                p.setY(e.getValue().getY());
                changed.add(p);
            }
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
    
    
    protected class LayoutableIterator extends MatrixIterator {

        /**
         * Keeps track of already visited elements so that they can be skipped.
         */
        private final Set<CoordinatesElement<E>> visited;

        /**
         * Last seen element.
         */
        private CoordinatesElement<E> last;
        
        /**
         * Total number of moves.
         */
        private int totalMoves = 0;

        
        /**
         * Constructor.
         */
        protected LayoutableIterator() {
            visited = new HashSet<>();
        }


        /**
         * {@inheritDoc}
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public CoordinatesElement<E> next() {
            last = super.next();
            visited.add(last);
            return last;
        }

        /**
         * Repositions the last element returned this iterator (optional operation).
         *
         * @param x New X coordinate.
         * @param y New Y coordinate.
         */
        @Override
        public void reposition(double x, double y) {
            repositionTo(last, x, y);
            totalMoves++;
            updateNext(true);
        }

        /**
         * Repositions the last element returned this iterator (optional operation).
         *
         * @param p New X/Y coordinates.
         */
        @Override
        public void reposition(Point2D p) {
            repositionTo(last, p);
            totalMoves++;
            updateNext(true);
        }

        /**
         * {@inheritDoc}
         * 
         * @param ignored Ignored.
         */
        @Override
        protected void updateNext(boolean ignored) {
            while(!hasReachedTheEnd() && !isCurrentNextValid()) {
                super.updateNext(true);
            }
        }

        /**
         * Getter for the expected modification count.
         *
         * @return The expected modification count.
         */
        // blegh
        @Override
        protected int expectedModCount() {
            return super.expectedModCount() + totalMoves;
        }

        /**
         * Checks whether there is a next element and if it is valid (i.e non visited).
         * 
         * @return {@code true} if the next element is valid; {@code false} otherwise.
         */
        private boolean isCurrentNextValid() {
            return getPotentialNext().isPresent() 
                && (visited == null // pains
                || !visited.contains(getPotentialNext().get())
            );
        }
    }
}
