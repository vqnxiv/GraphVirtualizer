package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.Layout;
import io.github.vqnxiv.structure.*;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Layoutable extension of {@link CoordinatesMatrix}.
 * 
 * @param <E> Type of elements.
 */
public class LayoutableMatrix<E> extends CoordinatesMatrix<E> implements LayoutableStructure<E> {


    /**
     * On event consumers. 
     */
    private final List<Consumer<? super StructureChange.Move<E>>> consumers = new ArrayList<>();
    

    /**
     * Constructor.
     *
     * @param el Elements.
     */
    public LayoutableMatrix(Collection<E> el) {
        super(el);
    }

    public LayoutableMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier) {
        super(el);
        layoutSupplier.apply(this).apply();
    }

    /**
     * Elements copy constructor.
     *
     * @param c Structure to copy.
     */
    public LayoutableMatrix(CoordinatesStructure<E> c) {
        super(c);
    }

    /**
     * Copy constructor.
     *
     * @param m Matrix to copy.
     */
    public LayoutableMatrix(CoordinatesMatrix<E> m) {
        super(m);
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
    public LayoutableMatrix(Collection<E> el,
                            double initialWidth, double initialHeight,
                            int initialRowNumber, int initialColNumber,
                            float maxRowRangeIncrease, float maxColRangeIncrease,
                            int maxRowNumber, int maxColNumber) {
        super(
            el,
            initialWidth, initialHeight,
            initialRowNumber, initialColNumber,
            maxRowRangeIncrease, maxColRangeIncrease,
            maxRowNumber, maxColNumber
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
    public LayoutableMatrix(CoordinatesStructure<E> el,
                            double initialWidth, double initialHeight,
                            int initialRowNumber, int initialColNumber,
                            float maxRowRangeIncrease, float maxColRangeIncrease,
                            int maxRowNumber, int maxColNumber) {
        super(
            el,
            initialWidth, initialHeight,
            initialRowNumber, initialColNumber,
            maxRowRangeIncrease, maxColRangeIncrease,
            maxRowNumber, maxColNumber
        );
    }
    
    /**
     * Constructor.
     *
     * @param el                  Elements.
     * @param layoutSupplier      Initial layout.
     * @param initialWidth        Width.
     * @param initialHeight       Height.
     * @param initialRowNumber    Row number.
     * @param initialColNumber    Column number.
     * @param maxRowRangeIncrease Maximum row range increase.
     * @param maxColRangeIncrease Maximum column range increase.
     * @param maxRowNumber        Maximum rows.
     * @param maxColNumber        Maximum columns.
     */
    public LayoutableMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier, 
                            double initialWidth, double initialHeight, 
                            int initialRowNumber, int initialColNumber, 
                            float maxRowRangeIncrease, float maxColRangeIncrease, 
                            int maxRowNumber, int maxColNumber) {
        super(
            el,
            initialWidth, initialHeight, 
            initialRowNumber, initialColNumber, 
            maxRowRangeIncrease, maxColRangeIncrease, 
            maxRowNumber, maxColNumber
        );

        layoutSupplier.apply(this).apply();
    }

    /**
     * Constructor.
     *
     * @param el                  Elements.
     * @param layoutSupplier      Initial layout.
     * @param initialWidth        Width.
     * @param initialHeight       Height.
     * @param initialRowNumber    Row number.
     * @param initialColNumber    Column number.
     * @param maxRowRangeIncrease Maximum row range increase.
     * @param maxColRangeIncrease Maximum column range increase.
     * @param maxRowNumber        Maximum rows.
     * @param maxColNumber        Maximum columns.
     */
    public LayoutableMatrix(CoordinatesStructure<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier, 
                            double initialWidth, double initialHeight, 
                            int initialRowNumber, int initialColNumber, 
                            float maxRowRangeIncrease, float maxColRangeIncrease, 
                            int maxRowNumber, int maxColNumber) {
        super(
            el,
            initialWidth, initialHeight, 
            initialRowNumber, initialColNumber, 
            maxRowRangeIncrease, maxColRangeIncrease, 
            maxRowNumber, maxColNumber
        );
        
        layoutSupplier.apply(this).apply();
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
        repositionTo(e, new Point2D(x, y));
    }

    /**
     * Reposition one element to the given coordinates.
     *
     * @param e The elements with its old coordinates.
     * @param p The new coordinates for the element.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, Point2D p) {
        var cp = new CoordinatesElement<>(e);
        if(!move(e, p)) {
            return;
        }

        var pTL = new Point2D(Math.min(cp.getX(), p.getX()), Math.min(cp.getY(), p.getX()));
        var pBR = new Point2D(Math.max(cp.getX(), p.getX()), Math.max(cp.getY(), p.getY()));
        fireEvent(Map.of(cp, p), pTL, pBR);
    }

    /**
     * Reposition multiple elements to the given coordinates.
     *
     * @param m Elements with their old coordinates mapped to
     *          their new coordinates.
     */
    @Override
    public void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m) {
        Map<CoordinatesElement<E>, Point2D> changed = new HashMap<>(m.size());
        
        double minChangedX = maximumWidth().get();
        double minChangedY = maximumHeight().get();
        double maxChangedX = 0d;
        double maxChangedY = 0d;
        CoordinatesElement<E> cp;
        
        for(var e : m.entrySet()) {
            cp = new CoordinatesElement<>(e.getKey());
            if(move(e.getKey(), e.getValue())) {

                minChangedX = Math.min(minChangedX, cp.getX());
                minChangedY = Math.min(minChangedY, cp.getY());
                maxChangedX = Math.max(maxChangedX, cp.getX());
                maxChangedY = Math.max(maxChangedY, cp.getY());
                
                changed.put(cp, e.getValue());

                minChangedX = Math.min(minChangedX, e.getValue().getX());
                minChangedY = Math.min(minChangedY, e.getValue().getY());
                maxChangedX = Math.max(maxChangedX, e.getValue().getX());
                maxChangedY = Math.max(maxChangedY, e.getValue().getY());
            }
        }

        if(changed.isEmpty()) {
            return;
        }
        
        fireEvent(changed, new Point2D(minChangedX, minChangedY), new Point2D(maxChangedX, maxChangedY));
    }

    /**
     * Notifies all the consumers.
     */
    protected void fireEvent(Map<CoordinatesElement<E>, Point2D> m, Point2D topLeft, Point2D bottomRight) {
        var e = StructureChange.moved(this, m, topLeft, bottomRight);
        for(var c : consumers) {
            c.accept(e);
        }
    }
    

    /**
     * {@inheritDoc}
     *
     * @param action The action to perform.
     */
    @Override
    public void addMoveListener(Consumer<? super StructureChange.Move<E>> action) {
        consumers.add(action);
    }

    /**
     * {@inheritDoc}
     *
     * @param action The action to stop doing.
     */
    @Override
    public void removeMoveListener(Consumer<? super StructureChange.Move<E>> action) {
        consumers.remove(action);
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
     * Iterator extension which allows the reposition of elements.
     */
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
