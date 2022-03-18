package io.github.vqnxiv.view;


import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;


/**
 * Virtualizer interface. 
 * <p>
 * Virtualizer aren't {@link Node} or any other class 
 * from a jfx scene graph, they handle a view ({@link #getView()})
 * and are responsible for the correct placement of the
 * virtualizer's elements within their view.
 * 
 * @see AbstractVirtualizer
 * @see VirtualizerRegion
 */
public interface Virtualizer {

    /**
     * Shifts the virtualizer on the Y-axis by the given amount.
     *
     * @param height Height offset.
     */
    void shiftHeightBy(double height);

    /**
     * Shifts the virtualizer on the X-axis by the given amount.
     *
     * @param width Width offset.
     */
    void shiftWidthBy(double width);

    /**
     * Shifts the virtualizer on both axis by the given amounts.
     *
     * @param height Height offset.
     * @param width  Width offset.
     */
    void shiftBy(double height, double width);

    /**
     * Shifts the virtualizer by the given point2d's 
     * {@link Point2D#getX()} and {@link Point2D#getY()}.
     *
     * @param p Point2D to retrieve the offsets from.
     */
    default void shiftBy(Point2D p) {
        shiftBy(p.getX(), p.getY());
    }


    /**
     * Forces the virtualizer to refresh its view.
     * Although it should normally not be needed, the method
     * is still present for exceptional cases where the external 
     * node may have been changed without notifying the virtualizer
     * in a way that requires the virtualizer to update.
     * It should not be called on every change (unless specified
     * otherwise), as the virtualizer should already do most
     * of the legwork in regards to updating itself.
     */
    void refreshView();

    /**
     * Getter for the virtualizer's view.
     * Returns a {@link Node} as some virtualizers
     * may internally use a region, a pane, or even a canvas.
     * <p>
     * The returned node's size may be different (likely larger)
     * than the values of the view width and height properties.
     *
     * @return The virtualizer's view.
     */
    Node getView();

    
    /**
     * Getter for the view height.
     * 
     * @return The view height.
     */
    default double getViewHeight() {
        return viewHeight().get();
    }

    /**
     * Getter for the view height property.
     * 
     * @return The view height property.
     */
    DoubleProperty viewHeight();

    /**
     * Getter for the view width.
     *
     * @return The view width.
     */
    default double getViewWidth() {
        return viewWidth().get();
    }

    /**
     * Getter for the view width property.
     *
     * @return The view width property.
     */
    DoubleProperty viewWidth();
    
    /**
     * Getter for the height offset.
     *
     * @return The height offset.
     */
    default double getHeightOffset() {
        return heightOffset().get();
    }

    /**
     * Getter for the height offset property.
     *
     * @return The height offset property.
     */
    DoubleProperty heightOffset();

    /**
     * Getter for the width offset.
     *
     * @return The width offser.
     */
    default double getWidthOffset() {
        return widthOffset().get();
    }

    /**
     * Getter for the width offset property.
     *
     * @return The width offset property.
     */
    DoubleProperty widthOffset();

    /**
     * Getter for the total height.
     *
     * @return The total height.
     */
    default double getTotalHeight() {
        return totalHeight().get();
    }

    /**
     * Getter for the total height property.
     *
     * @return The total height property.
     */
    DoubleProperty totalHeight();

    /**
     * Getter for the total width.
     *
     * @return The total width.
     */
    default double getTotalWidth() {
        return totalWidth().get();
    }

    /**
     * Getter for the total width property.
     *
     * @return The total width property.
     */
    DoubleProperty totalWidth();
}

