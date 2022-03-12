package io.github.vqnxiv.structure;


import io.github.vqnxiv.structure.impl.LayoutableList;
import javafx.geometry.Point2D;

import java.util.Map;
import java.util.function.Consumer;


/**
 * Interface which allows the repositioning of a structure's elements, 
 * so that it can be used by a {@link io.github.vqnxiv.layout.Layout}.
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
    void repositionTo(CoordinatesElement<E> e, double x, double y);
    
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
     * @param action The action to perform.
     */
    void addMoveListener(Consumer<? super StructureChange.Move<E>> action);

    /**
     * The structure will no longer perform the given action when one or more
     * elements are repositioned. This requires giving the <u>exact same</u>
     * consumer as the one that was passed to {@link #addMoveListener(Consumer)}.
     * 
     * @param action The action to stop doing.
     */
    void removeMoveListener(Consumer<? super StructureChange.Move<E>> action);
}
