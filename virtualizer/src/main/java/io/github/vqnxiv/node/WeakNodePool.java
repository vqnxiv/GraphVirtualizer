package io.github.vqnxiv.node;


import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


/**
 * Node pool which only holds weak references to its cached nodes.
 *
 * @param <D> Type of decorator.
 *
 * @see DecoratedNode
 */
// todo: not finished
public class WeakNodePool<D> implements DecoratedNodePool<D> {
   
    /**
     * Nodes which aren't in use and can be decorated.
     */
    private final Deque<WeakReference<DecoratedNode<D>>> freeNodes;

    /**
     * DecoratedNode nodes which have been retrieved from a 
     * {@link #get(Object)} or {@link #getAll(Collection)}
     * call.
     */
    private final Map<D, DecoratedNode<D>> usedNodes;

    /**
     * Factory which creates instances of DecoratedNode.
     */
    private final Function<D, DecoratedNode<D>> factory;


    /**
     * Constructor.
     * 
     * @param factory Factory.
     */
    public WeakNodePool(Function<D, DecoratedNode<D>> factory) {
        Objects.requireNonNull(factory);

        this.factory = factory;
        freeNodes = new ArrayDeque<>();
        usedNodes = new HashMap<>();
    }

    
    /**
     * Helper method.
     *
     * @param d Decorator.
     * @return Decorated node.
     */
    private DecoratedNode<D> createNode(D d) {
        DecoratedNode<D> ret;

        if(freeNodes.isEmpty()) {
            ret = factory.apply(d);
        }
        else {
            ret = freeNodes.poll().get();
            if(ret == null) {
                ret = factory.apply(d);
            }
            ret.setDecorator(d);
        }

        return ret;
    }
    
    
    /**
     * {@inheritDoc}
     *
     * @param d Decorator.
     * @return An instance of DecoratedNode.
     */
    @Override
    public Optional<DecoratedNode<D>> get(D d) {
        if(usedNodes.containsKey(d)) {
            return Optional.empty();
        }

        var ret = createNode(d);
        usedNodes.put(d, ret);

        return Optional.of(ret);
    }

    /**
     * {@inheritDoc}
     *
     * @param ds Decorators.
     * @return Multiple instances of DecoratedNode.
     */
    @Override
    public Collection<DecoratedNode<D>> getAll(Collection<D> ds) {
        List<DecoratedNode<D>> l = new ArrayList<>();

        for(D d : ds) {
            if(usedNodes.containsKey(d)) {
                continue;
            }

            var r = createNode(d);
            usedNodes.put(d, r);
            l.add(r);
        }

        return l;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param decoratedNode The decoratedNode to return.
     */
    @Override
    public void release(DecoratedNode<D> decoratedNode) {
        if(decoratedNode.getDecorator().isEmpty()) {
            usedNodes.entrySet().removeIf(e -> e.getValue() == decoratedNode);
        }
        else if(usedNodes.remove(decoratedNode.getDecorator().get(), decoratedNode)) {
            decoratedNode.clearDecoration();
            freeNodes.add(new WeakReference<>(decoratedNode));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        freeNodes.clear();
    }

    /**
     * {@inheritDoc}
     *
     * @return Tthe amount of available nodes in the pool.
     */
    @Override
    public int used() {
        return usedNodes.size();
    }

    /**
     * {@inheritDoc}
     *
     * @return The amount of available nodes in the pool.
     */
    @Override
    public int capacity() {
        return freeNodes.size();
    }
}
