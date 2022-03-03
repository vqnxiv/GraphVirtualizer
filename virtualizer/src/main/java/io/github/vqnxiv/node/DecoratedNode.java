package io.github.vqnxiv.node;


import javafx.scene.Node;

import java.util.Optional;


/**
 * Interface which wraps a node and a pojo (or anything).
 * The node is said to be 'decorated' with its associated pojo
 * (the 'decorator').
 * E.g the node is a Label, and the decoration is the 
 * pojo.toString() on the label.
 * Or like a pojo with a field Color, then the Node is a
 * Shape filled with the color field, etc.
 * 
 * @param <D> The decorator type.
 *  
 * @see DecoratedNodeFactory
 * @see DecoratedNodePool
 */
public interface DecoratedNode<D> {

    /**
     * Getter for the node.
     * 
     * @return The node.
     */
    Node getNode();

    /**
     * Getter for the decorator.
     * 
     * @return The decorator.
     */
    Optional<D> getDecorator();

    /**
     * Setter for the decorator.
     * <p>
     * This method should be responsible for updating
     * the underlying node, e.g setting a new text for a label,
     * so that we don't have to recreate as many nodes.
     * 
     * @param d New decorator.
     */
    void setDecorator(D d);

    /**
     * Whether the node is currently decorated.
     * 
     * @return {@code true} if this node is decorated.
     */
    boolean isDecorated();

    /**
     * Clears the decorator.
     */
    void clearDecoration();
}
