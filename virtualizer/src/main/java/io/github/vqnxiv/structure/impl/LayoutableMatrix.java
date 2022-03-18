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
    private final Map<Object, List<Consumer<? super StructureChange.Move<E>>>> consumers = new HashMap<>();



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
     * @param el             Elements
     * @param layoutSupplier Initial layout.
     */
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
     * {@inheritDoc}
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
        fireMoveEvent(Map.of(cp, p), pTL, pBR);
    }

    /**
     * {@inheritDoc}
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
        
        fireMoveEvent(changed, new Point2D(minChangedX, minChangedY), new Point2D(maxChangedX, maxChangedY));
    }
    
    
    /**
     * Notifies all the consumers.
     */
    private void fireMoveEvent(Map<CoordinatesElement<E>, Point2D> m, Point2D topLeft, Point2D bottomRight) {
        var e = StructureChange.moved(this, m, topLeft, bottomRight);
        for(var l : consumers.values()) {
            l.forEach(c -> c.accept(e));
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param owner  The listener owner.
     * @param action The action to perform.
     */
    @Override
    public void addMoveListener(Object owner, Consumer<? super StructureChange.Move<E>> action) {
        consumers.computeIfAbsent(owner, l -> new ArrayList<>());
        consumers.get(owner).add(action);
    }

    /**
     * {@inheritDoc}
     *
     * @param owner  The listener owner.
     * @param action The action to stop doing.
     */
    @Override
    public void removeMoveListener(Object owner, Consumer<? super StructureChange.Move<E>> action) {
        var l = consumers.get(owner);
        if(l != null) {
            l.remove(action);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param owner The listeners owner.
     * @return The removed listeners.
     */
    @Override
    public Collection<Consumer<? super StructureChange.Move<E>>> clearMoveListeners(Object owner) {
        var l =  consumers.remove(owner);
        return (l != null) ? l : List.of();
    }

    /**
     * {@inheritDoc}
     *
     * @return The removed listeners.
     */
    @Override
    public Collection<Consumer<? super StructureChange.Move<E>>> clearMoveListeners() {
        var l = new ArrayList<Consumer<? super StructureChange.Move<E>>>();
        consumers.values().forEach(l::addAll);
        consumers.clear();
        return l;
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
         * {@inheritDoc}
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
         * {@inheritDoc}
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
