package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.Layout;
import io.github.vqnxiv.structure.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Coordinates structure backed by a 2d array of lists.
 * <p>
 * The position in the 2d array is simply calculated as such: <br>
 * {@code i = element.x / maxWidth * rowNumber} 
 * <br> and <br>
 * {@code j = element.y / maxHeight * colNumber} 
 * <br> gives <br>
 * {@code array[i][j].add(element)}.
 * <p>
 * The array is initially 5*5 (or whatever {@link #DEFAULT_ROW_NUMBER}
 * and {@link #DEFAULT_COL_NUMBER} are), unless specified otherwise
 * in the constructor.
 * <p>
 * When an element is 'out of bounds' (e.g width greater than maxWidth
 * or height greater than maxHeight), this structure is 'resized' in one
 * of the two following ways:
 * <ul>
 *     <li>The width/height range of a row/column is increased.<br>
 *     E.g if the current maxWidth is 100 for a number of 5 rows,
 *     then a row has a range of 20. If an element with an
 *     {@code x} value of 110 is added, then the range of a row
 *     is increased to at least {@code 110/5 = 22}, then the elements
 *     are re-inserted into their correct positions.</li>
 *     <li>The array size is increased by however many columns / rows
 *     is needed to store the new element.<br>
 *     If we keep the same example, then the [5][*] array will
 *     become [6][*] with a row range of 20, thus making the total width 120.</li>
 * </ul>
 * The factor which decides which of one of these is performed is the
 * maximum row/col range increase ({@link #DEFAULT_MAX_WIDTH_INCREASE} and
 * {@link #DEFAULT_MAX_HEIGHT_INCREASE}). If the new row/column range from
 * the first way is less than (or equal to) the current range multiplied
 * by the maximum increase, then the range will be increased.
 * If it is not the case, the array will be resized. <br>
 * To follow up on the earlier example, assuming a maximum increase of 1.5: <br>
 * {@code element.x / rowNumber <= widthRange * widthMaxIncrease} <br>
 * would become here {@code 110 / 5 <= 20 * 1.5}, which is {@code 22 <= 30},
 * so it would have resized on the range. <br>
 * There is however a second factor: the maximum number of rows/columns.
 * ({@link #DEFAULT_MAX_ROW_NUMBER}, {@link #DEFAULT_MAX_COL_NUMBER})
 * Once it has been reached, the structure will always resize on the range. <br>
 * To avoid that, one can use the constructor which accepts a value for
 * the maximum number of rows and columns with {@link Integer#MAX_VALUE}.
 * Conversely, one can set the maximum range increase to a value of {@code 1.0}
 * (the minimum accepted) so that this structure always resizes the internal
 * array without increasing the range.
 * 
 * @param <E> Type of elements.
 *           
 * @see CoordinatesStructure
 * @see CoordinatesElement
 * @see CoordinatesList
 */
public class CoordinatesMatrix<E> implements CoordinatesStructure<E>, LocalizedStructure<E> {

    /**
     * Default initial row number.
     */
    public static final int DEFAULT_ROW_NUMBER = 5;

    /**
     * Default initial column number.
     */
    public static final int DEFAULT_COL_NUMBER = 5;

    /**
     * Default maximum row number.
     */
    public static final int DEFAULT_MAX_ROW_NUMBER = 50;

    /**
     * Default maximum column number.
     */
    public static final int DEFAULT_MAX_COL_NUMBER = 50;

    /**
     * Default maximum width range increase.
     */
    public static final float DEFAULT_MAX_WIDTH_INCREASE = 1.5f;

    /**
     * Default maximum height range increase.
     */
    public static final float DEFAULT_MAX_HEIGHT_INCREASE = 1.5f;

    /**
     * Default maximum width.
     */
    public static final double DEFAULT_MAX_WIDTH = 1_000d;

    /**
     * Default maximum height.
     */
    public static final double DEFAULT_MAX_HEIGHT = 1_000d;


    /**
     * The elements.
     */
    private List<CoordinatesElement<E>>[][] elements;
    
    /**
     * Current row range.
     */
    private int rowRange;

    /**
     * Current column range.
     */
    private int colRange;
    
    /**
     * Maximum row range increase.
     */
    private final float maxRowRangeIncrease;

    /**
     * Maximum column range increase.
     */
    private final float maxColRangeIncrease;

    /**
     * Maximum number of rows.
     */
    private final int maxRowNumber;

    /**
     * Maximum number of columns.
     */
    private final int maxColNumber;

    /**
     * Minimum width in this structure.
     */
    private final DoubleProperty minWidth = new SimpleDoubleProperty();

    /**
     * Minimum height in this structure.
     */
    private final DoubleProperty minHeight = new SimpleDoubleProperty();

    /**
     * Maximum width in this structure.
     */
    private final DoubleProperty maxWidth = new SimpleDoubleProperty();

    /**
     * Maximum height in this structure.
     */
    private final DoubleProperty maxHeight = new SimpleDoubleProperty();

    /**
     * True maximum width before resizing.
     */
    private double trueMaxWdith;

    /**
     * True maximum height before resizing.
     */
    private double trueMaxHeight;
    
    /**
     * Concurrent modification checker for {@link MatrixIterator}. 
     */
    private int modCount;

    /**
     * Number of elements.
     */
    private int size;
    

    /**
     * Constructor.
     *
     * @param el Elements.
     */
    public CoordinatesMatrix(Collection<E> el) {
        this(
            el,
            DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT,
            DEFAULT_ROW_NUMBER, DEFAULT_COL_NUMBER,
            DEFAULT_MAX_WIDTH_INCREASE, DEFAULT_MAX_HEIGHT_INCREASE,
            DEFAULT_MAX_ROW_NUMBER, DEFAULT_MAX_COL_NUMBER
        );
    }

    /**
     * 
     * @param el Elements.
     * @param layoutSupplier Initial layout.
     */
    public CoordinatesMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier) {
        this(new LayoutableMatrix<>(el, layoutSupplier));
    }
    
    /**
     * Elements copy constructor.
     * 
     * @param c Structure to copy.
     */
    public CoordinatesMatrix(CoordinatesStructure<E> c) {
        this(
            c,
            c.maximumWidth().get(), c.maximumHeight().get(),
            DEFAULT_ROW_NUMBER, DEFAULT_COL_NUMBER,
            DEFAULT_MAX_WIDTH_INCREASE, DEFAULT_MAX_HEIGHT_INCREASE,
            DEFAULT_MAX_ROW_NUMBER, DEFAULT_MAX_COL_NUMBER
        );
    }

    /**
     * Copy constructor.
     * 
     * @param m Matrix to copy.
     */
    public CoordinatesMatrix(CoordinatesMatrix<E> m) {
        this(
            m,
            m.maxWidth.get(), m.maxHeight.get(),
            m.elements.length, m.elements[0].length,
            m.maxRowRangeIncrease, m.maxColRangeIncrease,
            m.maxRowNumber, m.maxColNumber
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
    @SuppressWarnings("unchecked")
    public CoordinatesMatrix(Collection<E> el,
                             double initialWidth, double initialHeight,
                             int initialRowNumber, int initialColNumber,
                             float maxRowRangeIncrease, float maxColRangeIncrease,
                             int maxRowNumber, int maxColNumber) {

        if(initialWidth < 0 || initialHeight < 0
            || initialRowNumber < 1 || initialColNumber < 1
            || maxRowRangeIncrease < 1.0 || maxColRangeIncrease < 1.0
            || maxRowNumber < initialRowNumber || maxColNumber < initialColNumber) {
            throw new IllegalArgumentException();
        }

        this.maxRowRangeIncrease = maxRowRangeIncrease;
        this.maxColRangeIncrease = maxColRangeIncrease;

        this.maxRowNumber = maxRowNumber;
        this.maxColNumber = maxColNumber;

        elements = (List<CoordinatesElement<E>>[][]) Array.newInstance(List.class, initialRowNumber, initialColNumber);

        for(var t : elements) {
            for(int i = 0; i < t.length; i++) {
                t[i] = new ArrayList<>();
            }
        }

        maxWidth.set(initialWidth);
        maxHeight.set(initialHeight);

        rowRange = (int) (maxWidth.get() / initialRowNumber);
        colRange = (int) (maxHeight.get() / initialColNumber);

        for(E e : el) {
            elements[0][0].add(new CoordinatesElement<>(e, 0, 0));
        }
        
        size = el.size();
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
    public CoordinatesMatrix(CoordinatesStructure<E> el,
                             double initialWidth, double initialHeight,
                             int initialRowNumber, int initialColNumber,
                             float maxRowRangeIncrease, float maxColRangeIncrease,
                             int maxRowNumber, int maxColNumber) {
        this(
            List.of(),
            initialWidth, initialHeight,
            initialRowNumber, initialColNumber,
            maxRowRangeIncrease, maxColRangeIncrease,
            maxRowNumber, maxColNumber
        );
        
        for(var e : el) {
            place(e);
        }

        size = el.size();
    }
    
    /**
     * Constructor.
     *
     * @param el                  Elements.
     * @param layoutSupplier      Initial layout.
     * @param initialWidth        Width.
     * @param initialHeight       Height.
     * @param initialRowNumber    Row number.
     * @param initialColNumber    Column number.
     * @param maxRowRangeIncrease Maximum row range increase.
     * @param maxColRangeIncrease Maximum column range increase.
     * @param maxRowNumber        Maximum rows.
     * @param maxColNumber        Maximum columns.
     */
    public CoordinatesMatrix(Collection<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier, 
                             double initialWidth, double initialHeight,
                             int initialRowNumber, int initialColNumber,
                             float maxRowRangeIncrease, float maxColRangeIncrease,
                             int maxRowNumber, int maxColNumber) {
        this(
            new LayoutableMatrix<>(
                el, layoutSupplier,
                initialWidth, initialHeight,
                initialRowNumber, initialColNumber,
                maxRowRangeIncrease, maxColRangeIncrease,
                maxRowNumber, maxColNumber
            )
        );
    }

    /**
     * Constructor.
     *
     * @param el                  Elements.
     * @param layoutSupplier      Initial layout.
     * @param initialWidth        Width.
     * @param initialHeight       Height.
     * @param initialRowNumber    Row number.
     * @param initialColNumber    Column number.
     * @param maxRowRangeIncrease Maximum row range increase.
     * @param maxColRangeIncrease Maximum column range increase.
     * @param maxRowNumber        Maximum rows.
     * @param maxColNumber        Maximum columns.
     */
    public CoordinatesMatrix(CoordinatesStructure<E> el, Function<LayoutableStructure<E>, Layout<E>> layoutSupplier,
                             double initialWidth, double initialHeight,
                             int initialRowNumber, int initialColNumber,
                             float maxRowRangeIncrease, float maxColRangeIncrease,
                             int maxRowNumber, int maxColNumber) {
        this(
            new LayoutableMatrix<>(
                el, layoutSupplier,
                initialWidth, initialHeight,
                initialRowNumber, initialColNumber,
                maxRowRangeIncrease, maxColRangeIncrease,
                maxRowNumber, maxColNumber
            )
        );
    }

    
    // todo: dimensions update
    // auto on move etc or manual?
    // id say manual so its faster but idk
    
    /**
     * Should be called whenever {@link #elements} is modified.
     */
    private void modified() {
        modCount++;
    }
    
    /**
     * Helper method which returns where a pair of coordinates
     * would be localed in {@link #elements}.
     * 
     * @param p Coordinates.
     * @return Point2D which contains the coordinates.
     */
    protected Point2D indexesOf(CoordinatesElement<E> p) {
        int i = (int) (p.getX() / maxWidth.get() * elements.length);
        int j = (int) (p.getY() / maxHeight.get() * elements[0].length);
        return new Point2D(i, j);
    }

    /**
     * Helper method which returns where a pair of coordinates
     * would be localed in {@link #elements}.
     *
     * @param p Coordinates.
     * @return Point2D which contains the coordinates.
     */
    protected Point2D indexesOf(Point2D p) {
        int i = (int) (p.getX() / maxWidth.get() * elements.length);
        int j = (int) (p.getY() / maxHeight.get() * elements[0].length);
        return new Point2D(i, j);
    }

    /**
     * Helper method which returns where a pair of coordinates
     * would be localed in {@link #elements}.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Point2D which contains the coordinates.
     */
    protected Point2D indexesOf(double x, double y) {
        int i = (int) (x / maxWidth.get() * elements.length);
        int j = (int) (y / maxHeight.get() * elements[0].length);
        return new Point2D(i, j);
    }

    /**
     * Helper method which adds the given element 
     * to {@link #elements}.
     *
     * @param c The element to place.
     * @return {@code true} if it was added; {@code false} otherwise.
     */
    protected boolean place(CoordinatesElement<E> c) {
        ensureSize(c.getX(), c.getY());
        if(!getListAt(indexesOf(c)).add(c)) {
            return false;
        }
        modified();
        return true;
    }

    /**
     * Helper method which removes the given element 
     * from {@link #elements}.
     *
     * @param c The element to remove.
     * @return {@code true} if it was removed; {@code false} otherwise.
     */
    protected boolean remove(CoordinatesElement<E> c) {
        if(!getListAt(indexesOf(c)).remove(c)) {
            return false;
        }
        modified();
        return true;
    }

    /**
     * Helper method which an existing moves an element. 
     * Resizes the array if needed.
     * 
     * @param c The element to move.
     * @param x New X coordinate.
     * @param y New Y coordinate.
     * @return {@code true} if the element was present and successfully moved;
     * {@code false} otherwise.
     */
    protected boolean move(CoordinatesElement<E> c, double x, double y) {
        if(!getListAt(indexesOf(c)).remove(c)) {
            return false;
        }
        
        ensureSize(x, y);
        getListAt(indexesOf(x, y)).add(c);
        c.setX(x);
        c.setY(y);
        modified();
        return true;
    }

    /**
     * Helper method which an existing moves an element. 
     * Resizes the array if needed.
     * 
     * @param c The element to move.
     * @param p The new coordinates.
     * @return {@code true} if the element was present and successfully moved;
     * {@code false} otherwise.
     */
    protected boolean move(CoordinatesElement<E> c, Point2D p) {
        return move(c, p.getX(), p.getY());
    }

    /**
     * Helper method which gets a list from {@link #elements}.
     * 
     * @param p Coordinates.
     * @return The list.
     */
    private List<CoordinatesElement<E>> getListAt(Point2D p) {
        int x = Math.min((int) p.getX(), maxRowNumber - 1);
        int y = Math.min((int) p.getY(), maxColNumber - 1);
        return elements[Math.max(x, 0)][Math.max(y, 0)];
    }
    

    /**
     * Ensures that the structure can correctly store the given coordinates.
     * 
     * @param width  X coordinate.
     * @param height Y coordiinate.
     */
    protected void ensureSize(double width, double height) {
        // ensures that the coordinates can get inside
        // e.g if ensureSize(p.x, p.y) p can be inside the array
        width++;
        height++;

        if(width <= maxWidth.get() && height <= maxHeight.get()) {
            return;
        }
        
        double newWidth = Math.max(width, maxWidth.get());
        double newHeight = Math.max(height, maxHeight.get());
        
        var newElements = newArray(newWidth, newHeight);
        
        for(var e : this) {
            int i = (int) Math.min(newElements.length-1d, (e.getX() / newWidth * newElements.length));
            int j = (int) Math.min(newElements[0].length-1d, (e.getY() / newHeight * newElements[0].length));
            newElements[i][j].add(e);
        }
        
        elements = newElements;
        
        rowRange = (int) newWidth / elements.length;
        colRange = (int) newHeight / elements[0].length;
        
        maxWidth.set(newWidth);
        maxHeight.set(newHeight);
    }

    /**
     * Whether to resize the width on row range.
     * 
     * @param newWidth New total width.
     * @return {@code true} if yes; {@code false} otherwise.
     */
    private boolean resizeWidthOnRange(double newWidth) {
        double actualMaxWidth = (double) elements.length * rowRange;
        
        if(newWidth <= actualMaxWidth) {
            return false;
        }

        if(elements.length >= maxRowNumber) {
            return true;
        }
        
        return (int) (newWidth / elements.length) <= (int) (rowRange * maxRowRangeIncrease);
    }

    /**
     * Whether to resize the height on row range.
     *
     * @param newHeight New total width.
     * @return {@code true} if yes; {@code false} otherwise.
     */
    private boolean resizeHeightOnRange(double newHeight) {
        double actualMaxHeight = (double) elements[0].length * colRange;

        if(newHeight <= actualMaxHeight) {
            return false;
        }

        if(elements[0].length >= maxColNumber) {
            return true;
        }

        return (int) (newHeight / elements[0].length) <= (int) (colRange * maxColRangeIncrease);
    }

    /**
     * Creates the new array with the given sizes.
     * 
     * @param newWidth  New total width.
     * @param newHeight New total height.
     * @return New array.
     */
    @SuppressWarnings("unchecked")
    private List<CoordinatesElement<E>>[][] newArray(double newWidth, double newHeight) {
        int row = elements.length;
        int col = elements[0].length;

        if(!resizeWidthOnRange(newWidth)) {
            row = (int) Math.min(maxRowNumber, newWidth / rowRange);
        }
        
        if(!resizeHeightOnRange(newHeight)) {
            col = (int) Math.min(maxColNumber, newHeight / colRange);
        }
        
        var p = (List<CoordinatesElement<E>>[][]) Array.newInstance(List.class, row, col);
        
        for(var t : p) {
            for(int i = 0; i < t.length; i++) {
                t[i] = new ArrayList<>();
            }
        }
        
        return p;
    }
  
    
    /**
     * {@inheritDoc}
     *
     * @param topLeftX     Top left corner X coordinate.
     * @param topLeftY     Top left corner Y coordinate.
     * @param bottomRightX Bottom right corner X coordinate.
     * @param bottomRightY Bottom right corner Y coordinate.
     * @return Collection of all elements within the area.
     */
    @Override
    public Collection<CoordinatesElement<E>> between(double topLeftX, double topLeftY, 
                                                     double bottomRightX, double bottomRightY) {
        List<CoordinatesElement<E>> l = new ArrayList<>();

        // fail fast 
        if(topLeftX >= bottomRightX || topLeftY >= bottomRightY
            || topLeftX >= maxWidth.get() || topLeftY >= maxHeight.get()
            || bottomRightX <= 0 || bottomRightY <= 0) {
            return l;
        }
        
        int minI = Math.max((int) (topLeftX / maxWidth.get() * elements.length), 0);
        int minJ = Math.max((int) (topLeftY / maxHeight.get() * elements[0].length), 0);

        int maxI = Math.min((int) (bottomRightX / maxWidth.get() * elements.length), elements.length);
        int maxJ = Math.min((int) (bottomRightY / maxHeight.get() * elements[0].length), elements[0].length);
        
        for(int i = minI; i < maxI+1; i++) {
            for(int j = minJ; j < maxJ+1; j++) {
                if(i == minI || i == maxI || j == minJ || j == maxJ) {
                    for(var e : elements[i][j]) {
                        if(e.isIn(topLeftX, topLeftY, bottomRightX, bottomRightY)) {
                            l.add(e);
                        }
                    }
                }
                else {
                    l.addAll(elements[i][j]);
                }
            }
        }
        
        return l;
    }

    /**
     * {@inheritDoc}
     *
     * @param topLeftX     Top left corner X coordinate.
     * @param topLeftY     Top left corner Y coordinate.
     * @param bottomRightX Bottom right corner X coordinate.
     * @param bottomRightY Bottom right corner Y coordinate.
     * @param condition    Filtering condition.
     * @return Collection of all elements within the area.
     */
    @Override
    public Collection<CoordinatesElement<E>> between(double topLeftX, double topLeftY, 
                                                     double bottomRightX, double bottomRightY, 
                                                     Predicate<E> condition) {
        List<CoordinatesElement<E>> l = new ArrayList<>();

        if(topLeftX >= bottomRightX || topLeftY >= bottomRightY
            || topLeftX >= maxWidth.get() || topLeftY >= maxHeight.get()
            || bottomRightX <= 0 || bottomRightY <= 0) {
            return l;
        }

        int minI = Math.max((int) (topLeftX / maxWidth.get() * elements.length), 0);
        int minJ = Math.max((int) (topLeftY / maxHeight.get() * elements[0].length), 0);

        int maxI = Math.min((int) (bottomRightX / maxWidth.get() * elements.length), elements.length);
        int maxJ = Math.min((int) (bottomRightY / maxHeight.get() * elements[0].length), elements[0].length);

        for(int i = minI; i < maxI+1; i++) {
            for(int j = minJ; j < maxJ+1; j++) {
                for(var e : elements[i][j]) {
                    if(e.isIn(topLeftX, topLeftY, bottomRightX, bottomRightY) && condition.test(e.getElement())) {
                        l.add(e);
                    }
                }
            }
        }

        return l;
    }

    /**
     * Minimum width of this structure.
     *
     * @return Min width property.
     */
    @Override
    public ReadOnlyDoubleProperty minimumWidth() {
        return minWidth;
    }

    /**
     * Minimum height of this structure.
     *
     * @return Min height property.
     */
    @Override
    public ReadOnlyDoubleProperty minimumHeight() {
        return minHeight;
    }

    /**
     * {@inheritDoc}
     *
     * @return Max height property.
     */
    @Override
    public ReadOnlyDoubleProperty maximumHeight() {
        return maxHeight;
    }

    /** 
     * {@inheritDoc}
     *
     * @return Max width property.
     */
    @Override
    public ReadOnlyDoubleProperty maximumWidth() {
        return maxWidth;
    }

    /**
     * {@inheritDoc}
     *
     * @return The number of elements in this structure.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     *
     * @param c The element to check.
     * @return {@code true} if it contains it; {@code false} otherwise.
     */
    @Override
    public boolean contains(CoordinatesElement<E> c) {
        return getListAt(indexesOf(c)).contains(c);
    }

    /**
     * {@inheritDoc}
     *
     * @param element The element to find.
     * @return The coordinates of the given element if it is in the structure.
     */
    @Override
    public Optional<CoordinatesElement<E>> coordinatesOf(E element) {
        for(var c : this) {
            if(c.getElement().equals(element)) {
                return Optional.of(c);
            }
        }

        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     *
     * @param elements The elements to find.
     * @return The coordinates of the given elements that are in the structure.
     */
    @Override
    public Map<E, CoordinatesElement<E>> coordinatesOf(Collection<E> elements) {
        var m = new HashMap<E, CoordinatesElement<E>>();

        for(var e : elements) {
            coordinatesOf(e).ifPresent(
                c -> m.put(e, c)
            );
        }

        return m;
    }


    /**
     * {@inheritDoc}
     *
     * @return an Iterator.
     */
    @Override
    public CoordinatesIterator<CoordinatesElement<E>> iterator() {
        return new MatrixIterator();
    }

    /**
     * {@inheritDoc}
     *
     * @param action The action to be performed for each element
     */
    @Override
    public void forEach(Consumer<? super CoordinatesElement<E>> action) {
        // will automatically throw ConcurrentModififcationException
        for(var e : this) {
            action.accept(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(var e : elements) {
            sb.append(Arrays.toString(e)).append('\n');
        }
        return sb.toString();
    }

    
    /**
     * 3D internalItr over {@link #elements}.
     */
    protected class MatrixIterator implements CoordinatesIterator<CoordinatesElement<E>> {

        /**
         * Current row index.
         */
        private int currentRow = 0;

        /**
         * Current column index.
         */
        private int currentCol = 0;

        /**
         * Current index in the current row and column.
         */
        private int currentIndex = -1;
        
        /**
         * Row index of the next element. {@code -1} if no next element.
         */
        private int nextRow = 0;

        /**
         * Column index of the next element. {@code -1} if no next element.
         */
        private int nextCol = 0;

        /**
         * Index of the next element. {@code -1} if no next element.
         */
        private int nextIndex = -1;

        /**
         * Expected modification count for concurrent modification.
         * Useless here as this version of the class is 'immutable'.
         */
        private int expectedModCount;

        /**
         * Whether the end of the structure was reached.
         */
        private boolean reachedTheEnd = false;
        

        /**
         * Constructor.
         */
        protected MatrixIterator() {
            expectedModCount = modCount;
            updateNext(false);
        }
        
        /**
         * {@inheritDoc}
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            checkForComod();
            
            updateNext(false);
            return !reachedTheEnd;
        }

        /**
         * {@inheritDoc}
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public CoordinatesElement<E> next() {
            checkForComod();
            
            updateNext(false);
            
            if(reachedTheEnd) {
                throw new NoSuchElementException();
            }
            
            currentRow = nextRow;
            currentCol = nextCol;
            currentIndex = nextIndex;
            return elements[nextRow][nextCol].get(nextIndex);
        }


        /**
         * Getter for the potential next element.
         * 
         * @return Optional of the next element.
         */
        protected Optional<CoordinatesElement<E>> getPotentialNext() {
            if(nextRow == -1) {
                return Optional.empty();
            }
            
            try {
                return Optional.of(elements[nextRow][nextCol].get(nextIndex));
            } catch(IndexOutOfBoundsException e) {
                return Optional.empty();
            }
        }

        /**
         * Whether this internalItr has reached the end of the matrix.
         * 
         * @return Whether this internalItr has reached the end of the matrix.
         */
        protected boolean hasReachedTheEnd() {
            return reachedTheEnd;
        }

        /**
         * Sets the expected mod count to the current mod count.
         */
        protected void updateExpectedModCount() {
            expectedModCount = modCount;
        }

        /**
         * Updates {@link #nextRow}, {@link #nextCol} and
         * {@link #nextIndex} to the coordinates of the next
         * element; sets to {@code -1} if there is none.
         * 
         * @param force Forces an update.
         */
        protected void updateNext(boolean force) {
            if(!shouldUpdate() && !force) {
                return;
            }

            // next element is in the same row and same column (= in the same list)
            if(currentIndex < elements[currentRow][currentCol].size() - 1) {
                nextIndex++;
                return;
            }

            // next element is in the same row (= same sub array)
            for(int c = currentCol + 1; c < elements[currentRow].length; c++) {
                if(!elements[currentRow][c].isEmpty()) {
                    nextCol = c;
                    nextIndex = 0;
                    return;
                }
            }
            
            // different row
            for(int i = currentRow + 1; i < elements.length; i++) {
                for(int j = 0; j < elements[i].length; j++) {
                    if(!elements[i][j].isEmpty()) {
                        nextRow = i;
                        nextCol = j;
                        nextIndex = 0;
                        return;
                    }
                }
            }

            // reached the end
            nextRow = -1;
            nextCol = -1;
            nextIndex= -1;
            reachedTheEnd = true;
        }
        
        
        /**
         * Whether to search for the next element.
         * 
         * @return {@code true} if the next element should be searched.
         */
        private boolean shouldUpdate() {
            return !reachedTheEnd
                && currentIndex == nextIndex 
                && currentCol == nextCol 
                && currentRow == nextRow;
        }

        /**
         * Concurrent modification checker.
         */
        private void checkForComod() {
            if(expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
