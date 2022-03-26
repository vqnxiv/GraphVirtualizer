package io.github.vqnxiv.layout;


import javafx.beans.property.DoubleProperty;


/**
 * Interface which sets the coordinates of the elements
 * of a {@link io.github.vqnxiv.structure.LayoutableStructure} within its given bounds.
 *
 * @param <E> Type of elements of the {@link io.github.vqnxiv.structure.LayoutableStructure}.
 */
public interface ConstrainedLayout<E> extends Layout<E> {

    /**
     * Applies this layout to the given structure
     * within its current bounds.
     *
     */
    @Override
    void apply();

    /**
     * Applies this layout to its structure
     * within the given bounds and sets its maximum
     * allowed width and height to the given ones.
     * 
     * @param minWidth  Minimum allowed width.
     * @param minHeight Minimum allowed height.
     * @param maxWidth  Maximum allowed width.
     * @param maxHeight Maximum allowed height.
     */
    default void applyWithinBounds(double minWidth, double minHeight, double maxWidth, double maxHeight) {
        setMinAllowedWidth(minWidth);
        setMinAllowedHeight(minHeight);
        setMaxAllowedWidth(maxWidth);
        setMaxAllowedHeight(maxHeight);
        apply();
    }

    /**
     * Minimum allowed width offset when
     * positioning the elements of the structure.
     *
     * @return Minimum allowed width offset.
     */
    default double getMinAllowedWidth() {
        return minAllowedWidth().get();
    }

    /**
     * Sets the minimum allowed width offset.
     *
     * @param minAllowedWidth New minimum allowed width offset.
     */
    default void setMinAllowedWidth(double minAllowedWidth) {
        minAllowedWidth().set(minAllowedWidth);
    }

    /**
     * Property of the minimum allowed width offset.
     *
     * @return Property of the minimum allowed width offset.
     */
    DoubleProperty minAllowedWidth();

    /**
     * Minimum allowed height offset when
     * positioning the elements of the structure.
     *
     * @return Minimum allowed height offset.
     */
    default double getMinAllowedHeight() {
        return minAllowedHeight().get();
    }

    /**
     * Sets the minimum allowed height offset.
     *
     * @param minAllowedHeight New minimum allowed height offset.
     */
    default void setMinAllowedHeight(double minAllowedHeight) {
        minAllowedHeight().set(minAllowedHeight);
    }

    /**
     * Property of the minimum allowed height offset.
     *
     * @return Property of the minimum allowed height offset.
     */
    DoubleProperty minAllowedHeight();
    
    /**
     * Maximum allowed width offset when
     * positioning the elements of the structure.
     * 
     * @return Maximum allowed width offset.
     */
    default double getMaxAllowedWidth() {
        return maxAllowedWidth().get();
    }

    /**
     * Sets the maximum allowed width offset.
     * 
     * @param maxAllowedWidth New maximum allowed width offset.
     */
    default void setMaxAllowedWidth(double maxAllowedWidth) {
        maxAllowedWidth().set(maxAllowedWidth);
    }

    /**
     * Property of the maximum allowed width offset.
     * 
     * @return Property of the maximum allowed width offset.
     */
    DoubleProperty maxAllowedWidth();
    
    /**
     * Maximum allowed height offset when
     * positioning the elements of the structure.
     *
     * @return Maximum allowed height offset.
     */
    default double getMaxAllowedHeight() {
        return maxAllowedHeight().get();
    }

    /**
     * Sets the maximum allowed height offset.
     * 
     * @param maxAllowedHeight New maximum allowed height offset.
     */
    default void setMaxAllowedHeight(double maxAllowedHeight) {
        maxAllowedHeight().set(maxAllowedHeight);
    }

    /**
     * Property of the maximum allowed height offset.
     *
     * @return Property of the maximum allowed height offset.
     */
    DoubleProperty maxAllowedHeight();
}
