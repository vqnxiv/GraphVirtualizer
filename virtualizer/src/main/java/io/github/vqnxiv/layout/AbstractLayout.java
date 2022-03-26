package io.github.vqnxiv.layout;


import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


/**
 * Boilerplate code for a {@link Layout} implementation.
 * 
 * @param <E> Type of {@link io.github.vqnxiv.structure.LayoutableStructure}
 * elements.
 * 
 * @see Layout
 */
public abstract class AbstractLayout<E> implements Layout<E> {

    /**
     * Minimum used width property.
     */
    protected final DoubleProperty minUsedWidth;

    /**
     * Minimum used height property.
     */
    protected final DoubleProperty minUsedHeight;
    
    /**
     * Maximum used width property.
     */
    protected final DoubleProperty maxUsedWidth;

    /**
     * Maximum used height property.
     */
    protected final DoubleProperty maxUsedHeight;

    /**
     * Structure.
     */
    protected final LayoutableStructure<E> structure;

    
    /**
     * Constructor.
     * 
     * @param s Structure.
     */
    protected AbstractLayout(LayoutableStructure<E> s) {
        structure = s;
        minUsedWidth = new SimpleDoubleProperty(0);
        minUsedHeight = new SimpleDoubleProperty(0);
        maxUsedWidth = new SimpleDoubleProperty(0);
        maxUsedHeight = new SimpleDoubleProperty(0);
    }


    /**
     * Sets the used dimensions.
     * 
     * @param minWidth  Minimum used width.
     * @param minHeight Minimum used height.
     * @param maxWidth  Maximum used width.
     * @param maxHeight Maximum used height.
     */
    protected void setUsedDimensions(double minWidth, double minHeight, double maxWidth, double maxHeight) {
        minUsedWidth.set(minWidth);
        minUsedHeight.set(minHeight);
        maxUsedWidth.set(maxWidth);
        maxUsedHeight.set(maxHeight);
    }
    
    
    /**
     * {@inheritDoc}
     *
     * @return This layout's structure.
     */
    @Override
    public LayoutableStructure<E> getStructure() {
        return structure;
    }

    /**
     * {@inheritDoc}
     *
     * @return Property of the minimum used width offset.
     */
    @Override
    public ReadOnlyDoubleProperty minUsedWidth() {
        return minUsedWidth;
    }

    /**
     * {@inheritDoc}
     *
     * @return Property of the minimum used height offset.
     */
    @Override
    public ReadOnlyDoubleProperty minUsedHeight() {
        return minUsedHeight;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return Property of the maximum used width offset.
     */
    @Override
    public ReadOnlyDoubleProperty maxUsedWidth() {
        return maxUsedWidth;
    }

    /**
     * {@inheritDoc}
     *
     * @return Property of the maximum used height offset.
     */
    @Override
    public ReadOnlyDoubleProperty maxUsedHeight() {
        return maxUsedHeight;
    }
}
