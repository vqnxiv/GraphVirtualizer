package io.github.vqnxiv.view;


import io.github.vqnxiv.misc.BoundedDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;


/**
 * Contains boilerplate code for a Virtualizer implementation.
 * 
 * @see NodeVirtualizer
 * @see ThrottledNodeVirtualizer
 */
public abstract class AbstractVirtualizer implements Virtualizer {

    /**
     * The total height of the content.
     */
    protected final BoundedDoubleProperty totalHeight = new BoundedDoubleProperty(0, Double.MAX_VALUE) {
        // automatically updates the max value for the constrained dimensions
        // and for the max offset
        @Override
        protected void invalidated() {
            viewHeight.setMax(get());
            heightOffset.setMax(get() - viewHeight.get());
            onTotalChanged();
        }
    };

    /**
     * The total size of the content.
     */
    protected final BoundedDoubleProperty totalWidth = new BoundedDoubleProperty(0, Double.MAX_VALUE) {
        @Override
        protected void invalidated() {
            viewWidth.setMax(get());
            widthOffset.setMax(get() - viewWidth.get());
            onTotalChanged();
        }
    };

    /**
     * The coordinates of the area this object is displaying.
     */
    protected final BoundedDoubleProperty heightOffset = new BoundedDoubleProperty(0, Double.MAX_VALUE) {
        @Override
        protected void invalidated() {
            if(!skipNext) {
                onOffsetChanged();
            }
        }
    };

    /**
     * The coordinates of the area this object is displaying.
     */
    protected final BoundedDoubleProperty widthOffset = new BoundedDoubleProperty(0, Double.MAX_VALUE) {
        @Override
        protected void invalidated() {
            if(!skipNext) {
                onOffsetChanged();
            }
        }
    };

    /**
     * The height of the area that this object is displaying.
     */
    protected final BoundedDoubleProperty viewHeight = new BoundedDoubleProperty(0, Double.MAX_VALUE) {
        @Override
        protected void invalidated() {
            heightOffset.setMax(totalHeight.get() - get());
            onViewChanged();
        }
    };

    /**
     * The width of the area that this object is displaying.
     */
    protected final BoundedDoubleProperty viewWidth = new BoundedDoubleProperty(0, Double.MAX_VALUE) {
        @Override
        protected void invalidated() {
            widthOffset.setMax(totalWidth.get() - get());
            onViewChanged();
        }
    };


    /**
     * So we can fire only one abstract onChanged when setting both height and width
     */
    private boolean skipNext = false;
    
    
    /**
     * Constructor.
     */
    protected AbstractVirtualizer() {}
    
    
    /**
     * Abstract method called whenever one of the offsets may have changed.
     */
    protected abstract void onOffsetChanged();

    /**
     * Abstract method called whenever the total width or height may have changed.
     */
    protected abstract void onTotalChanged();

    /**
     * Abstract method called whenever the viewed width or height may have changed.
     */
    protected abstract void onViewChanged();


    /**
     * {@inheritDoc}
     * 
     * @param height Height offset.
     */
    @Override
    public void shiftHeightBy(double height) {
        heightOffset.set(heightOffset.get() - height);
    }

    /**
     * {@inheritDoc}
     * 
     * @param width Width offset.
     */
    @Override
    public void shiftWidthBy(double width) {
        widthOffset.set(widthOffset.get() - width);
    }

    /**
     * {@inheritDoc}
     * 
     * @param height Height offset.
     * @param width  Width offset.
     */
    @Override
    public void shiftBy(double height, double width) {
        skipNext = true;
        shiftHeightBy(height);
        skipNext = false;
        shiftWidthBy(width);
    }

    /**
     * {@inheritDoc}
     * 
     * @param p Point2D to retrieve the offsets from.
     */
    @Override
    public void shiftBy(Point2D p) {
        shiftBy(p.getX(), p.getY());
    }

    /**
     * {@inheritDoc}
     * 
     * @return The view height.
     */
    @Override
    public double getViewHeight() {
        return viewHeight.get();
    }

    /**
     * {@inheritDoc}
     * 
     * @return The view height property.
     */
    @Override
    public DoubleProperty viewHeight() {
        return viewHeight;
    }

    /**
     * {@inheritDoc}
     * 
     * @return The view width.
     */
    @Override
    public double getViewWidth() {
        return viewWidth.get();
    }

    /**
     * {@inheritDoc}
     * 
     * @return The view width property.
     */
    @Override
    public DoubleProperty viewWidth() {
        return viewWidth;
    }

    /**
     * {@inheritDoc}
     * 
     * @return The height offset.
     */
    @Override
    public double getHeightOffset() {
        return heightOffset.get();
    }

    /**
     * {@inheritDoc}
     *
     * @return The height offset property.
     */
    @Override
    public DoubleProperty heightOffset() {
        return heightOffset;
    }

    /**
     * {@inheritDoc}
     *
     * @return The width offser.
     */
    @Override
    public double getWidthOffset() {
        return widthOffset.get();
    }
    
    /**
     * {@inheritDoc}
     *
     * @return The width offset property.
     */
    @Override
    public DoubleProperty widthOffset() {
        return widthOffset;
    }

    /**
     * {@inheritDoc}
     *
     * @return The total height.
     */
    @Override
    public double getTotalHeight() {
        return totalHeight.get();
    }

    /**
     * {@inheritDoc}
     *
     * @return The total height property.
     */
    @Override
    public DoubleProperty totalHeight() {
        return totalHeight;
    }

    /**
     * {@inheritDoc}
     *
     * @return The total width.
     */
    @Override
    public double getTotalWidth() {
        return totalWidth.get();
    }

    /**
     * {@inheritDoc}
     *
     * @return The total width property.
     */
    @Override
    public DoubleProperty totalWidth() {
        return totalWidth;
    }
}
