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
     * @param maxWidth  Maximum allowed width.
     * @param maxHeight Maximum allowed height.
     */
    void applyWithinBounds(double maxWidth, double maxHeight);
    
    /**
     * Maximum allowed width offset when
     * positioning the elements of the structure.
     * 
     * @return Maximum allowed width offset.
     */
    double getMaxAllowedWidth();

    /**
     * Sets the maximum allowed width offset.
     * 
     * @param maxAllowedWidth New maximum allowed width offset.
     */
    void setMaxAllowedWidth(double maxAllowedWidth);

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
    double getMaxAllowedHeight();

    /**
     * Sets the maximum allowed height offset.
     * 
     * @param maxAllowedHeight New maximum allowed height offset.
     */
    void setMaxAllowedHeight(double maxAllowedHeight);

    /**
     * Property of the maximum allowed height offset.
     *
     * @return Property of the maximum allowed height offset.
     */
    DoubleProperty maxAllowedHeight();
}
