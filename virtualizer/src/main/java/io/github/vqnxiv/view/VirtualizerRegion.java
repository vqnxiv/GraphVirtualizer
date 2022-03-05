package io.github.vqnxiv.view;


import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import java.util.Objects;


/**
 * An {@link AbstractAbleRegion} around a {@link Virtualizer}.
 * 
 * @see AbstractAbleRegion
 * @see Virtualizer
 */
public class VirtualizerRegion extends AbstractAbleRegion {


    /**
     * The inner virtualizer whose view will be displayed in this region.
     */
    private final Virtualizer virtualizer;

    /**
     * The virtualizer view.
     */
    private final Node view;
    

    /**
     * Constructor.
     *
     * @param virtualizer Virtualizer.
     */
    public VirtualizerRegion(Virtualizer virtualizer) {
        super();

        Objects.requireNonNull(virtualizer, "Virtualizer cannot be null");
        this.virtualizer = virtualizer;
        view = virtualizer.getView();

        // the 'view' node might be larger than the virtualizer's viewHeight/viewWidth
        Rectangle clip = new Rectangle();
        
        // doesnt align when the bars are hidden. either set bar width to zero in parent's layoutChildren
        // or add a check in this layoutChildren?
        // or maybe make some 10km ugly binding
        clip.widthProperty().bind(widthProperty().subtract(vBar.widthProperty()).divide(zoomProperty()));
        clip.heightProperty().bind(heightProperty().subtract(hBar.heightProperty()).divide(zoomProperty()));
        
        view.setClip(clip);

        // scale for zooming
        Scale scale = new Scale();
        scale.xProperty().bind(zoomProperty());
        scale.yProperty().bind(zoomProperty());
        view.getTransforms().add(scale);
        
        virtualizer.viewHeight().bind(heightProperty().divide(zoomProperty()));
        virtualizer.viewWidth().bind(widthProperty().divide(zoomProperty()));

        hBar.maxProperty().bind(virtualizer.totalWidth().subtract(virtualizer.viewWidth()));
        vBar.maxProperty().bind(virtualizer.totalHeight().subtract(virtualizer.viewHeight()));

        hBar.valueProperty().bindBidirectional(virtualizer.widthOffset());
        vBar.valueProperty().bindBidirectional(virtualizer.heightOffset());

        getChildren().add(view);
    }


    /**
     * Layout method.
     */
    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        view.relocate(0, 0);
    }

    /**
     * Getter for the internal total width.
     *
     * @return {@link #virtualizer} total width.
     */
    @Override
    protected double internalTotalWidth() {
        return virtualizer.getTotalWidth();
    }

    /**
     * Getter for the internal total height.
     *
     * @return {@link #virtualizer} total height.
     */
    @Override
    protected double internalTotalHeight() {
        return virtualizer.getTotalHeight();
    }

    /**
     * Shifts the internal virtualizer by the given amounts.
     *
     * @param widthDelta  The width offset of the dragging.
     * @param heightDelta The height offset of the dragging.
     */
    @Override
    protected void handleDraggedBy(double widthDelta, double heightDelta) {
        if(widthDelta == 0 && heightDelta == 0) {
            return;
        }

        if(widthDelta == 0) {
            virtualizer.shiftHeightBy(heightDelta);
        }

        if(heightDelta == 0) {
            virtualizer.shiftWidthBy(widthDelta);
        }

        virtualizer.shiftBy(heightDelta, widthDelta);
    }

    /**
     * Zooms in or out on the internal virtualizer.
     *
     * @param oldZoom       The old zoom value.
     * @param widthOffset   The x coordinate of the zoom event.
     * @param heightOffset  The y coordinate of the zoom event.
     */
    @Override
    protected void handleZoomedAt(double oldZoom, double widthOffset, double heightOffset) {
        double off = zoomProperty().get() / oldZoom - 1;
        virtualizer.shiftBy(heightOffset * off, widthOffset * off);
    }
}
