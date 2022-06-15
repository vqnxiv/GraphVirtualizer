package io.github.vqnxiv.view;


import io.github.vqnxiv.node.DecoratedNode;
import io.github.vqnxiv.node.DecoratedNodePool;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesStructure;
import io.github.vqnxiv.structure.LocalizedStructure;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Very basic implementation of a node based
 * virtualizer. This implementation is <u>not</u>
 * thread safe and does <u>not</u> use 
 * {@link javafx.application.Platform#runLater(Runnable)}.
 * <br>
 * {@link #refreshView()} and {@link #refreshTo(double, double)} 
 * should only be called on the JFX thread.
 *           
 * @see ThrottledNodeVirtualizer
 */
public class NodeVirtualizer extends AbstractVirtualizer {

    /**
     * Represents a link between a {@link CoordinatesStructure}
     * and a {@link DecoratedNodePool}.
     * 
     * @param structure The structure.
     * @param pool      The pool.
     * @param <T>       Type of element.
     */
    public record StructureToPool<T>(
        CoordinatesStructure<T> structure,
        DecoratedNodePool<CoordinatesElement<T>> pool
    ) {}
    
    /**
     * Represents a link between a structure, a pool
     * and the nodes retrieved from the pool.
     * 
     * @param structure The structure.
     * @param pool      The pool.
     * @param nodes     The nodes.
     * @param <T>       Type of element.
     */
    private record Triple<T>(
        CoordinatesStructure<T> structure,
        DecoratedNodePool<CoordinatesElement<T>> pool,
        Map<Node, DecoratedNode<CoordinatesElement<T>>> nodes
    ) {
        /**
         * Constructor from {@link StructureToPool} 
         * and new empty map.
         * 
         * @param stp {@link StructureToPool}.
         */
        private Triple(StructureToPool<T> stp) {
            this(stp.structure(), stp.pool(), new HashMap<>());
        }
    }
    

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
     * This virtualizer's elements & nodes.
     */
    private final List<Triple<?>> triples;

    /**
     * Look ahead value for this virtualizer.
     */
    protected final double lookAhead;

    /**
     * The current position of the top left corner.
     */
    private Point2D topLeft = new Point2D(0d, 0d);

    /**
     * The current position of the bottom right corner.
     */
    private Point2D bottomRight = new Point2D(0d, 0d);
    
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
     * Constructor with default look ahead.
     * Drawing priority is determined by the iteration
     * order of the given collection.
     * 
     * @param pairs The structures and pools.
     */
    // priority = collection order
    public NodeVirtualizer(Collection<StructureToPool<?>> pairs) {
        this(pairs, DEFAULT_LOOK_AHEAD);
    }

    /**
     * Constructor.
     * Drawing priority is determined by the iteration
     * order of the given collection.
     * 
     * @param pairs The structures and pools.
     * @param lookAhead Look ahead value.
     */
    public NodeVirtualizer(Collection<StructureToPool<?>> pairs, double lookAhead) {
        super();
   
        this.internal = new AnchorPane();
        this.lookAhead = lookAhead;

        triples = new ArrayList<>();
        pairs.forEach(p -> triples.add(new Triple<>(p)));
        
        var mWidth = Bindings.createDoubleBinding(
            () -> {
                /*
                double max = 0;
                for(var t : triples) {
                    max = Math.max(max, t.structure().getMaximumWidth());
                }
                return max;
                */
                return triples.stream()
                    .map(Triple::structure)
                    .mapToDouble(CoordinatesStructure::getMaximumWidth)
                    .max()
                    .orElse(0d);
            },
            triples.stream()
                .map(Triple::structure)
                .map(CoordinatesStructure::maximumWidth)
                .toArray(Observable[]::new)
        );

        var mHeight = Bindings.createDoubleBinding(
            () -> {
                /*
                double max = 0;
                for(var t : triples) {
                    max = Math.max(max, t.structure().getMaximumHeight());
                }
                return max;
                */
                return triples.stream()
                    .map(Triple::structure)
                    .mapToDouble(CoordinatesStructure::getMaximumHeight)
                    .max()
                    .orElse(0d);
            },
            triples.stream()
                .map(Triple::structure)
                .map(CoordinatesStructure::maximumHeight)
                .toArray(Observable[]::new)
        );
        
        totalWidth().bind(mWidth);
        totalHeight().bind(mHeight);
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
        
        // problem here is that it refreshes twice i.e on height change and on width change
        // not a problem in throttled version though
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
        topLeft = new Point2D( width - lookAhead, height - lookAhead);
        bottomRight = new Point2D(width + getViewWidth(), height + getViewHeight());
        
        // no change
        if(topLeft.equals(previousTopLeft) && bottomRight.equals(previousBottomRight)) {
            return;
        }
       
        if(isReduction()) {
            filterAndRelocateNodes(width, height);
            previousTopLeft = topLeft;
            previousBottomRight = bottomRight;
            return;
        }
        
        boolean noOverlap = noOverlap();
        
        if(noOverlap) {
            internal.getChildren().clear();
            triples.forEach(this::clearNodes);
        }
        else {
            filterAndRelocateNodes(width, height);
        }

        triples.forEach(
            t -> fetchAndUpdateNewNodes(t, noOverlap, width, height)
        );

        previousTopLeft = topLeft;
        previousBottomRight = bottomRight;
    }

    /**
     * Checks whether the current view size is a
     * reduction compared to the previous size.
     * 
     * @return {@code true} if the current view size
     * is equal or less than the previous size.
     */
    private boolean isReduction() {
        return topLeft.getX() >= previousTopLeft.getX()
            && topLeft.getY() >= previousTopLeft.getY()
            && bottomRight.getX() <= previousBottomRight.getX()
            && bottomRight.getY() <= previousBottomRight.getY();
    }

    /**
     * Checks whether the current view area overlaps
     * with the previous area.
     * 
     * @return {@code true} if the current view area
     * does <u>not</u> overlap with the previous one.
     */
    private boolean noOverlap() {
        return topLeft.getX() >= previousBottomRight.getX()
            || topLeft.getY() >= previousBottomRight.getY()
            || bottomRight.getX() <= previousTopLeft.getX()
            || bottomRight.getY() <= previousTopLeft.getY();
    }

    /**
     * Filters out nodes that are no longer in the view area
     * & returns them to their respective pools; relocates
     * the rest to their new view coordinates.
     *
     * @param width  Width offset.
     * @param height Height offset.
     */
    private void filterAndRelocateNodes(double width, double height) {
        var itr = internal.getChildren().iterator();
        
        while(itr.hasNext()) {
            var n = itr.next();
            
            var opt = getTriple(n);
            if(opt.isEmpty()) {
                itr.remove();
            }
            else {
                var t = opt.get();
                var dn = t.nodes().get(n);
                if(dn.getDecorator().isPresent() && dn.getDecorator().get().isIn(topLeft, bottomRight)) {
                    dn.getNode().relocate(dn.getDecorator().get().getX() - width, dn.getDecorator().get().getY() - height);
                }
                else {
                    itr.remove();
                    removeNodeIn(n, t);
                }
            }
        }
    }

    /**
     * Fetches and places new elements from structures
     * and new nodes from their respective pools.
     * 
     * @param triple    The triple to to update.
     * @param noOverlap Whether the current view overlaps with the previous.
     * @param width     Width offset.
     * @param height    Height offset.
     * @param <T>       Type of triple.
     */
    private <T> void fetchAndUpdateNewNodes(Triple<T> triple, boolean noOverlap, double width, double height) {
        var possible = (triple.structure() instanceof LocalizedStructure<?> && !noOverlap) ?
            localizedGetFromPool(triple) :
            triple.pool().getAll(
                triple.structure()
                    .between(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY())
            );
        
        for(var n : possible) {
            if(triple.nodes().putIfAbsent(n.getNode(), n) != null) {
                triple.pool().release(n);
                continue;
            }
            internal.getChildren().add(n.getNode());
            n.getDecorator().ifPresent(
                d -> n.getNode().relocate(d.getX() - width, d.getY() - height)
            );
            if(n.getDecorator().isEmpty()) {
                // ?
            }
        }
    }

    /**
     * Helper method which does localized lookups, i.e parts of the current area
     * that weren't in the previous area.
     * 
     * @param triple The triple to fetch from.
     * @return       The nodes retrieved from the triple's pool.
     * @param <T>    Type of triple.
     */
    private <T> Collection<DecoratedNode<CoordinatesElement<T>>> localizedGetFromPool(Triple<T> triple) {
        
        Collection<DecoratedNode<CoordinatesElement<T>>> possible = new ArrayList<>();
        double xOffset = topLeft.getX() - previousTopLeft.getX();
        double yOffset = topLeft.getY() - previousTopLeft.getY();

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

            var e = triple.structure().between(nTopLeftX, nTopLeftY, nBottomRightX, nBottomRightY);
            possible.addAll(triple.pool().getAll(e));
        }
        // == 0 -> no changes; < 0 -> reduction
        else if((xOffset = bottomRight.getX() - previousBottomRight.getX()) > 0) {
            nTopLeftY = previousTopLeft.getY() + yOffset;
            nTopLeftX = previousBottomRight.getX() + yOffset;
            nBottomRightX = bottomRight.getX();
            nBottomRightY = bottomRight.getY();

            var e = triple.structure().between(nTopLeftX, nTopLeftY, nBottomRightX, nBottomRightY);
            possible.addAll(triple.pool().getAll(e));
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

            var e = triple.structure().between(nTopLeftX, nTopLeftY, nBottomRightX, nBottomRightY);
            possible.addAll(triple.pool().getAll(e));
        }
        else if(bottomRight.getY() - previousBottomRight.getY() > 0) {
            nTopLeftX = previousTopLeft.getX() + xOffset;
            nTopLeftY = previousBottomRight.getY() + xOffset;
            nBottomRightX = bottomRight.getX();
            nBottomRightY = bottomRight.getY();

            var e = triple.structure().between(nTopLeftX, nTopLeftY, nBottomRightX, nBottomRightY);
            possible.addAll(triple.pool().getAll(e));
        }

        return possible;
    }

    /**
     * Gets the triple the given node belongs to.
     * 
     * @param node The node to check.
     * @return {@link Optional} of the triple this node belongs to.
     */
    private Optional<Triple<?>> getTriple(Node node) {
        for(var t : triples) {
            if(t.nodes().get(node) != null) {
                return Optional.of(t);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Helper method which removes a node from a triple, 
     * if it belongs to it.
     * 
     * @param n   The node.
     * @param t   The triple.
     * @param <T> Type of triple.
     */
    private <T> void removeNodeIn(Node n, Triple<T> t) {
        DecoratedNode<CoordinatesElement<T>> dn;
        if((dn = t.nodes().remove(n)) != null) {
            t.pool().release(dn);
        }
    }

    /**
     * Helper method which clears the nodes from a triple.
     * 
     * @param t   The triple to clear.
     * @param <T> Type of triple.
     */
    private <T> void clearNodes(Triple<T> t) {
        t.pool().releaseAll(t.nodes().values());
        t.nodes().clear();
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
