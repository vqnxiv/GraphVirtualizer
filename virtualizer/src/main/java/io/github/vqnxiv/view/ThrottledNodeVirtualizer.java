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
/*
    Right now blocking is not exactly smooth, but excessive smoothing makes 
  panning / scrolling become extremely slow and feel unresponsive
  (e.g if scrollbar shift => scroll of 2.5k px in shifts of 200...)
  and dropping too many shifts makes it look like a slideshow.
  
    Smooth up to 1k-2k nodes, 2k-4k makes it feel slightly unresponsive,
  and above 10k is a bit less slideshow than throttledvirtualizer.
  My guess is that with correct use of line reduction, smoothing etc
  it could feel smooth until 5-10k, then we reach JFX limits
  (updating the view takes too long for any sort of shift to seem smooth).
  (though that may not be the case with better structures and/or async fecthing?)
 
    A possibility would be to track both shifts positions and their timestamps,
  so we can detect 'jumps' (scrollbar shifts) and avoid smoothing them <br>
    + could also adapt the smoothing depending on how fast the updates are done
  (e.g increase/decrease shifts depending on how slow/fast it updates) <br>
    + could also drop shifts if there's too many queued (e.g like in dry(),
  only take the start and the end of the line). <br>
    + we can reduce a lot with douglasPeucker and make curves/sudden corner shifts
  a lot smoother with smooth(). Like combine dry() + dgp() on sublists of the 
  total queued shifts, then smooth everything; or smooth depending on time 
  between updates, etc.
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
                    // we get it here as there might be a delay between the moment 
                    // runLater() is called and the moment it's actually executing 
                    // the runnable
                    
                    // not sure this delay is worth accounting for though
                    // it seems to be sub 10ms with a few nodes (sub 5ms
                    // when fully zoomed in), and then 40-70ms with a few
                    // thousands and <= 100ms at ~15k nodes?
                    // only goes above that (~200-400ms) when spamming 
                    // scrollbars or resizing the pane to fullscreen from 
                    // 100*100
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
