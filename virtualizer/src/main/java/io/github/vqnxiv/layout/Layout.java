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
     * Minimum used width offset in the last 
     * {@link #apply()} pass.
     *
     * @return Minimum used width offset.
     */
    default double getMinUsedWidth() {
        return minUsedWidth().get();
    }

    /**
     * Property of the minimum used width offset.
     *
     * @return Property of the minimum used width offset.
     */
    ReadOnlyDoubleProperty minUsedWidth();

    /**
     * Minimum used height offset in the last 
     * {@link #apply()} pass.
     *
     * @return Minimum used height offset.
     */
    default double getMinUsedHeight() {
        return minUsedHeight().get();
    }

    /**
     * Property of the minimum used height offset.
     *
     * @return Property of the minimum used height offset.
     */
    ReadOnlyDoubleProperty minUsedHeight();
    
    /**
     * Maximum used width offset in the last 
     * {@link #apply()} pass.
     * 
     * @return Maximum used width offset.
     */
    default double getMaxUsedWidth() {
        return maxUsedWidth().get();
    }

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
    default double getMaxUsedHeight() {
        return maxUsedHeight().get();
    }

    /**
     * Property of the maximum used height offset.
     *
     * @return Property of the maximum used height offset.
     */
    ReadOnlyDoubleProperty maxUsedHeight();
}
