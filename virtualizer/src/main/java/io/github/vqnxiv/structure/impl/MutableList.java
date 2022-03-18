package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.Layout;
import io.github.vqnxiv.structure.*;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Mutable extension of {@link LayoutableList}.
 * 
 * @param <E> Type of elements.
 *           
 * @see LayoutableList
 * @see MutableStructure
 */
public class MutableList<E> extends LayoutableList<E> implements MutableStructure<E> {

    /**
     * On addition consumers. 
     */
    private final Map<Object, List<Consumer<? super StructureChange.Addition<E>>>> addConsumers = new HashMap<>();

    /**
     * On removal consumers. 
     */
    private final Map<Object, List<Consumer<? super StructureChange.Removal<E>>>> rmConsumers = new HashMap<>();
    
    
    /**
     * Constructor.
     *
     * @param el Elements.
     */
    public MutableList(Collection<E> el) {
        super(el);
    }

    /**
     * Constructor with layout.
     *
     * @param el             Elements.
     * @param layoutSupplier Initial layout.
     */
    public MutableList(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier) {
        super(el, layoutSupplier);
    }

    /**
     * Copy constructor.
     *
     * @param el Structure to copy.
     */
    public MutableList(CoordinatesStructure<E> el) {
        super(el);
    }
    

    // todo: update max dimensions on all add/remove
    
