package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.Layout;
import io.github.vqnxiv.structure.*;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


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
     * On event consumers. 
     */
    private final Map<Object, List<Consumer<? super StructureChange.Move<E>>>> consumers = new HashMap<>();

    /**
     * Modification counter for internalItr concurrent modification.
     */
    private int modCount = 0;

    
    /**
     * Constructor.
     *
     * @param el Elements.
     */
    public LayoutableList(Collection<E> el) {
        super(el);
    }

    /**
     * Constructor with layout.
     *
     * @param el Elements.
     * @param layoutSupplier Initial layout.
     */
    public LayoutableList(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier) {
        super(el);
        layoutSupplier.apply(this).apply();
    }

    /**
     * Copy constructor.
     *
     * @param el Structure to copy.
     */
    public LayoutableList(CoordinatesStructure<E> el) {
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
        // < 0 check?
        
        int i = elements().indexOf(e);
        if(i < 0) {
            return;
        }

        var cp = new CoordinatesElement<>(e);
        var e2 = elements().get(i);
        e2.setX(x);
        e2.setY(y);
        if(x > maxWidth.get()) {
            maxWidth.set(x);
        }
        if(y > maxHeight.get()) {
            maxHeight.set(y);
        }
        
        var pTL = new Point2D(Math.min(cp.getX(), x), Math.min(cp.getY(), y));
        var pBR = new Point2D(Math.max(cp.getX(), x), Math.max(cp.getY(), y));
        modified();
        fireMoveEvent(Map.of(cp, e2.getXY()), pTL, pBR);
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
    // extremely slow because needs to check whether the element is in the list
    @Override
    public void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m) {
        Map<CoordinatesElement<E>, Point2D> changed = new HashMap<>(m.size());

        // area which contains the changes
        double minChangedX = maxWidth.get();
        double minChangedY = maxHeight.get();
        double maxChangedX = 0d;
        double maxChangedY = 0d;
            
        // new possible max dimensions
        double maxx = 0;
        double maxy = 0;
        
        for(var e : m.entrySet()) {
            int i = elements().indexOf(e.getKey());
            if(i < 0) {
                continue;
            }
            
            var e2 = elements().get(i);
            maxx = Math.max(maxx, e.getValue().getX());
            maxy = Math.max(maxy, e.getValue().getY());

            // checks old coordinates
            minChangedX = Math.min(minChangedX, e2.getX());
            minChangedY = Math.min(minChangedY, e2.getY());
            maxChangedX = Math.max(maxChangedX, e2.getX());
            maxChangedY = Math.max(maxChangedY, e2.getY());
            
            e2.setX(e.getValue().getX());
            e2.setY(e.getValue().getY());

            // checks new coordinates
            minChangedX = Math.min(minChangedX, e2.getX());
            minChangedY = Math.min(minChangedY, e2.getY());
            maxChangedX = Math.max(maxChangedX, e2.getX());
            maxChangedY = Math.max(maxChangedY, e2.getY());
            
            changed.put(e.getKey(), e2.getXY());
        }
        
        if(changed.isEmpty()) {
            return;
        }
        
        if(maxx > maxWidth.get()) {
            maxWidth.set(maxx);
        }
        if(maxy > maxHeight.get()) {
            maxHeight.set(maxy);
        }
        
        modified();
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
     * Increases the modification count.
     */
    protected void modified() {
        modCount++;
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
     * Extension of {@link io.github.vqnxiv.structure.impl.CoordinatesList.CoordsListIterator}
     * to support repositioning elements.
     */
    protected class LayoutableIterator extends CoordsListIterator {

        /**
         * Last seen elements.
         */
        private CoordinatesElement<E> lastElt;

        /**
         * Expected modification count.
         */
        private final int expectedModCount;


        /**
         * Constructor.
         */
        protected LayoutableIterator() { 
            expectedModCount = modCount;
        }


        /**
         * Sets the the last element to null.
         */
        protected void nullLast() {
            lastElt = null;
        }
        
        
        /**
         * {@inheritDoc}
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public CoordinatesElement<E> next() {
            // the underlying list itself isn't changed 
            // so it could be done, but that way it's
            // consistent w/ matrix + potentially other structures?
            if(expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            lastElt = super.next();
            return lastElt;
        }

        /**
         * {@inheritDoc}
         *
         * @param x New X coordinate.
         * @param y New Y coordinate.
         */
        @Override
        public void reposition(double x, double y) {
            if(lastElt == null) {
                throw new IllegalStateException();
            }
            repositionTo(lastElt, x, y);
            modCount--; // blergh
        }
    }
}
