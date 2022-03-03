package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.geometry.Point2D;

import java.util.Collection;
import java.util.Map;


/**
 * Layoutable extension of {@link CoordinatesList}.
 * 
 * @param <E> Type of elements.
 *           
 * @see CoordinatesList
 * @see LayoutableStructure
 */
public class LayoutableList<E> extends CoordinatesList<E> implements LayoutableStructure<E> {
    
    
    /**
     * Constructor.
     *
     * @param el Elements.
     */
    public LayoutableList(Collection<E> el) {
        super(el);
    }

    
    /**
     * {@inheritDoc}
     *
     * @param e The elements with its old coordinates.
     * @param p The new coordinates for the element.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, Point2D p) {
        try {
            // in case its a deep copy or its not in the list
            var e2 = elements.get(elements.indexOf(e));
            e2.setX(p.getX());
            e2.setY(p.getY());
        }
        catch(IndexOutOfBoundsException ex) {
            // ignore for now
        }

    }

    /**
     * {@inheritDoc}
     *
     * @param m Elements with their old coordinates mapped to
     *          their new coordinates.
     */
    @Override
    public void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m) {
        for(var e : m.entrySet()) {
            try {
                var e2 = elements.get(elements.indexOf(e.getKey()));
                e2.setX(e.getValue().getX());
                e2.setY(e.getValue().getY());
            }
            catch(IndexOutOfBoundsException ex) {
                // ignore for now
            }
        }
    }
}
