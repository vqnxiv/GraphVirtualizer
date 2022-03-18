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
        this(s, Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param s Structure.
     * @param maxWidth  Initial maximum allowed width.
     * @param maxHeight Initial maximum allowed height.
     */
    protected AbstractConstrainedLayout(LayoutableStructure<E> s, double maxWidth, double maxHeight) {
        super(s);
        maxAllowedWidth = new SimpleDoubleProperty(maxWidth);
        maxAllowedHeight = new SimpleDoubleProperty(maxHeight);
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
