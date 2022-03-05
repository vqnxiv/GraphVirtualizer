package io.github.vqnxiv.view;


import io.github.vqnxiv.misc.BoundedDoubleProperty;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;


/**
 * Contains boilerplate code for a scrollable, pannable
 * and zoomable region.
 * 
 * @see VirtualizerRegion 
 */
public abstract class AbstractAbleRegion extends Region {

    /**
     * The default minimum zoom level.
     */
    public static final double DEFAULT_MIN_ZOOM = 0.1d;

    /**
     * The default maximum zoom level.
     */
    public static final double DEFAULT_MAX_ZOOM = 2.0d;

    /**
     * The default zoom unit ('one mouse wheel rotation').
     */
    public static final double DEFAULT_ZOOM_STEP = 0.1d;


    /**
     * Whether this region can be scrolled.
     */
    private final BooleanProperty scrollable = new SimpleBooleanProperty();

   
    /**
     * Whether this region can be panned.
     */
    private final BooleanProperty pannable = new SimpleBooleanProperty();
    
    /**
     * Whether this region can be zoomed.
     */
    private final BooleanProperty zoomable = new SimpleBooleanProperty();

    /**
     * Whether to show the horizontal scrollbar.
     */
    private final ObjectProperty<ScrollPane.ScrollBarPolicy> hbarPolicy =
        new SimpleObjectProperty<>(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    /**
     * Whether to show the vertical scrollbar.
     */
    private final ObjectProperty<ScrollPane.ScrollBarPolicy> vbarPolicy =
        new SimpleObjectProperty<>(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    /**
     * The current zoom level.
     */
    private final BoundedDoubleProperty zoom =
        new BoundedDoubleProperty(DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM);


    /**
     * Horizontal scrollbar.
     */
    protected final ScrollBar hBar = new ScrollBar();

    /**
     * Vertical scrollbar.
     */
    protected final ScrollBar vBar = new ScrollBar();


    /**
     * Keeps the position of the last mouse click so we get a starting
     * point when calculating the dragging shift.
     */
    private Point2D mouseClick = new Point2D(0, 0);

    /**
     * Saves click position to {@link #mouseClick}.
     */
    private final EventHandler<MouseEvent> mousePressed = e -> mouseClick = new Point2D(e.getX(), e.getY());

    /**
     * Calls {@link #handleDraggedBy(double, double)} on mouse drag.
     */
    private final EventHandler<MouseEvent> mouseDragged = e -> {
        double dx = e.getX() - mouseClick.getX();
        double dy = e.getY() - mouseClick.getY();
        mouseClick = new Point2D(e.getX(), e.getY());
        handleDraggedBy(dx, dy);
    };

    /**
     * Calls {@link #handleZoomedAt(double, double, double)} on mousewheel scroll.
     */
    private final EventHandler<ScrollEvent> scrolled = e -> {
        // getDeltaY -> scroll 'quantity' so we get the number of scroll wheel rotations
        // when we divide by getMultiplier (e.g 40 / 40 = 1, -80 / 40 = -2).
        double d = zoom.get();
        zoom.set(d + (e.getDeltaY() / e.getMultiplierY() * DEFAULT_ZOOM_STEP));
        handleZoomedAt(d, e.getX(), e.getY());
    };


    /**
     * Constructor.
     */
    protected AbstractAbleRegion() {
        this(1, true, true, true);
    }

    /**
     * Constructor.
     *
     * @param zoom Base zoom level.
     */
    protected AbstractAbleRegion(double zoom) {
        this(zoom, true, true, true);
    }

    /**
     * Constructor.
     *
     * @param zoom       Base zoom level.
     * @param scrollable Whether to activate scrollbars.
     * @param pannable   Whether to enable dragging.
     * @param zoomable   Whether to activate zooming.
     */
    protected AbstractAbleRegion(double zoom, boolean scrollable, boolean pannable, boolean zoomable) {
        super();

        this.pannable.addListener(this::updatePannable);
        this.zoomable.addListener(this::updateZoomable);

        this.zoom.set(zoom);
        this.scrollable.set(scrollable);
        this.pannable.set(pannable);
        this.zoomable.set(zoomable);

        hBar.setOrientation(Orientation.HORIZONTAL);
        vBar.setOrientation(Orientation.VERTICAL);
        
        getChildren().addAll(vBar, hBar);
    }


    /**
     * Updates {@link #pannable} listeners.
     * 
     * @param onlyHereForMethodRef Ignored.
     */
    private void updatePannable(Observable onlyHereForMethodRef) {
        if(pannable.get()) {
            AbstractAbleRegion.this.setOnMousePressed(mousePressed);
            AbstractAbleRegion.this.setOnMouseDragged(mouseDragged);
        }
        else {
            AbstractAbleRegion.this.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
            AbstractAbleRegion.this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
        }
    }

    /**
     * Updates {@link #zoomable} listeners.
     *
     * @param onlyHereForMethodRef Ignored.
     */
    private void updateZoomable(Observable onlyHereForMethodRef) {
        if(zoomable.get()) {
            AbstractAbleRegion.this.setOnScroll(scrolled);
        }
        else {
            AbstractAbleRegion.this.removeEventHandler(ScrollEvent.SCROLL, scrolled);
        }
    }
    

    /**
     * Method to be implemented which should do whatever has to be done on mouse drag.
     *
     * @param widthDelta The width offset of the dragging.
     * @param heightDelta The height offset of the dragging.
     */
    protected abstract void handleDraggedBy(double widthDelta, double heightDelta);

    /**
     * Method to be implemented which should do whatever has to be done on zooming.
     *
     * @param oldZoom Old zoom value.
     * @param widthOffset The x coordinate of the zoom event.
     * @param heightOffset The y coordinate of the zoom event.
     */
    protected abstract void handleZoomedAt(double oldZoom, double widthOffset, double heightOffset);

    /**
     * 'Private' method which should return the internal content's total width.
     *
     * @return Total width.
     */
    protected abstract double internalTotalWidth();

    /**
     * 'Private' method which should return the internal content's total height.
     *
     * @return Total height.
     */
    protected abstract double internalTotalHeight();

    /**
     * Determines whether the horizontal scrollbar should show.
     *
     * @return {@code true} if it should show; {@code false} otherwise.
     */
    private boolean shouldHBarShow() {
        return switch(vbarPolicy.get()) {
            case NEVER -> false;
            case ALWAYS -> true;
            case AS_NEEDED ->  internalTotalWidth() > getWidth();
        };
    }

    /**
     * Determines whether the vertical scrollbar should show.
     *
     * @return {@code true} if it should show; {@code false} otherwise.
     */
    private boolean shouldVBarShow() {
        return switch(hbarPolicy.get()) {
            case NEVER -> false;
            case ALWAYS -> true;
            case AS_NEEDED -> internalTotalHeight() > getHeight();
        };
    }

    /**
     * Layout method.
     */
    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double totalHeight = getHeight();
        double totalWidth = getWidth();

        if(!scrollable.get()) {
            vBar.setVisible(false);
            hBar.setVisible(false);
            return;
        }
        
        boolean hbarVis = shouldHBarShow();
        boolean vbarVis = shouldVBarShow();

        double hbarH = (hbarVis) ? hBar.getHeight() : 0;
        double vbarW = (vbarVis) ? vBar.getWidth() : 0;

        hBar.setVisible(hbarVis);
        if(hbarVis) {
            hBar.resizeRelocate(
                0,
                snapPositionY(totalHeight - vbarW),
                snapSizeX(totalWidth - vbarW),
                hbarH
            );
        }

        vBar.setVisible(vbarVis);
        if(vbarVis) {
            vBar.resizeRelocate(
                snapPositionX(totalWidth - vbarW),
                0,
                vbarW,
                snapSizeY(totalHeight - hbarH)
            );
        }
    }


    /**
     * Setter for {@link #hbarPolicy}.
     * 
     * @param hbarPolicy New HBar policy value.
     */
    public void setHbarPolicy(ScrollPane.ScrollBarPolicy hbarPolicy) {
        this.hbarPolicy.set(hbarPolicy);
    }

    /**
     * Setter for {@link #vbarPolicy}.
     *
     * @param vbarPolicy New HBar policy value.
     */
    public void setVbarPolicy(ScrollPane.ScrollBarPolicy vbarPolicy) {
        this.vbarPolicy.set(vbarPolicy);
    }

    /**
     * Setter for {@link #scrollable}.
     *
     * @param scrollable New value for {@link #scrollable}.
     */
    public void setScrollable(boolean scrollable) {
        this.scrollable.set(scrollable);
    }

    /**
     * Getter for {@link #scrollable} value.
     *
     * @return {@link #scrollable} value.
     */
    public boolean isScrollable() {
        return scrollable.get();
    }

    /**
     * Getter for {@link #scrollable}.
     *
     * @return {@link #scrollable}.
     */
    public BooleanProperty scrollableProperty() {
        return scrollable;
    }


    /**
     * Setter for {@link #pannable}.
     *
     * @param pannable New value for {@link #pannable}.
     */
    public void setPannable(boolean pannable) {
        this.pannable.set(pannable);
    }

    /**
     * Getter for {@link #scrollable} value.
     *
     * @return {@link #scrollable} value.
     */
    public boolean isPannable() {
        return pannable.get();
    }

    /**
     * Getter for {@link #pannable}.
     *
     * @return {@link #pannable}.
     */
    public BooleanProperty pannableProperty() {
        return pannable;
    }


    /**
     * Setter for {@link #zoomable}.
     *
     * @param zoomable New value for {@link #zoomable}.
     */
    public void setZoomable(boolean zoomable) {
        this.zoomable.set(zoomable);
    }

    /**
     * Getter for {@link #zoomable} value.
     *
     * @return {@link #zoomable} value.
     */
    public boolean isZoomable() {
        return zoomable.get();
    }

    /**
     * Getter for {@link #zoomable}.
     *
     * @return {@link #zoomable}.
     */
    public BooleanProperty zoomableProperty() {
        return zoomable;
    }


    /**
     * Setter for {@link #zoom}.
     *
     * @param zoom New value for {@link #zoom}.
     */
    public void setZoom(double zoom) {
        this.zoom.set(zoom);
    }

    /**
     * Getter for {@link #zoom} value.
     *
     * @return {@link #zoom} value.
     */
    public double getZoom() {
        return zoom.get();
    }

    /**
     * Getter for {@link #zoom}.
     *
     * @return {@link #zoom}.
     */
    public BoundedDoubleProperty zoomProperty() {
        return zoom;
    }

    /**
     * Setter for {@link #zoom} max value.
     *
     * @param max New value for {@link #zoom} max.
     */
    public void setMaxZoom(double max) {
        zoom.setMax(max);
    }

    /**
     * Getter for {@link #zoom} max value.
     *
     * @return {@link #zoom} max value.
     */
    public double getMaxZoom() {
        return zoom.getMax();
    }

    /**
     * Setter for {@link #zoom} min value.
     *
     * @param min New value for {@link #zoom} min.
     */
    public void setMinZoom(double min) {
        zoom.setMin(min);
    }

    /**
     * Getter for {@link #zoom} min value.
     *
     * @return {@link #zoom} min value.
     */
    public double getMinZoom() {
        return zoom.getMin();
    }
}
