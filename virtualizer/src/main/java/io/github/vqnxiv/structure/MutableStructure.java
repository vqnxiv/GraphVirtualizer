package io.github.vqnxiv.structure;


import javafx.geometry.Point2D;


/**
 * Mutable extension of {@link LayoutableStructure}.
 * 
 * @param <E> Type of elements.
 *           
 * @see LayoutableStructure
 * @see CoordinatesStructure
 */
public interface MutableStructure<E> extends LayoutableStructure<E> {
    
    boolean add(E e);
    
    boolean addAt(E e, Point2D p);
    
    boolean remove(E e);
    
    // ...
    
}
