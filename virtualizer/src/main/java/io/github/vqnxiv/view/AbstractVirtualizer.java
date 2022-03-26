package io.github.vqnxiv.view;


import io.github.vqnxiv.misc.BoundedDoubleProperty;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;


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
    protected final BoundedDoubleProperty totalHeight = new BoundedDoubleProperty(0, Double.MAX_VALUE);

    /**
     * The total size of the content.
     */
    protected final BoundedDoubleProperty totalWidth = new BoundedDoubleProperty(0, Double.MAX_VALUE);
    
    /**
     * The coordinates of the area this object is displaying.
     */
    protected final BoundedDoubleProperty heightOffset = new BoundedDoubleProperty(0, Double.MAX_VALUE);

    /**
     * The coordinates of the area this object is displaying.
     */
    protected final BoundedDoubleProperty widthOffset = new BoundedDoubleProperty(0, Double.MAX_VALUE);

    /**
     * The height of the area that this object is displaying.
     */
    protected final BoundedDoubleProperty viewHeight = new BoundedDoubleProperty(0, Double.MAX_VALUE);

    /**
     * The width of the area that this object is displaying.
     */
    protected final BoundedDoubleProperty viewWidth = new BoundedDoubleProperty(0, Double.MAX_VALUE);

    /**
     * So we can fire only one abstract onChanged when setting both height and width
     */
    private boolean skipNext = false;
    
    
    /**
     * Constructor.
     */
    protected AbstractVirtualizer() {
        totalWidth.addListener(obs -> updateTotal(viewWidth, totalWidth, widthOffset));
        totalHeight.addListener(obs -> updateTotal(viewHeight, totalHeight, heightOffset));
        
        widthOffset.addListener(this::updateOffset);
        heightOffset.addListener(this::updateOffset);
        
        viewWidth.addListener(obs -> updateView(viewWidth, totalWidth, widthOffset));
        viewHeight.addListener(obs -> updateView(viewHeight, totalHeight, heightOffset));
    }


    /**
     * Called when a total dimension might be changed.
     * 
     * @param view View dimension.
     * @param total Total dimension.
     * @param offset Offset dimension.
     */
    private void updateTotal(BoundedDoubleProperty view, DoubleProperty total, BoundedDoubleProperty offset) {
        view.setMax(total.get());
        offset.setMax(total.get() - view.get());
        onTotalChanged();
    }

    /**
     * Called when an offset dimension might be changed.
     * 
     * @param obs Ignored.
     */
    private void updateOffset(Observable obs) {
        if(!skipNext) {
            onOffsetChanged();
        }
    }

    /**
     * Called when a view dimension might be changed.
     *
     * @param view View dimension.
     * @param total Total dimension.
     * @param offset Offset dimension.
     */
    private void updateView(DoubleProperty view, DoubleProperty total, BoundedDoubleProperty offset) {
        try {
            offset.setMax(total.get() - view.get());
            onViewChanged();
        } catch(IllegalArgumentException ignored) {
            offset.setMax(0);
            onViewChanged();
        }
    }
    
    
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
     * @return The view height property.
     */
    @Override
    public DoubleProperty viewHeight() {
        return viewHeight;
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
     * @return The height offset property.
     */
    @Override
    public DoubleProperty heightOffset() {
        return heightOffset;
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
     * @return The total height property.
     */
    @Override
    public DoubleProperty totalHeight() {
        return totalHeight;
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
