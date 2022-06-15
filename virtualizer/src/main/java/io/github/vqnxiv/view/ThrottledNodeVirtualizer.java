package io.github.vqnxiv.view;


import javafx.application.Platform;
import javafx.geometry.Point2D;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Extension of {@link NodeVirtualizer} which uses
 * {@link Platform#runLater(Runnable)} to refresh 
 * its view.
 * Updates are 'throttled' to avoid swarming the JFX
 * thread, i.e it will only refresh again once the 
 * previous refresh has been completed.
 * <p>
 * By default, any shift calls or property value set
 * will be ignored and dropped if the view is currently
 * being updated. The view will however refresh itself
 * once more if one the properties value was changed
 * during an update.
 * <p>
 * It is however possible to enable blocking, which
 * keeps a list of the positions it should shift to
 * and queues them one by one in order to make the 
 * movement 'smoother'.
 *
 * @see NodeVirtualizer
 */
public class ThrottledNodeVirtualizer extends NodeVirtualizer {

    /**
     * Whether it's allowed to send a refresh through Platform.runLater().
     */
    protected final AtomicBoolean canUpdate = new AtomicBoolean(true);

    /**
     * Whether to refresh once more after the current update is done.
     */
    protected final AtomicBoolean onceMore = new AtomicBoolean(false);

    /**
     * Whether to block and queue 'inbetween' positions.
     */
    private final boolean blocking;

    /**
     * Possible next positions.
     */
    private final Deque<Point2D> nextPositions = new ArrayDeque<>();
    
    
    /**
     * Constructor with default look ahead and disabled blocking.
     * Drawing priority is determined by the iteration
     * order of the given collection.
     *
     * @param pairs The structures and pools.
     */
    public ThrottledNodeVirtualizer(Collection<StructureToPool<?>> pairs) {
        super(pairs);
        blocking = false;
    }

    /**
     * Constructor.
     * Drawing priority is determined by the iteration
     * order of the given collection.
     *
     * @param pairs The structures and pools.
     * @param blocking  Whether to enable blocking.
     */
    public ThrottledNodeVirtualizer(Collection<StructureToPool<?>> pairs, boolean blocking) {
        super(pairs);
        this.blocking = blocking;
    }

    /**
     * Constructor.
     * Drawing priority is determined by the iteration
     * order of the given collection.
     *
     * @param pairs The structures and pools.
     * @param blocking  Whether to enable blocking.
     * @param lookAhead Look ahead value.
     */
    public ThrottledNodeVirtualizer(Collection<StructureToPool<?>> pairs, boolean blocking, double lookAhead) {
        super(pairs, lookAhead);
        this.blocking = blocking;
    }


    /**
     * {@inheritDoc}.
     * <br>
     * Queues the current position if blocking is enabled.
     */
    @Override
    protected void onOffsetChanged() {
        if(internal == null) {
            return;
        }

        if(blocking) {
            nextPositions.add(new Point2D(getWidthOffset(), getHeightOffset()));
        }
        
        super.onOffsetChanged();
    }
    

    /**
     * {@inheritDoc}
     * <p>
     * Refreshing is sent through {@link Platform#runLater(Runnable)}
     * <u>iff</u> this method was <u>not</u> called on the FX thread. 
     * If it was called on the FX thread, the view is directly
     * updated.
     */
    @Override
    public void refreshView() {
        if(internal == null) {
            return;
        }
        
        if(canUpdate.getAndSet(false)) {
            if(Platform.isFxApplicationThread()) {
                doUpdate();
            }
            else {
                Platform.runLater(this::doUpdate);
            }
        }
        else {
            onceMore.set(true);
        }
    }
    
    
    /**
     * Refreshes the view to either the current values of the
     * offset properties or the next position in the queue
     * depending on whether blocking is enabled.
     * This does <u>not</u> check whether it was called on the FX thread.
     */
    private void doUpdate() {
        var p = getNext();
        refreshTo(p.getX(), p.getY());

        canUpdate.set(true);
        if(!nextPositions.isEmpty()) {
            onceMore.set(true);
        }

        if(onceMore.getAndSet(false)) {
            refreshView();
        }
    }

    /**
     * Returns the next position to refresh to.
     * 
     * @return The next position to refresh to.
     */
    private Point2D getNext() {
        if(!blocking || nextPositions.isEmpty()) {
            return new Point2D(getWidthOffset(), getHeightOffset());
        }
        else {
            return nextPositions.pollFirst();
        }
    }

}
