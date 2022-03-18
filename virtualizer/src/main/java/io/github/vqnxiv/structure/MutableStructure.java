package io.github.vqnxiv.structure;


import javafx.geometry.Point2D;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Mutable extension of {@link LayoutableStructure}.
 * 
 * @param <E> Type of elements.
 *           
 * @see LayoutableStructure
 * @see CoordinatesStructure
 */
public interface MutableStructure<E> extends LayoutableStructure<E> {

    /**
     * Add an element.
     * 
     * @param element The element to add.
     * @return {@code true} if it was successfully added.
     */
    default boolean add(E element) {
        return addAt(new CoordinatesElement<>(element));
    }

    /**
     * Adds all the given elements.
     * 
     * @param elements The elements to add.
     * @return {@code true} if at least one element was successfully added.
     */
    default boolean addAll(Collection<E> elements) {
        return addAllAt(
            elements.stream().map(CoordinatesElement::new).toList()
        );
    }

    /**
     * Adds the given element at the given position.
     * The element may be repositioned by any layout observing
     * the structure.
     * 
     * @param element     The element to add.
     * @param coordinates Its coordinates.
     * @return {@code true} if it was successfully added.
     */
    default boolean addAt(E element, Point2D coordinates) {
        return addAt(new CoordinatesElement<>(element, coordinates));
    }

    /**
     * Adds the given element at the given position. Gets
     * overridden by a layout if there is one.
     *
     * @param element The element to add.
     * @return {@code true} if it was successfully added.
     */
    default boolean addAt(CoordinatesElement<E> element) {
        return addAllAt(List.of(element));
    }
    
    /**
     * Adds all the given elements at the given positions.
     * The elements may be repositioned by any layout observing
     * the structure.
     *
     * @param elements The elements to add.
     * @return {@code true} if at least one element was successfully added.
     */
    default boolean addAllAt(Map<E, Point2D> elements) {
        return addAllAt(
            elements
                .entrySet()
                .stream()
                .map(e -> new CoordinatesElement<>(e.getKey(), e.getValue()))
                .toList()
        );
    }

    /**
     * Adds all the given elements at the given positions.
     * The elements may be repositioned by any layout observing
     * the structure.
     * 
     * @param elements The elements to add.
     * @return {@code true} if at least one element was successfully added.
     */
    boolean addAllAt(Collection<CoordinatesElement<E>> elements);

    
    /**
     * Removes the given element.
     * 
     * @param element The element to remove.
     * @return {@code true} if the element was successfully removed.
     */
    boolean remove(E element);

    /**
     * Removes the given elements.
     *
     * @param elements The element to remove.
     * @return {@code true} if at least one element was successfully removed.
     */
    boolean removeAll(Collection<E> elements);

    /**
     * Removes the given element.
     *
     * @param element The element to remove.
     * @return {@code true} if the element was successfully removed.
     */
    boolean removeAt(CoordinatesElement<E> element);

    /**
     * Removes the given elements.
     *
     * @param elements The element to remove.
     * @return {@code true} if at least one element was successfully removed.
     */
    boolean removeAllAt(Collection<CoordinatesElement<E>> elements);

    /**
     * Removes all the elements that match the given condition.
     * 
     * @param condition Filtering condition.
     * @return {@code true} if at least one element was successfully removed.
     */
    boolean removeIf(Predicate<E> condition);

    /**
     * Removes all the elements that match the given condition.
     *
     * @param condition Filtering condition.
     * @return {@code true} if at least one element was successfully removed.
     */
    boolean removeCoordinatesIf(Predicate<CoordinatesElement<E>> condition);

    /**
     * Clears the structure.
     * 
     * @return {@code true} if it was modified as a result of calling this.
     */
    boolean clear();


    /**
     * The structure will perform the given action when one or more
     * elements are added. <br>
     * The given action should avoid changing the structure.
     * <p>
     * Multiple actions can be given to a single structure, and a
     * single action can be given to multiple structures.
     *
     * @param owner  The listener owner.
     * @param action The action to perform.
     */
    void addAdditionListener(Object owner, Consumer<? super StructureChange.Addition<E>> action);

    /**
     * The structure will no longer perform the given action when one or more
     * elements are added. This requires giving the <u>exact same</u>
     * consumer as the one that was passed to {@link #addAdditionListener(Object, Consumer)}.
     *
     * @param owner  The listener owner.
     * @param action The action to stop doing.
     */
    void removeAdditionListener(Object owner, Consumer<? super StructureChange.Addition<E>> action);

    /**
     * Removes and returns the move listeners from the given owner attached to this structure.
     *
     * @param owner The listeners owner.
     * @return The removed listeners.
     */
    Collection<Consumer<? super StructureChange.Addition<E>>> clearAdditionListeners(Object owner);

    /**
     * Removes and returns the move listeners attached to this structure.
     *
     * @return The removed listeners.
     */
    Collection<Consumer<? super StructureChange.Addition<E>>> clearAdditionListeners();


    /**
     * The structure will perform the given action when one or more
     * elements are removed. <br>
     * The given action should avoid changing the structure.
     * <p>
     * Multiple actions can be given to a single structure, and a
     * single action can be given to multiple structures.
     *
     * @param owner  The listener owner.
     * @param action The action to perform.
     */
    void addRemovalListener(Object owner, Consumer<? super StructureChange.Removal<E>> action);

    /**
     * The structure will no longer perform the given action when one or more
     * elements are removed. This requires giving the <u>exact same</u>
     * consumer as the one that was passed to {@link #addRemovalListener(Object, Consumer)}.
     *
     * @param owner  The listener owner.
     * @param action The action to stop doing.
     */
    void removeRemovalListener(Object owner, Consumer<? super StructureChange.Removal<E>> action);

    /**
     * Removes and returns the move listeners from the given owner attached to this structure.
     *
     * @param owner The listeners owner.
     * @return The removed listeners.
     */
    Collection<Consumer<? super StructureChange.Removal<E>>> clearRemovalListeners(Object owner);

    /**
     * Removes and returns the move listeners attached to this structure.
     *
     * @return The removed listeners.
     */
    Collection<Consumer<? super StructureChange.Removal<E>>> clearRemovalListeners();

}
