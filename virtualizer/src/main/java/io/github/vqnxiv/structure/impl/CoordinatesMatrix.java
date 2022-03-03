package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.CoordinatesIterator;
import io.github.vqnxiv.structure.CoordinatesStructure;
import io.github.vqnxiv.structure.LocalizedStructure;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.function.Consumer;


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
    
    public static final double DEFAULT_MAX_WIDTH = 1_000d;
    public static final double DEFAULT_MAX_HEIGHT = 1_000d;


    /**
     * The elements.
     */
    protected List<CoordinatesElement<E>>[][] elements;
    
    /**
     * Current row range.
     */
    private int rowRange;

    /**
     * Current column range.
     */
    private int colRange;
    
    // NOT NEEDED?
    private int rowNumber;
    private int colNumber;
    
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
     * Maximum height in this structure.
     */
    private final DoubleProperty maxHeight = new SimpleDoubleProperty();

    /**
     * Maximum width in this structure.
     */
    private final DoubleProperty maxWidth = new SimpleDoubleProperty();

    /**
     * Concurrent modification checker for {@link MatrixIterator}. 
     */
    private int modCount = 0;

    
    
    public CoordinatesMatrix(Collection<E> el) {
        this(
            el, DEFAULT_ROW_NUMBER, DEFAULT_COL_NUMBER,
            DEFAULT_MAX_WIDTH_INCREASE, DEFAULT_MAX_HEIGHT_INCREASE,
            DEFAULT_MAX_ROW_NUMBER, DEFAULT_MAX_COL_NUMBER
        );
    }
    
    public CoordinatesMatrix(Collection<E> el, int initialRowNumber, int initialColNumber) {
        this(
            el, initialRowNumber, initialColNumber, 
            DEFAULT_MAX_WIDTH_INCREASE, DEFAULT_MAX_HEIGHT_INCREASE,
            DEFAULT_MAX_ROW_NUMBER, DEFAULT_MAX_COL_NUMBER
        );
    }
    
    public CoordinatesMatrix(Collection<E> el, int initialRowNumber, int initialColNumber,
                             float maxRowRangeIncrease, float maxColRangeIncrease) {
        this(
            el, initialRowNumber, initialColNumber,
            maxRowRangeIncrease, maxColRangeIncrease,
            DEFAULT_MAX_ROW_NUMBER, DEFAULT_MAX_COL_NUMBER
        );
    }
    
    @SuppressWarnings("unchecked")
    public CoordinatesMatrix(Collection<E> el, int initialRowNumber, int initialColNumber,
                             float maxRowRangeIncrease, float maxColRangeIncrease,
                             int maxRowNumber, int maxColNumber) {
        
        if(initialRowNumber < 1 || initialColNumber < 1 
            || maxRowRangeIncrease < 1.0 || maxColRangeIncrease < 1.0
            || maxRowNumber < initialRowNumber || maxColNumber < initialColNumber) {
            throw new IllegalArgumentException();
        }
        
        this.rowNumber = initialRowNumber;
        this.colNumber = initialColNumber;
        
        this.maxRowRangeIncrease = maxRowRangeIncrease;
        this.maxColRangeIncrease = maxColRangeIncrease;

        this.maxRowNumber = maxRowNumber;
        this.maxColNumber = maxColNumber;
        
        this.elements = (List<CoordinatesElement<E>>[][]) new List[rowNumber][colNumber];
        
        maxWidth.set(DEFAULT_MAX_WIDTH);
        maxHeight.set(DEFAULT_MAX_HEIGHT);
        
        rowRange = (int) (maxWidth.get() / rowNumber);
        colRange = (int) (maxHeight.get() / colNumber);
        
        for(int i = 0; i < elements.length; i++) {
            for(int j = 0; j < elements[0].length; j++) {
                elements[i][j] = new ArrayList<>();
            }
        }
        
        for(E e : el) {
            elements[0][0].add(new CoordinatesElement<>(e, 0, 0));
        }
        
    }
    

    protected Point2D getCoordsInArray(CoordinatesElement<E> p) {
        int i = (int) (p.getX() / maxWidth.get() * rowNumber);
        int j = (int) (p.getY() / maxHeight.get() * colNumber);
        return new Point2D(i, j);
    }

    protected Point2D getCoordsInArray(Point2D p) {
        
        int i = (int) (p.getX() / maxWidth.get() * rowNumber);
        int j = (int) (p.getY() / maxHeight.get() * colNumber);
        return new Point2D(i, j);
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
    public Collection<CoordinatesElement<E>> between(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        List<CoordinatesElement<E>> l = new ArrayList<>();

        // fail fast 
        if(topLeftX >= bottomRightX || topLeftY >= bottomRightY
            || topLeftX >= maxWidth.get() || topLeftY >= maxHeight.get()
            || bottomRightX <= 0 || bottomRightY <= 0) {
            return l;
        }
        
        int minI = Math.max((int) (topLeftX / maxWidth.get() * rowNumber), 0);
        int minJ = Math.max((int) (topLeftY / maxHeight.get() * colNumber), 0);

        int maxI = Math.min((int) (bottomRightX / maxWidth.get() * rowNumber), rowNumber);
        int maxJ = Math.min((int) (bottomRightY / maxHeight.get() * colNumber), rowNumber);
        
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
    
    
    protected void ensureSize(double width, double height) {
        width++;
        height++;
        
        double actualMaxWidth = (double) rowNumber * rowRange;
        double actualMaxHeight = (double) colNumber * colRange;
        
        if(width <= actualMaxWidth && height <= actualMaxHeight) {
            return;
        }
        
        int possNewRowRange = (int) (width / rowNumber);
        int possNewColRange = (int) (height / colNumber);
        
        boolean resizeOnRowRange = possNewRowRange <= (int) (rowRange * maxRowRangeIncrease);
        boolean resizeOnColRange = possNewColRange <= (int) (colRange * maxColRangeIncrease);
        
        
        int newRowNumber = (resizeOnRowRange) ? rowNumber : (int) (width / rowRange);
        int newColNumber = (resizeOnColRange) ? colNumber : (int) (height / colRange);

        var newElements = (List<CoordinatesElement<E>>[][]) new List[newRowNumber][newColNumber];
        for(var t : newElements) {
            for(int i = 0; i < t.length; i++) {
                t[i] = new ArrayList<>();
            }
        }
        
     
        for(var e : this) {
            int i = (int) (e.getX() / width * newRowNumber);
            int j = (int) (e.getY() / height * newColNumber);
            newElements[i][j].add(e);
        }
        
        elements = newElements;
        rowNumber = newRowNumber;
        colNumber = newColNumber;
        rowRange = (resizeOnRowRange) ? possNewRowRange : rowRange;
        colRange = (resizeOnColRange) ? possNewColRange : colRange;
        maxWidth.set(width);
        maxHeight.set(height);
    }
    
    private void reposition() {
        
    }
    

    /**
     * {@inheritDoc}
     *
     * @param topLeft     Top left corner.
     * @param bottomRight Bottom right corner.
     * @return Collection of all elements within the area.
     */
    @Override
    public Collection<CoordinatesElement<E>> between(Point2D topLeft, Point2D bottomRight) {
        return between(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
    }

    /**
     * {@inheritDoc}
     *
     * @return Max height property.
     */
    @Override
    public DoubleProperty maximumHeight() {
        return maxHeight;
    }

    /** 
     * {@inheritDoc}
     *
     * @return Max width property.
     */
    @Override
    public DoubleProperty maximumWidth() {
        return maxWidth;
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
        //CoordinatesStructure.super.forEach(action);
    }


    /**
     * 3D iterator over {@link #elements}.
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
        private int nextIndex = 0;

        /**
         * Expected modification count for concurrent modification.
         * Useless here as this version of the class is 'immutable'.
         */
        private final int expectedModCount;
        

        /**
         * Constructor.
         */
        MatrixIterator() {
            expectedModCount = modCount;
            
        }
        
        /**
         * {@inheritDoc}
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            checkForComod();
            
            updateNext();
            return nextRow != - 1;
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
            
            updateNext();
            
            if(nextRow == -1) {
                throw new NoSuchElementException();
            }
            
            currentRow = nextRow;
            currentCol = nextCol;
            currentIndex = nextIndex;
            return elements[nextRow][nextCol].get(nextIndex);
        }

        /**
         * Updates {@link #nextRow}, {@link #nextCol} and
         * {@link #nextIndex} to the coordinates of the next
         * element; sets to {@code -1} if there is none.
         */
        private void updateNext() {
            if(!shouldUpdate()) {
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
        }

        /**
         * Whether to search for the next element.
         * 
         * @return {@code true} if the next element should be searched.
         */
        private boolean shouldUpdate() {
            return nextRow > -1 
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
