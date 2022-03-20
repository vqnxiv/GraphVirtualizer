package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.Layout;
import io.github.vqnxiv.structure.*;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Mutable extension of {@link LayoutableMatrix}.
 *
 * @param <E> Type of elements.
 *
 * @see LayoutableMatrix
 * @see MutableStructure
 */
public class MutableMatrix<E> extends LayoutableMatrix<E> implements MutableStructure<E> {

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
    public MutableMatrix(Collection<E> el) {
        super(el);
    }

    /**
     * Constructor.
     *
     * @param el             Elements
     * @param layoutSupplier Initial layout.
     */
    public MutableMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier) {
        super(el, layoutSupplier);
    }

    /**
     * Elements copy constructor.
     *
     * @param c Structure to copy.
     */
    public MutableMatrix(CoordinatesStructure<E> c) {
        super(c);
    }

    /**
     * Copy constructor.
     *
     * @param m Matrix to copy.
     */
    public MutableMatrix(CoordinatesMatrix<E> m) {
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
    public MutableMatrix(Collection<E> el, 
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
    public MutableMatrix(CoordinatesStructure<E> el, 
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
    public MutableMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier, 
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
    public MutableMatrix(CoordinatesStructure<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier, 
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
     * @param element The element to add.
     * @return {@code true} if it was successfully added.
     */
    @Override
    public boolean addAt(CoordinatesElement<E> element) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param coordinatesElements The elements to add.
     * @return {@code true} if at least one element was successfully added.
     */
    @Override
    public boolean addAllAt(Collection<CoordinatesElement<E>> coordinatesElements) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param element The element to remove.
     * @return {@code true} if the element was successfully removed.
     */
    @Override
    public boolean remove(E element) {
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
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param element The element to remove.
     * @return {@code true} if the element was successfully removed.
     */
    @Override
    public boolean removeAt(CoordinatesElement<E> element) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param coordinatesElements The element to remove.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeAllAt(Collection<CoordinatesElement<E>> coordinatesElements) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param condition Filtering condition.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeIf(Predicate<E> condition) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param condition Filtering condition.
     * @return {@code true} if at least one element was successfully removed.
     */
    @Override
    public boolean removeCoordinatesIf(Predicate<CoordinatesElement<E>> condition) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if it was modified as a result of calling this.
     */
    @Override
    public boolean clear() {
        return false;
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
        rmConsumers.computeIfAbsent(owner, p -> new ArrayList<>());
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
    
    // todo: iterator
}
