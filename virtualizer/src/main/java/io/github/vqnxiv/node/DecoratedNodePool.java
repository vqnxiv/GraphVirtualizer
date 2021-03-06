package io.github.vqnxiv.node;


import java.util.Collection;
import java.util.Optional;


/**
 * Interface which describes a pool of 
 * decorated nodes.
 * 
 * @param <D> Type of decorators.
 *
 * @see DecoratedNode
 * @see SetNodePool
 * @see TimedNodePool
 */
public interface DecoratedNodePool<D> {

    /**
     * Get one DecoratedNode node which is decorated 
     * with the given decorator, if it is not already
     * associated with a decoratednode in this pool.
     * 
     * @param d Decorator.
     * @return An instance of DecoratedNode.
     */
    Optional<DecoratedNode<D>> get(D d);

    /**
     * Get multiple DecoratedNode nodes which are decorated 
     * with the given decorators.
     *
     * @param ds Decorators.
     * @return Multiple instances of DecoratedNode.
     */
    default Collection<DecoratedNode<D>> getAll(Collection<D> ds) {
        return ds.stream().map(this::get).flatMap(Optional::stream).toList();
    }

    /**
     * Returns an instance of DecoratedNode to the pool.
     * 
     * @param decoratedNode The decoratedNode to return.
     */
    void release(DecoratedNode<D> decoratedNode);

    /**
     * Returns multiple instances of DecoratedNode to the pool.
     * 
     * @param decoratedNodes The decoratedNodes to return.
     */
    default void releaseAll(Collection<DecoratedNode<D>> decoratedNodes) {
        decoratedNodes.forEach(this::release);
    }

    /**
     * Clears the free nodes.
     */
    void clear();
    
    /**
     * Returns the amount of nodes that are currently in use.
     * 
     * @return The amount of used nodes.
     */
    int used();

    /**
     * Returns the amount of available nodes in the pool.
     * 
     * @return The amount of available nodes in the pool.
     */
    int capacity();
}
