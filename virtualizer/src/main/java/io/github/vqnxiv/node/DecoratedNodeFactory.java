package io.github.vqnxiv.node;


/**
 * Functional factory interface for decorated instances.
 * Ideally this should create the node which contains the
 * decorator, e.g
 * <pre>
 *     public DecoratedNode createNode(D d) {
 *         return new MyDecorated(d, new Label(d.toString());
 *     }
 * </pre>
 * rather than relying on a node pool, as this interface is
 * intended for node pools to create new nodes when they need to.
 * 
 * @see DecoratedNode
 * @see DecoratedNodePool
 * 
 * @param <D> The type of decorator.
 */
@FunctionalInterface
public interface DecoratedNodeFactory<D> {

    /**
     * Creates a new DecoratedNode instance.
     * 
     * @param d The decorator.
     * @return A new decorated.
     */
    DecoratedNode<D> createNode(D d);
}
