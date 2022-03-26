package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.Layout;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesIterator;
import io.github.vqnxiv.structure.CoordinatesStructure;
import io.github.vqnxiv.structure.LayoutableStructure;
import io.github.vqnxiv.structure.StructureChange;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Layoutable extension of {@link CoordinatesMatrix}.
 * 
 * @param <E> Type of elements.
 */
public class LayoutableMatrix<E> extends CoordinatesMatrix<E> implements LayoutableStructure<E> {


    /**
     * Layoutable matrix where {@link #repositionAllTo(Map)}) doesn't check
     * whether the structure already contains the elements.
     * 
     * It is only used for faster repositioning when creating a new matrix
     * with a layout (as all the elements will likely be placed in the [0][0]
     * collection so {@code contains} checks can be quite time consuming).
     * 
     * @param <E> Type of elements.
     */
    protected static final class UncheckedLayoutableMatrix<E> extends LayoutableMatrix<E> {

        /**
         * Layout constructor.
         *
         * @param el             Elements.
         * @param layoutSupplier Initial layout.
         */
        public UncheckedLayoutableMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier) {
            super(el);
            layoutSupplier.apply(this).apply();
        }

        /**
         * Layout constructor.
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
        public UncheckedLayoutableMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier, 
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
         * {@inheritDoc}
         *
         * @param m Elements with their old coordinates mapped to
         *          their new coordinates.
         */
        @Override
        public void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m) {
            emptyElements();
            m.forEach(
                (k, v) -> {
                    k.setXY(v);
                    place(k);
                }
            );

            updateDimensions();
        }
    }  
    
    
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
     * Layout constructor.
     *
     * @param el             Elements.
     * @param layoutSupplier Initial layout.
     */
    public LayoutableMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier) {
        super(el, layoutSupplier);
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
     * Layout constructor.
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
    public LayoutableMatrix(Collection<E> el, 
                            Function<LayoutableStructure<E>, Layout<E>> layoutSupplier, 
                            double initialWidth, double initialHeight,
                            int initialRowNumber, int initialColNumber, 
                            float maxRowRangeIncrease, float maxColRangeIncrease, 
                            int maxRowNumber, int maxColNumber) {
        super(
            el, layoutSupplier, 
            initialWidth, initialHeight, 
            initialRowNumber, initialColNumber, 
            maxRowRangeIncrease, maxColRangeIncrease, 
            maxRowNumber, maxColNumber
        );
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
        consumers.computeIfAbsent(owner, o -> new ArrayList<>());
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
         * Last seen element. {@code null} if {@link #reposition(double, double)}
         * or {@link #remove()} were called.
         */
        private CoordinatesElement<E> last;

        /**
         * Next element. {@code null} if no more next elements.
         */
        private CoordinatesElement<E> next;
        
        
        /**
         * Constructor.
         */
        protected LayoutableIterator() {
            super();
            visited = new HashSet<>();
            
            // if(super.hasNext()) {
            //     next = super.next();
            // }
        }


        /**
         * {@inheritDoc}
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            // return next != null;
            return super.hasNext();
        }

        /**
         * {@inheritDoc}
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public CoordinatesElement<E> next() {
            // if(next == null) {
            //     throw new NoSuchElementException();
            // }
            
            // last = next;
            // visited.add(last);
            
            // while(visited.contains(next) && next != null) {
            //     next = (super.hasNext()) ? super.next() : null;
            // }
            last = super.next();
            
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
            if(last == null) {
                throw new IllegalStateException();
            }
            
            /*
            var cp = new CoordinatesElement<>(last);
            last.setXY(x, y);
            place(last);
            
            var pTL = new Point2D(Math.min(cp.getX(), x), Math.min(cp.getY(), y));
            var pBR = new Point2D(Math.max(cp.getX(), x), Math.max(cp.getY(), y));
            fireMoveEvent(Map.of(cp, last.getXY()), pTL, pBR);
            */
            
            repositionTo(last, x, y);
            updateExpectedModCount();
        }


        /**
         * Sets the the last element to null.
         */
        protected void nullLast() {
            last = null;
        }

        /**
         * Getter for the last seen element.
         *
         * @return The last seen element.
         */
        protected CoordinatesElement<E> getLast() {
            return last;
        }
    }
}
