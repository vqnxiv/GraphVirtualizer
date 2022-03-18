package io.github.vqnxiv.structure;


import javafx.geometry.Point2D;

import java.util.Objects;


/**
 * Class which contains an object and a pair of coordinates.
 *
 * @param <E> The type of object.
 */
public class CoordinatesElement<E> {

    /**
     * The element.
     */
    private final E element;

    /**
     * The x coordinate of the top left corner ('width offset').
     */
    private double x;

    /**
     * The y coordinate of the top left corner ('height offset').
     */
    private double y;


    /**
     * Constructor.
     *
     * @param e Element.
     */
    public CoordinatesElement(E e) {
        this.element = e;
        this.x = 0d;
        this.y = 0d;
    }

    /**
     * Constructor.
     *
     * @param e Element.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public CoordinatesElement(E e, double x, double y) {
        this.element = e;
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor.
     * 
     * @param e Element.
     * @param p Coordinates.
     */
    public CoordinatesElement(E e, Point2D p) {
        this(e, p.getX(), p.getY());
    }

    /**
     * Copy constructor.
     * 
     * @param c The coordinates element to copy.
     */
    public CoordinatesElement(CoordinatesElement<E> c) {
        this(c.getElement(), c.getX(), c.y);
    }


    /**
     * Whether this element is within the given area.
     * 
     * @param topLeftX      Top left corner X coordinate.
     * @param topLeftY      Top left corner Y coordinate.
     * @param bottomRightX  Bottom right corner X coordinate.
     * @param bottomRightY  Bottom right corner Y coordinate.
     * @return {@code true} if this element is within the area.
     */
    public boolean isIn(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        return x >= topLeftX && x <= bottomRightX
            && y >= topLeftY && y <= bottomRightY;
    }

    /**
     * Whether this element is within the given area.
     * 
     * @param topLeft       Top left corner.
     * @param bottomRight   Bottom right corner.
     * @return {@code true} if this element is within the area.
     */
    public boolean isIn(Point2D topLeft, Point2D bottomRight) {
        return isIn(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
    }

    /**
     * Getter for the element.
     *
     * @return The element.
     */
    public E getElement() {
        return element;
    }

    /**
     * Getter for the x coordinate.
     *
     * @return The x coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Setter for the x coordinate.
     *
     * @param x The new x coordinate.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Getter for the y coordinate.
     *
     * @return The y coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Setter for the y coordinate.
     *
     * @param y The new y coordinate.
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Setter for both coordinates.
     *
     * @param x New x coordinate.
     * @param y New y coordinate.
     */
    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Getter for both coordinates.
     * 
     * @return Point2D of both coordinates.
     */
    public Point2D getXY() {
        return new Point2D(x, y);
    }

    /**
     * Setter for both coordinates.
     * 
     * @param p New coordinates.
     */
    public void setXY(Point2D p) {
        x = p.getX();
        y = p.getY();
    }


    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        
        if(o instanceof CoordinatesElement<?> c) {
            return x == c.x && y == c.y && element.equals(c.element);
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ") " + element.toString();
    }
}