    /**
     * {@inheritDoc}
     *
     * @param element The element to add.
     * @return {@code true} if it was successfully added.
     */
    @Override
    public boolean addAt(CoordinatesElement<E> element) {
        if(!elements().add(element)) {
            return false;
        }

        modified();
        fireAddEvent(List.of(element), element.getXY(), element.getXY());
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinatesElements The elements to add.
     * @return {@code true} if at least one element was successfully added.
     */
    @Override
    public boolean addAllAt(Collection<CoordinatesElement<E>> coordinatesElements) {
        var l = new ArrayList<CoordinatesElement<E>>(coordinatesElements.size());

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = 0d;
        double maxY = 0d;
        
        for(var c : coordinatesElements) {
            if(elements().add(c)) {
                l.add(c);
                minX = Math.min(minX, c.getX());
                maxX = Math.max(maxX, c.getX());
                minY = Math.min(minY, c.getY());
                maxY = Math.max(maxY, c.getY());
            }
        }
        
        if(l.isEmpty()) {
            return false;
        }

        modified();
        fireAddEvent(l, new Point2D(minX, minY), new Point2D(maxX, maxY));
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param element The element to remove.
     * @return {@code true} if the element was successfully removed.
     */
    @Override
    public boolean remove(E element) {
        CoordinatesElement<E> c;
        for(int i = 0; i < elements().size(); i++) {
            if((c = elements().get(i)).getElement().equals(element)) {
                elements().remove(i);
                modified();
                fireRmEvent(List.of(c), c.getXY(), c.getXY());
                return true;
            }
        }
        
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param elements The element to remove.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeAll(Collection<E> elements) {
        var l = new ArrayList<CoordinatesElement<E>>(elements.size());

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = 0d;
        double maxY = 0d;
        
        for(var c : elements()) {
            if(elements.contains(c.getElement())) {
                l.add(c);
                minX = Math.min(minX, c.getX());
                maxX = Math.max(maxX, c.getX());
                minY = Math.min(minY, c.getY());
                maxY = Math.max(maxY, c.getY());
            }
        }
        
        if(l.isEmpty()) {
            return false;
        }
        
        elements().removeAll(l);
        modified();
        fireRmEvent(l, new Point2D(minX, minY), new Point2D(maxX, maxY));
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param element The element to remove.
     * @return {@code true} if the element was successfully removed.
     */
    @Override
    public boolean removeAt(CoordinatesElement<E> element) {
        if(!elements().remove(element)) {
            return false;
        }

        modified();
        fireRmEvent(List.of(element), element.getXY(), element.getXY());
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinatesElements The element to remove.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeAllAt(Collection<CoordinatesElement<E>> coordinatesElements) {
        var l = new ArrayList<CoordinatesElement<E>>(coordinatesElements.size());

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = 0d;
        double maxY = 0d;

        for(var c : coordinatesElements) {
            if(elements().remove(c)) {
                l.add(c);
                minX = Math.min(minX, c.getX());
                maxX = Math.max(maxX, c.getX());
                minY = Math.min(minY, c.getY());
                maxY = Math.max(maxY, c.getY());
            }
        }

        if(l.isEmpty()) {
            return false;
        }

        modified();
        fireRmEvent(l, new Point2D(minX, minY), new Point2D(maxX, maxY));
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param condition Filtering condition.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeIf(Predicate<E> condition) {
        var l = new ArrayList<CoordinatesElement<E>>();

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = 0d;
        double maxY = 0d;

        for(var c : elements()) {
            if(condition.test(c.getElement())) {
                l.add(c);
                minX = Math.min(minX, c.getX());
                maxX = Math.max(maxX, c.getX());
                minY = Math.min(minY, c.getY());
                maxY = Math.max(maxY, c.getY());
            }
        }

        if(l.isEmpty()) {
            return false;
        }

        elements().removeAll(l);
        modified();
        fireRmEvent(l, new Point2D(minX, minY), new Point2D(maxX, maxY));
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param condition Filtering condition.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeCoordinatesIf(Predicate<CoordinatesElement<E>> condition) {
        var l = new ArrayList<CoordinatesElement<E>>();

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = 0d;
        double maxY = 0d;

        for(var c : elements()) {
            if(condition.test(c)) {
                l.add(c);
                minX = Math.min(minX, c.getX());
                maxX = Math.max(maxX, c.getX());
                minY = Math.min(minY, c.getY());
                maxY = Math.max(maxY, c.getY());
            }
        }

        if(l.isEmpty()) {
            return false;
        }

        elements().removeAll(l);
        modified();
        fireRmEvent(l, new Point2D(minX, minY), new Point2D(maxX, maxY));
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if it was modified as a result of calling this.
     */
    @Override
    public boolean clear() {
        var l = List.copyOf(elements());
        
        elements().clear();
        modified();
        
        var p = new Point2D(maxWidth.get(), maxHeight.get());
        maxWidth.set(0);
        maxHeight.set(0);
        
        fireRmEvent(l, new Point2D(0, 0), p);
        return true;
    }

    
    /**
     * Notifies all the addition consumers.
     */
    private void fireAddEvent(List<CoordinatesElement<E>> elts, Point2D topLeft, Point2D bottomRight) {
        var e = StructureChange.added(this, elts, topLeft, bottomRight);
        for(var l : addConsumers.values()) {
            l.forEach(c -> c.accept(e));
        }
    }

    /**
     * Notifies all the removal consumers.
     */
    private void fireRmEvent(List<CoordinatesElement<E>> elts, Point2D topLeft, Point2D bottomRight) {
        var e = StructureChange.removed(this, elts, topLeft, bottomRight);
        for(var l : rmConsumers.values()) {
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
    public void addAdditionListener(Object owner, Consumer<? super StructureChange.Addition<E>> action) {
        addConsumers.computeIfAbsent(owner, l -> new ArrayList<>());
        addConsumers.get(owner).add(action);  
    }

    /**
     * {@inheritDoc}
     *
     * @param owner  The listener owner.
     * @param action The action to stop doing.
     */
    @Override
    public void removeAdditionListener(Object owner, Consumer<? super StructureChange.Addition<E>> action) {
        var l = addConsumers.get(owner);
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
    public Collection<Consumer<? super StructureChange.Addition<E>>> clearAdditionListeners(Object owner) {
        var l =  addConsumers.remove(owner);
        return (l != null) ? l : List.of();
    }

    /**
     * {@inheritDoc}
     *
     * @return The removed listeners.
     */
    @Override
    public Collection<Consumer<? super StructureChange.Addition<E>>> clearAdditionListeners() {
        var l = new ArrayList<Consumer<? super StructureChange.Addition<E>>>();
        addConsumers.values().forEach(l::addAll);
        addConsumers.clear();
        return l;
    }

    /**
     * {@inheritDoc}
     *
     * @param owner  The listener owner.
     * @param action The action to perform.
     */
    @Override
    public void addRemovalListener(Object owner, Consumer<? super StructureChange.Removal<E>> action) {
        rmConsumers.computeIfAbsent(owner, l -> new ArrayList<>());
        rmConsumers.get(owner).add(action);
    }

    /**
     * {@inheritDoc}
     *
     * @param owner  The listener owner.
     * @param action The action to stop doing.
     */
    @Override
    public void removeRemovalListener(Object owner, Consumer<? super StructureChange.Removal<E>> action) {
        var l = rmConsumers.get(owner);
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
    public Collection<Consumer<? super StructureChange.Removal<E>>> clearRemovalListeners(Object owner) {
        var l =  rmConsumers.remove(owner);
        return (l != null) ? l : List.of();
    }

    /**
     * {@inheritDoc}
     *
     * @return The removed listeners.
     */
    @Override
    public Collection<Consumer<? super StructureChange.Removal<E>>> clearRemovalListeners() {
        var l = new ArrayList<Consumer<? super StructureChange.Removal<E>>>();
        rmConsumers.values().forEach(l::addAll);
        rmConsumers.clear();
        return l;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return an Iterator.
     */
    @Override
    public CoordinatesIterator<CoordinatesElement<E>> iterator() {
        return new RemoverIterator();
    }

    
    /**
     * Extension of {@link io.github.vqnxiv.structure.impl.LayoutableList.LayoutableIterator}
     * to support element removal.
     */
    private class RemoverIterator extends LayoutableIterator {
        
        /**
         * Constructor.
         */
        protected RemoverIterator() {
            super();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            // todo: event
            internalItr().remove();
            nullLast();
        }
    }
}
