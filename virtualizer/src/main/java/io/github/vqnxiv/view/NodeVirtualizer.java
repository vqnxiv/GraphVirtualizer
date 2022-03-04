package io.github.vqnxiv.view;


import io.github.vqnxiv.node.DecoratedNode;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesStructure;
import io.github.vqnxiv.node.DecoratedNodePool;
import io.github.vqnxiv.structure.LocalizedStructure;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.*;


/**
 * Very basic implementation of a node based
 * virtualizer. This implementation is <u>not</u>
 * thread safe and does <u>not</u> use 
 * {@link javafx.application.Platform#runLater(Runnable)}.
 * <br>
 * {@link #refreshView()} and {@link #refreshTo(double, double)} 
 * should only be called on the JFX thread.
 * 
 * @param <E> The decorator type of the nodes.
 *           
 * @see ThrottledNodeVirtualizer
 */
public class NodeVirtualizer<E> extends AbstractVirtualizer {
    
    /**
     * Default look ahead value. 
     * I.e the size of the area around the view which should
     * be included when displaying nodes. Although these nodes
     * may be clipped or completely outside of the view depending
     * on their dimensions, this avoids sudden 'pop-in' and 'pop-out'
     * of the nodes which are displayed in the view when they reach
     * the borders.
     */
    public static final double DEFAULT_LOOK_AHEAD = 200d;


    /**
     * Internal pane on which the nodes are displayed.
     */
    protected final AnchorPane internal;
    
    /**
     * The elements to display.
     */
    protected final CoordinatesStructure<E> elements;

    /**
     * Node pool.
     */
    protected final DecoratedNodePool<CoordinatesElement<E>> pool;

    /**
     * The nodes which are currently displayed.
     */
    protected final Set<DecoratedNode<CoordinatesElement<E>>> nodes;

    /**
     * Look ahead value for this virtualizer.
     */
    protected final double lookAhead;

    /**
     * The previous position of the top left corner, 
     * i.e the offset used in {@link #refreshTo(double, double)} 
     * minus the {@link #lookAhead} value.
     */
    private Point2D previousTopLeft = new Point2D(0d, 0d);

    /**
     * The previous position of the bottom right corner,
     * i.e the offset used in {@link #refreshTo(double, double)}
     * plus the width and height values.
     */
    private Point2D previousBottomRight = new Point2D(-1d, -1d);

    /**
     * {@code true} to disable potentially time consuming checks.
     * Exceptions and inconsistencies may happen /shrug
     */
    private boolean unsure = true;
    
    
    /**
     * Constructor with default look ahead.
     * 
     * @param structure Elements.
     * @param pool      Node pool.
     */
    public NodeVirtualizer(CoordinatesStructure<E> structure, DecoratedNodePool<CoordinatesElement<E>> pool) {
        this(structure, pool, DEFAULT_LOOK_AHEAD);
    }

    /**
     * Constructor.
     * 
     * @param structure Elements.
     * @param pool      Node pool.
     * @param lookAhead Look ahead value.
     */
    public NodeVirtualizer(CoordinatesStructure<E> structure, DecoratedNodePool<CoordinatesElement<E>> pool, 
                           double lookAhead) {
        super();

        this.elements = structure;
        this.pool = pool;
        this.nodes = new LinkedHashSet<>();
        this.internal = new AnchorPane();
        this.lookAhead = lookAhead;

        totalWidth().bind(elements.maximumWidth());
        totalHeight().bind(elements.maximumHeight());
    }
    
    /**
     * Refreshes the view.
     */
    @Override
    protected void onOffsetChanged() {
        refreshView();
    }

    /**
     * Does nothing.
     */
    @Override
    protected void onTotalChanged() {
        // nothing. either i's out of bounds and we don't need to refresh 
        // or it changed offset/view and it refreshes through these
    }

