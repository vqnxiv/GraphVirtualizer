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
        maxUsedWidth = new SimpleDoubleProperty(0);
        maxUsedHeight = new SimpleDoubleProperty(0);
        
        //structure.maximumWidth().bind(maxUsedWidth);
        //structure.maximumHeight().bind(maxUsedHeight);
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
     * @return Maximum used width offset.
     */
    @Override
    public double getMaxUsedWidth() {
        return maxUsedWidth.get();
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
     * @return Maximum used height offset.
     */
    @Override
    public double getMaxUsedHeight() {
        return maxUsedHeight.get();
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
