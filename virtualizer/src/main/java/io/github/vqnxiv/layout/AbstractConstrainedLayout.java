package io.github.vqnxiv.layout;


import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


/**
 * Boilerplate code for a {@link ConstrainedLayout} implementation.
 *
 * @param <E> Type of {@link io.github.vqnxiv.structure.LayoutableStructure}
 * elements.
 *
 * @see ConstrainedLayout
 * @see AbstractLayout
 */
public abstract class AbstractConstrainedLayout<E> extends AbstractLayout<E> implements ConstrainedLayout<E> {

    /**
     * Minimum allowed width property.
     */
    protected final DoubleProperty minAllowedWidth;

    /**
     * Minimum allowed height property.
     */
    protected final DoubleProperty minAllowedHeight;
    
    /**
     * Maximum allowed width property.
     */
    protected final DoubleProperty maxAllowedWidth;

    /**
     * Maximum allowed height property.
     */
    protected final DoubleProperty maxAllowedHeight;

    
    /**
     * Constructor.
     * 
     * @param s Structure.
     */
    protected AbstractConstrainedLayout(LayoutableStructure<E> s) {
        this(s, 0d, 0d, Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param s Structure.
     * @param minWidth  Initial minimum allowed width.
     * @param minHeight Initial minimum allowed height.
     * @param maxWidth  Initial maximum allowed width.
     * @param maxHeight Initial maximum allowed height.
     */
    protected AbstractConstrainedLayout(LayoutableStructure<E> s,
                                        double minWidth, double minHeight,
                                        double maxWidth, double maxHeight) {
        super(s);
        minAllowedWidth = new SimpleDoubleProperty(minWidth);
        minAllowedHeight = new SimpleDoubleProperty(minHeight);
        maxAllowedWidth = new SimpleDoubleProperty(maxWidth);
        maxAllowedHeight = new SimpleDoubleProperty(maxHeight);
    }


    /**
     * {@inheritDoc}
     *
     * @return Property of the minimum allowed width offset.
     */
    @Override
    public DoubleProperty minAllowedWidth() {
        return minAllowedWidth;
    }

    /**
     * {@inheritDoc}
     *
     * @return Property of the minimum allowed height offset.
     */
    @Override
    public DoubleProperty minAllowedHeight() {
        return minAllowedHeight;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return Property of the maximum allowed width offset.
     */
    @Override
    public DoubleProperty maxAllowedWidth() {
        return maxAllowedWidth;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return Property of the maximum allowed height offset.
     */
    @Override
    public DoubleProperty maxAllowedHeight() {
        return maxAllowedHeight;
    }
}
