package io.github.vqnxiv.structure;


import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Point2D;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;


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
public interface CoordinatesStructure<E> extends CoordinatesIterable<CoordinatesElement<E>> {
    
    /**
     * Gets all elements between the given coordinates.
     *
     * @param topLeftX      Top left corner X coordinate.
     * @param topLeftY      Top left corner Y coordinate.
     * @param bottomRightX  Bottom right corner X coordinate.
     * @param bottomRightY  Bottom right corner Y coordinate.
     * @return Collection of all elements within the area.
     */
    Collection<CoordinatesElement<E>> between(double topLeftX, double topLeftY, 
                                              double bottomRightX, double bottomRightY);

    /**
     * Gets all elements between the given coordinates.
     * 
     * @param topLeft       Top left corner.
     * @param bottomRight   Bottom right corner.
     * @return Collection of all elements within the area.
     */
    default Collection<CoordinatesElement<E>> between(Point2D topLeft, Point2D bottomRight) {
        return between(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
    }

    /**
     * Gets all elements between the given coordinates that match the given condition.
     *
     * @param topLeftX      Top left corner X coordinate.
     * @param topLeftY      Top left corner Y coordinate.
     * @param bottomRightX  Bottom right corner X coordinate.
     * @param bottomRightY  Bottom right corner Y coordinate.
     * @param condition     Filtering condition.
     * @return Collection of all elements within the area.
     */
    Collection<CoordinatesElement<E>> between(double topLeftX, double topLeftY,
                                              double bottomRightX, double bottomRightY,
                                              Predicate<E> condition);

    /**
     * Gets all elements between the given coordinates that match the given condition.
     *
     * @param topLeft       Top left corner.
     * @param bottomRight   Bottom right corner.
     * @param condition     Filtering condition.
     * @return Collection of all elements within the area.
     */
    default Collection<CoordinatesElement<E>> between(Point2D topLeft, Point2D bottomRight, 
                                              Predicate<E> condition) {
        return between(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY(), condition);
    }
    
    // todo: min height and width?
    
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

    /**
     * Returns {@code true} if this structure contains no elements.
     * 
     * @return {@code true} if this structure contains no elements.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of elements in this structure.
     * 
     * @return The number of elements in this structure.
     */
    int size();

    /**
     * Returns {@code true} if this structure contains the specified element.
     * 
     * @param element The element to search for.
     * @return {@code true} if this structure contains the specified element.
     */
    default boolean contains(E element) {
        return coordinatesOf(element).isPresent();
    }

    /**
     * Returns {@code true} if this structure contains the specified element
     * at the specified coordinates (as far as double precision goes).
     *
     * @param element The element to search for.
     * @return {@code true} if this structure contains the specified element.
     */
    boolean contains(CoordinatesElement<E> element);

    /**
     * Returns the coordinates of the given element if it is in the structure.
     * 
     * @param element The element to find.
     * @return The coordinates of the given element if it is in the structure.
     */
    Optional<CoordinatesElement<E>> coordinatesOf(E element);

    /**
     * Returns the coordinates of the given elements that are in the structure.
     *
     * @param elements The elements to find.
     * @return The coordinates of the given elements that are in the structure.
     */
    Map<E, CoordinatesElement<E>> coordinatesOf(Collection<E> elements);

}
