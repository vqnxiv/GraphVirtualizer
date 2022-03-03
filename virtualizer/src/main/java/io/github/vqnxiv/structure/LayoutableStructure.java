package io.github.vqnxiv.structure;


import io.github.vqnxiv.structure.impl.LayoutableList;
import javafx.geometry.Point2D;

import java.util.Map;


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
}
