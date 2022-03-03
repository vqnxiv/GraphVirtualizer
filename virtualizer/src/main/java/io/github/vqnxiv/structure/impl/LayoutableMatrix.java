package io.github.vqnxiv.structure.impl;

import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.geometry.Point2D;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class LayoutableMatrix<E> extends CoordinatesMatrix<E> implements LayoutableStructure<E> {
    public LayoutableMatrix(Collection<E> el) {
        super(el);
    }

    public LayoutableMatrix(Collection<E> el, int initialRowNumber, int initialColNumber) {
        super(el, initialRowNumber, initialColNumber);
    }

    public LayoutableMatrix(Collection<E> el, int initialRowNumber, int initialColNumber, float maxRowRangeIncrease, float maxColRangeIncrease) {
        super(el, initialRowNumber, initialColNumber, maxRowRangeIncrease, maxColRangeIncrease);
    }

    public LayoutableMatrix(Collection<E> el, int initialRowNumber, int initialColNumber, float maxRowRangeIncrease, float maxColRangeIncrease, int maxRowNumber, int maxColNumber) {
        super(el, initialRowNumber, initialColNumber, maxRowRangeIncrease, maxColRangeIncrease, maxRowNumber, maxColNumber);
    }

    /**
     * Reposition one element to the given coordinates.
     *
     * @param e The elements with its old coordinates.
     * @param p The new coordinates for the element.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, Point2D p) {
        var p2 = getCoordsInArray(e);
        
        if(!elements[(int) p2.getX()][(int) p2.getY()].remove(e)) {
            return;
        }
        
        ensureSize(p.getX(), p.getY());
        var p3 = getCoordsInArray(p);
        e.setX(p.getX());
        e.setY(p.getY());
        elements[(int) p3.getX()][(int) p3.getY()].add(e);
    }

    /**
     * Reposition multiple elements to the given coordinates.
     *
     * @param m Elements with their old coordinates mapped to
     *          their new coordinates.
     */
    @Override
    public void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m) {

    }
}
