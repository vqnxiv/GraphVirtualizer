package io.github.vqnxiv.structure;


import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Point2D;

import java.util.Collection;


/**
 * Structure which contains a set of elements and their coordinates.
 * 
 * @param <E> Type of elements.
 *           
 * @see CoordinatesElement
 * @see LocalizedStructure
 * @see LayoutableStructure
 * @see MutableStructure
 * @see CoordinatesIterable
 * @see CoordinatesIterator
 */
// todo: extends Collection<E>? w/ default throws for all mutable methods
// ==> extends Collection<E>, Collection<CoordinatesElement<E>>
// potential erasure clash? (removeAll, retainAll, containsAll, addAll)
// iterator() clash + other signatures too? spliterator, stream, toArray, etc
    
// tbf i think no collection is the way to go? too many name clashes so idk
public interface CoordinatesStructure<E> extends CoordinatesIterable<CoordinatesElement<E>> {

    
    // todo: predicate versions of between()
    // potential todo: outOf() = opposite of between?
    
    /**
     * Gets all elements between the given coordinates.
     *
     * @param topLeftX      Top left corner X coordinate.
     * @param topLeftY      Top left corner Y coordinate.
     * @param bottomRightX  Bottom right corner X coordinate.
     * @param bottomRightY  Bottom right corner Y coordinate.
     * @return Collection of all elements within the area.
     */
    Collection<CoordinatesElement<E>> between(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY);

    /**
     * Gets all elements between the given coordinates.
     * 
     * @param topLeft       Top left corner.
     * @param bottomRight   Bottom right corner.
     * @return Collection of all elements within the area.
     */
    Collection<CoordinatesElement<E>> between(Point2D topLeft, Point2D bottomRight);

    /**
     * Maximum height of this structure.
     * 
     * @return Max height property.
     */
    ReadOnlyDoubleProperty maximumHeight();

    /**
     * Maxmimum width of this structure.
     * 
     * @return Max width property.
     */
    ReadOnlyDoubleProperty maximumWidth();
}
