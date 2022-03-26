package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.Layout;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesIterator;
import io.github.vqnxiv.structure.CoordinatesStructure;
import io.github.vqnxiv.structure.LayoutableStructure;
import io.github.vqnxiv.structure.MutableStructure;
import io.github.vqnxiv.structure.StructureChange;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    
    /**
     * {@inheritDoc}
     *
     * @param element The element to add.
     * @return {@code true} if it was successfully added.
     */
    @Override
    public boolean addCoordinates(CoordinatesElement<E> element) {
        if(!elements().add(element)) {
            return false;
        }

        setDimensionsIfOutside(element);
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
    public boolean addAllCoordinates(Collection<CoordinatesElement<E>> coordinatesElements) {
        var l = new ArrayList<CoordinatesElement<E>>(coordinatesElements.size());

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        
        for(var c : coordinatesElements) {
            if(!elements().add(c)) {
                continue;
            }
            l.add(c);
            minX = Math.min(minX, c.getX());
            maxX = Math.max(maxX, c.getX());
            minY = Math.min(minY, c.getY());
            maxY = Math.max(maxY, c.getY());
            setDimensionsIfOutside(c);
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
    public boolean removeValue(E element) {
        CoordinatesElement<E> c = null;
        int i = 0;
        for(; i < elements().size(); i++) {
            if(elements().get(i).getElement().equals(element)) {
                c = elements().get(i);
                break;
            }
        }

        if(c == null) {
            return false;
        }
        
        elements().remove(i);
        if(isOnBound(c)) {
            updateDimensions();
        }
        modified();
        fireRmEvent(List.of(c), c.getXY(), c.getXY());
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param elements The element to remove.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeAllValues(Collection<E> elements) {
        var l = new ArrayList<CoordinatesElement<E>>(elements.size());
        
        for(var c : elements()) {
            if(elements.contains(c.getElement())) {
                l.add(c);
            }
        }

        return !(l.isEmpty()) && removeAllCoordinates(l);
    }

    /**
     * {@inheritDoc}
     *
     * @param element The element to remove.
     * @return {@code true} if the element was successfully removed.
     */
    @Override
    public boolean removeCoordinates(CoordinatesElement<E> element) {
        if(!elements().remove(element)) {
            return false;
        }

        if(isOnBound(element)) {
            updateDimensions();
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
    public boolean removeAllCoordinates(Collection<CoordinatesElement<E>> coordinatesElements) {
        var l = new ArrayList<CoordinatesElement<E>>(coordinatesElements.size());

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        boolean updateDimensions = false;

        for(var c : coordinatesElements) {
            if(!elements().remove(c)) {
                continue;
            }
            l.add(c);
            minX = Math.min(minX, c.getX());
            maxX = Math.max(maxX, c.getX());
            minY = Math.min(minY, c.getY());
            maxY = Math.max(maxY, c.getY());
            if(isOnBound(c)) {
                updateDimensions = true;
            }
        }

        if(l.isEmpty()) {
            return false;
        }

        if(updateDimensions) {
            updateDimensions();
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
    public boolean removeValuesIf(Predicate<? super E> condition) {
        return removeCoordinatesIf(c -> condition.test(c.getElement()));
    }

    /**
     * {@inheritDoc}
     *
     * @param condition Filtering condition.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeCoordinatesIf(Predicate<? super CoordinatesElement<E>> condition) {
        var l = new ArrayList<CoordinatesElement<E>>();

        for(var c : elements()) {
            if(condition.test(c)) {
                l.add(c);
            }
        }
        
        return !(l.isEmpty()) && removeAllCoordinates(l);
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

        var p1 = new Point2D(getMinimumWidth(), getMinimumHeight());
        var p2 = new Point2D(getMaximumWidth(), getMaximumHeight());
        
        setDimensions(0d, 0d, 0d, 0d);
        modified();
        fireRmEvent(l, p1, p2);
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
        addConsumers.computeIfAbsent(owner, o -> new ArrayList<>());
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
        rmConsumers.computeIfAbsent(owner, o -> new ArrayList<>());
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
    protected class RemoverIterator extends LayoutableIterator {
        
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
            internalItr().remove();
            fireRmEvent(List.of(getLast()), getLast().getXY(), getLast().getXY());
            nullLast();
            updateExpectedModCount();
        }
    }
}
