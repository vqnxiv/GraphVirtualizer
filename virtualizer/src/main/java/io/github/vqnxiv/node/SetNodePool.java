package io.github.vqnxiv.node;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;


/**
 * The most basic kind of pool. It keeps all generated nodes
 * until {@link #clear()} is called.
 * 
 * @param <D> Type of decorator.
 *
 * @see DecoratedNode
 * @see TimedNodePool
 */
public class SetNodePool<D> implements DecoratedNodePool<D> {

    /**
     * Nodes which aren't in use and can be decorated.
     */
    private final Queue<DecoratedNode<D>> freeNodes;

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
    public SetNodePool(Function<D, DecoratedNode<D>> factory) {
        Objects.requireNonNull(factory);
        
        this.factory = factory;
        freeNodes = new ArrayDeque<>();
        usedNodes = new HashMap<>(); 
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
        
        DecoratedNode<D> ret;
        
        if(freeNodes.isEmpty()) {
            ret = factory.apply(d);
        }
        else {
            ret = freeNodes.poll();
            ret.setDecorator(d);
        }
        
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
            get(d).ifPresent(l::add);
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
            return;
        }

        if(usedNodes.remove(decoratedNode.getDecorator().get(), decoratedNode)) {
            decoratedNode.clearDecoration();
            freeNodes.add(decoratedNode);
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
