package io.github.vqnxiv.structure;


import io.github.vqnxiv.structure.impl.LayoutableList;
import javafx.geometry.Point2D;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Interface which allows the repositioning of a structure's elements, 
 * so that it can be used with a {@link io.github.vqnxiv.layout.Layout}.
 * <p>
 * When a structure is modified (reposition, add, remove, clear),
 * it usually happens in the following order:
 * <ol>
 *     <li>the actual internal change happens</li>
 *     <li>the structure's dimension properties are updated</li>
 *     <li>the structure is internally marked as modified</li>
 *     <li>the listeners/consumers are notified</li>
 * </ol>
 * 
 * @param <E> Type of elements.
 *           
 * @see CoordinatesStructure
 * @see LayoutableList
 */
public interface LayoutableStructure<E> extends CoordinatesStructure<E> {

    
    /**
     * Reposition one element to the given coordinates.
     *
     * @param e The elements with its old coordinates.
     * @param x New X coordinate.
     * @param y New Y coordinate.
     */
    default void repositionTo(CoordinatesElement<E> e, double x, double y) {
        repositionTo(e, new Point2D(x, y));
    }
    
    /**
     * Reposition one element to the given coordinates.
     * 
     * @param e The elements with its old coordinates.
     * @param p The new coordinates for the element.
     */
    void repositionTo(CoordinatesElement<E> e, Point2D p);

    /**
     * Reposition multiple elements to the given coordinates.
     * 
     * @param m Elements with their old coordinates mapped to
     * their new coordinates.
     */
    void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m);


    /**
     * The structure will perform the given action when one or more
     * elements are repositioned. <br>
     * The given action should avoid changing the structure.
     * <p>
     * Multiple actions can be given to a single structure, and a
     * single action can be given to multiple structures.
     *
     * @param owner  The listener owner.
     * @param action The action to perform.
     */
    void addMoveListener(Object owner, Consumer<? super StructureChange.Move<E>> action);

    /**
     * The structure will no longer perform the given action when one or more
     * elements are repositioned. This requires giving the <u>exact same</u>
     * consumer as the one that was passed to {@link #addMoveListener(Object, Consumer)}.
     * 
     * @param owner  The listener owner.
     * @param action The action to stop doing.
     */
    void removeMoveListener(Object owner, Consumer<? super StructureChange.Move<E>> action);

    /**
     * Removes and returns the move listeners from the given owner attached to this structure.
     * 
     * @param owner The listeners owner.
     * @return The removed listeners.
     */
    Collection<Consumer<? super StructureChange.Move<E>>> clearMoveListeners(Object owner);
    
    /**
     * Removes and returns the move listeners attached to this structure.
     * 
     * @return The removed listeners.
     */
    Collection<Consumer<? super StructureChange.Move<E>>> clearMoveListeners();
}
