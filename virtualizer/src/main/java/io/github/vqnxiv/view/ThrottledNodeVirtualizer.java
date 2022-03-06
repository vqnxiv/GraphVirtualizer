package io.github.vqnxiv.view;


import io.github.vqnxiv.node.DecoratedNodePool;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesStructure;
import javafx.application.Platform;
import javafx.geometry.Point2D;

import java.util.ArrayDeque;
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
 * @param <E> The decorator type of the nodes.
 *
 * @see NodeVirtualizer
 */
public class ThrottledNodeVirtualizer<E> extends NodeVirtualizer<E> {

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
    // not thread safe if refreshView is called from non JFX thread?
    private final Deque<Point2D> nextPositions = new ArrayDeque<>();
    
    
    /**
     * Constructor with default look ahead and disabled blocking.
     *
     * @param structure Elements.
     * @param pool      Node pool.
     */
    public ThrottledNodeVirtualizer(CoordinatesStructure<E> structure, DecoratedNodePool<CoordinatesElement<E>> pool) {
        super(structure, pool);
        blocking = false;
    }

    /**
     * Constructor.
     *
     * @param structure Elements.
     * @param pool      Node pool.
     * @param blocking  Whether to enable blocking.
     */
    public ThrottledNodeVirtualizer(CoordinatesStructure<E> structure, DecoratedNodePool<CoordinatesElement<E>> pool,
                                    boolean blocking) {
        super(structure, pool);
        this.blocking = blocking;
    }

    /**
     * Constructor.
     *
     * @param structure Elements.
     * @param pool      Node pool.
     * @param blocking  Whether to enable blocking.
     * @param lookAhead Look ahead value.
     */
    public ThrottledNodeVirtualizer(CoordinatesStructure<E> structure, DecoratedNodePool<CoordinatesElement<E>> pool,
                                    boolean blocking, double lookAhead) {
        super(structure, pool, lookAhead);
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
     */
    @Override
    public void refreshView() {
        if(internal == null) {
            return;
        }
        
        if(canUpdate.getAndSet(false)) {
            doUpdate();
        }
        else {
            onceMore.set(true);
        }
    }
    
    
    /**
     * Refreshes the view to either the current values of the
     * offset properties or the next position in the queue
     * depending on whether blocking is enabled.
     * <br>
     * Refreshing is sent through {@link Platform#runLater(Runnable)}
     * <u>iff</u> this method was <u>not</u> called on the FX thread. 
     * If it was called on the FX thread, the view is directly
     * updated.
     */
    private void doUpdate() {
        // move fxthread check in refresh
        if(Platform.isFxApplicationThread()) {
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
        else {
            Platform.runLater(
                () -> {
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
            );
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
