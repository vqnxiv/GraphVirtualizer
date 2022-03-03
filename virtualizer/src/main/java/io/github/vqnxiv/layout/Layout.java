package io.github.vqnxiv.layout;


import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.beans.property.ReadOnlyDoubleProperty;


/**
 * Interface which sets the coordinates of the elements
 * of a {@link io.github.vqnxiv.structure.CoordinatesStructure}.
 * 
 * @param <E> Type of elements of the {@link io.github.vqnxiv.structure.CoordinatesStructure}.
 *           
 * @see ConstrainedLayout
 * @see AbstractLayout
 */
public interface Layout<E> {

    /**
     * Applies this layout to its structure.
     */
    void apply();

    /**
     * Getter for this layout's structure.
     * 
     * @return This layout's structure.
     */
    LayoutableStructure<E> getStructure();
    
    /**
     * Maximum used width offset in the last 
     * {@link #apply()} pass.
     * 
     * @return Maximum used width offset.
     */
    double getMaxUsedWidth();

    /**
     * Property of the maximum used width offset.
     * 
     * @return Property of the maximum used width offset.
     */
    ReadOnlyDoubleProperty maxUsedWidth();

    /**
     * Maximum used height offset in the last 
     * {@link #apply()} pass.
     *
     * @return Maximum used height offset.
     */
    double getMaxUsedHeight();

    /**
     * Property of the maximum used height offset.
     *
     * @return Property of the maximum used height offset.
     */
    ReadOnlyDoubleProperty maxUsedHeight();
}