    /**
     * Resizes the internal pane and refreshes the view.
     */
    @Override
    protected void onViewChanged() {
        if(internal == null) {
            return;
        }
        
        // manual resizing because anchorPane widthProperty and heightProperty are readOnly
        // resize slightly more than view + lookAhead as some nodes to display may be larger 
        // (particularly on the right and bottom sides) 
        internal.setPrefSize((getViewWidth() + lookAhead) * 1.1d, (getViewHeight() + lookAhead) * 1.1d);
        
        refreshView();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshView() {
        // super sets property values before this object is completely initialized,
        // so this method gets called through invalidated() before the anchorpane is created
        if(internal == null) {
            return;
        }
        
        refreshTo(widthOffset.get(), heightOffset.get());
    }


    /**
     * Refreshes and 'moves' the view to the given coordinates.
     * <br>
     * Does <u>not</u> check the internal pane for nullity.
     *
     * @param width  Width offset.
     * @param height Height offset.
     */
    protected void refreshTo(double width, double height) {

        double topLeftX = width - lookAhead;
        double topLeftY = height - lookAhead;

        double bottomRightX = width + getViewWidth();
        double bottomRightY = height + getViewHeight();

        // if any of these checks true, that means there is no overlap between
        // the current and the previous area (e.g scrollbar shift)
        boolean noOverlap = topLeftX >= previousBottomRight.getX() 
            || topLeftY >= previousBottomRight.getY()
            || bottomRightX <= previousTopLeft.getX() 
            || bottomRightY <= previousTopLeft.getY();
        
        if(noOverlap) {
            internal.getChildren().clear();
            pool.releaseAll(nodes);
            nodes.clear();
        }
        else {
            filterOutBetween(topLeftX, topLeftY, bottomRightX, bottomRightY, width, height);
        }

        var possible = 
            (elements instanceof LocalizedStructure<E> && !noOverlap) ?
                localizedGetFromPool(topLeftX, topLeftY) : 
                pool.getAll(elements.between(topLeftX, topLeftY, bottomRightX, bottomRightY));
        
        for(var n : possible) {
            if(nodes.add(n)) {
                if(unsure) {
                    // adds without checking, which should normally work
                    // because nodes are requested through the pool
                    // (only returns nodes that aren't used, 
                    // i.e nodes that aren't in this control or another)
                    internal.getChildren().add(n.getNode());
                    n.getDecorator().ifPresent(
                        d -> n.getNode().relocate(d.getX() - width, d.getY() - height)
                    );
                }
                else {
                    if(!internal.getChildren().contains(n.getNode())) {
                        internal.getChildren().add(n.getNode());
                        n.getDecorator().ifPresent(
                            d -> n.getNode().relocate(d.getX() - width, d.getY() - height)
                        );
                    }
                }
            }
            else {
                // shouldn't happen but better safe than sorry
                pool.release(n);
            }
        }
        
        previousTopLeft = new Point2D(topLeftX, topLeftY);
        previousBottomRight = new Point2D(bottomRightX, bottomRightY);
    }


    /**
     * Helper method which iterates over the nodes and relocate them 
     * within {@link #internal} if they're still in the area given by 
     * {@code [topLeftX, topLeftY], [bottomRightX, bottomRightY]}.
     * If a node is out of the area, it is removed from {@link #nodes}
     * and {@link #internal} children nodes, then returned to{@link #pool}.
     * 
     * @param topLeftX      Top left X coordinate.
     * @param topLeftY      Top left Y coordinate.
     * @param bottomRightX  Bottom right X coordinate.
     * @param bottomRightY  Bottom right Y coordinate.
     * @param width         Width of the drawn area, used to relocate nodes.
     * @param height        Height of the drawn area, used to relocate nodes.
     */
    private void filterOutBetween(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY,
                                  double width, double height) {
        List<DecoratedNode<CoordinatesElement<E>>> toRemove = new ArrayList<>();

        // current index, nodes is a set
        final int[] nodeIndex = { 0 };
        for(var n : nodes) {
            n.getDecorator().ifPresentOrElse(
                d -> {
                    // relocate if in area
                    if(d.isIn(topLeftX, topLeftY, bottomRightX, bottomRightY)) {
                        n.getNode().relocate(d.getX() - width, d.getY() - height);
                    }
                    // remove otherwise
                    else {
                        toRemove.add(n);
                        if(unsure) {
                            // this should work everytime as the set of nodes
                            // is a linkedhashset and the observable list
                            // is backed by an arraylist
                            internal.getChildren().remove(nodeIndex[0]);
                            nodeIndex[0]--;
                        }
                    }
                },
                // remove if empty, should never happen
                () -> {
                    toRemove.add(n);
                    internal.getChildren().remove(n.getNode());
                }
            );
            nodeIndex[0]++;
        }
        
        if(!unsure) {
            internal.getChildren().removeAll(
                toRemove.stream().map(DecoratedNode::getNode).toList()
            );
        }
        
        toRemove.forEach(nodes::remove);
        pool.releaseAll(toRemove);
    }

    /**
     * Helper method which does localized lookups, i.e parts of the current area
     * that weren't in the previous area.
     * 
     * @param topLeftX Top left X coordinate.
     * @param topLeftY Top left Y coordinate.
     * @return {@link Collection} of the nodes retrieved from {{@link #pool}}.
     */
    // todo: fix inwards shifts (zoom out)
    private Collection<DecoratedNode<CoordinatesElement<E>>> localizedGetFromPool(double topLeftX, double topLeftY) {
        
        Collection<DecoratedNode<CoordinatesElement<E>>> possible = new ArrayList<>();
        double xOffset = topLeftX - previousTopLeft.getX();
        double yOffset = topLeftY - previousTopLeft.getY();
        
        double nTopLeftX, nTopLeftY, nBottomRightX, nBottomRightY;
        
        if(xOffset != 0) {
            nTopLeftY = previousTopLeft.getY() + yOffset;
            nBottomRightY = previousBottomRight.getY() + yOffset;

            if(xOffset > 0) {
                nTopLeftX = previousBottomRight.getX();
                nBottomRightX = previousBottomRight.getX() + xOffset;
            } 
            else {
                nTopLeftX = previousTopLeft.getX() + xOffset;
                nBottomRightX = previousTopLeft.getX();
            }
            
            var e = elements.between(nTopLeftX, nTopLeftY, nBottomRightX, nBottomRightY);
            possible.addAll(pool.getAll(e));
        }
        
        if(yOffset != 0) {
            nTopLeftX = previousTopLeft.getX() + xOffset;
            nBottomRightX = previousBottomRight.getX() + xOffset;

            if(yOffset > 0) {
                nTopLeftY = previousBottomRight.getY();
                nBottomRightY = previousBottomRight.getY() + yOffset;
            } 
            else {
                nTopLeftY = previousTopLeft.getY() + yOffset;
                nBottomRightY = previousTopLeft.getY();
            }

            var e = elements.between(nTopLeftX, nTopLeftY, nBottomRightX, nBottomRightY);
            possible.addAll(pool.getAll(e));
        }
        
        return possible;
    }
    
    
    /**
     * {@inheritDoc}
     *
     * @return The virtualizer's view.
     */
    @Override
    public Node getView() {
        return internal;
    }
}
