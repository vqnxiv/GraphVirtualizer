package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Layoutable extension of {@link CoordinatesMatrix}.
 * 
 * @param <E> Type of elements.
 */
public class LayoutableMatrix<E> extends CoordinatesMatrix<E> implements LayoutableStructure<E> {


    /**
     * Constructor.
     *
     * @param el Elements.
     */
    public LayoutableMatrix(Collection<E> el) {
        super(el);
    }

    /**
     * Constructor.
     *
     * @param el            Elements.
     * @param initialWidth  Width.
     * @param initialHeight Height.
     */
    public LayoutableMatrix(Collection<E> el, double initialWidth, double initialHeight) {
        super(el, initialWidth, initialHeight);
    }

    /**
     * Constructor.
     *
     * @param el               Elements.
     * @param initialWidth     Width.
     * @param initialHeight    Height.
     * @param initialRowNumber Row number.
     * @param initialColNumber Column number.
     */
    public LayoutableMatrix(Collection<E> el, double initialWidth, double initialHeight, 
                            int initialRowNumber, int initialColNumber) {
        super(
            el, initialWidth, initialHeight, 
            initialRowNumber, initialColNumber
        );
    }

    /**
     * Constructor.
     *
     * @param el                  Elements.
     * @param initialWidth        Width.
     * @param initialHeight       Height.
     * @param initialRowNumber    Row number.
     * @param initialColNumber    Column number.
     * @param maxRowRangeIncrease Maximum row range increase.
     * @param maxColRangeIncrease Maximum column range increase.
     */
    public LayoutableMatrix(Collection<E> el, double initialWidth, double initialHeight, 
                            int initialRowNumber, int initialColNumber, 
                            float maxRowRangeIncrease, float maxColRangeIncrease) {
        super(
            el, initialWidth, initialHeight, 
            initialRowNumber, initialColNumber, 
            maxRowRangeIncrease, maxColRangeIncrease
        );
    }

    /**
     * Constructor.
     *
     * @param el                  Elements.
     * @param initialWidth        Width.
     * @param initialHeight       Height.
     * @param initialRowNumber    Row number.
     * @param initialColNumber    Column number.
     * @param maxRowRangeIncrease Maximum row range increase.
     * @param maxColRangeIncrease Maximum column range increase.
     * @param maxRowNumber        Maximum rows.
     * @param maxColNumber        Maximum columns.
     */
    public LayoutableMatrix(Collection<E> el, double initialWidth, double initialHeight, 
                            int initialRowNumber, int initialColNumber, 
                            float maxRowRangeIncrease, float maxColRangeIncrease, 
                            int maxRowNumber, int maxColNumber) {
        super(
            el, initialWidth, initialHeight, 
            initialRowNumber, initialColNumber, 
            maxRowRangeIncrease, maxColRangeIncrease, 
            maxRowNumber, maxColNumber
        );
    }

    /**
     * Reposition one element to the given coordinates.
     *
     * @param e The elements with its old coordinates.
     * @param x New X coordinate.
     * @param y New Y coordinate.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, double x, double y) {
        move(e, x, y);
    }

    /**
     * Reposition one element to the given coordinates.
     *
     * @param e The elements with its old coordinates.
     * @param p The new coordinates for the element.
     */
    @Override
    public void repositionTo(CoordinatesElement<E> e, Point2D p) {
        repositionTo(e, p.getX(), p.getY());
    }

    /**
     * Reposition multiple elements to the given coordinates.
     *
     * @param m Elements with their old coordinates mapped to
     *          their new coordinates.
     */
    @Override
    public void repositionAllTo(Map<CoordinatesElement<E>, Point2D> m) {
        List<CoordinatesElement<E>> changed = new ArrayList<>(m.size());

        for(var e : m.entrySet()) {

        }
    }
}
