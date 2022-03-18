package io.github.vqnxiv.structure;


import javafx.geometry.Point2D;

import java.util.List;
import java.util.Map;


/**
 * Class which describes a change to a {@link CoordinatesStructure}.
 * There are three possible kinds of change:
 * <ul>
 *     <li>moving one or more elements;</li>
 *     <li>adding one or more elements;</li>
 *     <li>removing one or more elements.</li>
 * </ul>
 * <p>
 * Changes are created through the three factory methods
 * {@link #moved(LayoutableStructure, Map, Point2D, Point2D)},
 * {@link #added(MutableStructure, List, Point2D, Point2D)} and
 * {@link #removed(MutableStructure, List, Point2D, Point2D)}.
 */
/*
    Possibility of merging all three subclass into one?
    if there is a way that two sort of changes happen at the same time
    e.g adding a new elements to an autolayouted structure which causes
    the whole structure to be relayouted
 */
/*
    Another thing is to possibly merge all 3 structure getters into
    a single CoordinatesStructure<E> getter to make sure it doesnt
    get modified, though this removes the possibility to unregister
    the consumers
 */
public abstract class StructureChange {

    /**
     * Factory method for a move change.
     * 
     * @param structure   The structure in which the change happened.
     * @param elements    The concerned elements.
     * @param topLeft     The top left coordinates of the area in which the change happened.
     * @param bottomRight The bottom right coordinates of the area in which the change happened.
     * @param <E> The type of elements in the structure.
     * @return A change object containing the above information.
     */
    public static <E> Move<E> moved(LayoutableStructure<E> structure, Map<CoordinatesElement<E>, 
        Point2D> elements, Point2D topLeft, Point2D bottomRight) {
        return new Move<>(structure, elements, topLeft, bottomRight);
    }

    /**
     * Factory method for an addition change.
     *
     * @param structure   The structure in which the change happened.
     * @param elements    The concerned elements.
     * @param topLeft     The top left coordinates of the area in which the change happened.
     * @param bottomRight The bottom right coordinates of the area in which the change happened.
     * @param <E> The type of elements in the structure.
     * @return A change object containing the above information.
     */
    public static <E> Addition<E> added(MutableStructure<E> structure, List<CoordinatesElement<E>> elements, 
                                        Point2D topLeft, Point2D bottomRight) {
        return new Addition<>(structure, elements, topLeft, bottomRight);
    }

    /**
     * Factory method for a removal change.
     *
     * @param structure   The structure in which the change happened.
     * @param elements    The concerned elements.
     * @param topLeft     The top left coordinates of the area in which the change happened.
     * @param bottomRight The bottom right coordinates of the area in which the change happened.
     * @param <E> The type of elements in the structure.
     * @return A change object containing the above information.
     */
    public static <E> Removal<E> removed(MutableStructure<E> structure, List<CoordinatesElement<E>> elements, 
                                         Point2D topLeft, Point2D bottomRight) {
        return new Removal<>(structure, elements, topLeft, bottomRight);
    }


    /**
     * Top left coordinates.
     */
    private final Point2D topLeft;

    /**
     * Bottom right coordinates.
     */
    private final Point2D bottomRight;

    
    /**
     * Constructor.
     * 
     * @param topLeft     Top left coordinates.
     * @param bottomRight Bottom right coordinates.
     */
    private StructureChange(Point2D topLeft, Point2D bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    
    /**
     * Getter for the top left coordinates.
     * 
     * @return The top left coordinates.
     */
    public Point2D topLeft() {
        return topLeft;
    }

    /**
     * Getter for the top right coordinates.
     * 
     * @return The bottom right coordinates.
     */
    public Point2D bottomRight() {
        return bottomRight;
    }

    
    /**
     * Concrete implementation of {@link StructureChange}.
     * 
     * @param <E> Type of elements in the structure.
     */
    public static class Move<E> extends StructureChange {

        /**
         * The structure.
         */
        private final LayoutableStructure<E> structure;

        /**
         * The elements.
         */
        private final Map<CoordinatesElement<E>, Point2D> elements;


        /**
         * Constructor.
         *
         * @param structure   The structure in which the change happened.
         * @param elements    The concerned elements.
         * @param topLeft     The top left coordinates of the area in which the change happened.
         * @param bottomRight The bottom right coordinates of the area in which the change happened.
         */
        private Move(LayoutableStructure<E> structure, Map<CoordinatesElement<E>, Point2D> elements, 
                    Point2D topLeft, Point2D bottomRight) {
            super(topLeft, bottomRight);
            this.structure = structure;
            this.elements = Map.copyOf(elements);
        }

        
        /**
         * Getter for the structure in which the change happened.
         * 
         * @return The structure in which the change happened.
         */
        public LayoutableStructure<E> structure() {
            return structure;
        }

        /**
         * The concerned elements.
         *
         * @return The concerned elements.
         */
        public Map<CoordinatesElement<E>, Point2D> elements() {
            return elements;
        }

        @Override
        public String toString() {
            return "Move {" +
                "structure = " + structure +
                ", elements = " + elements +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            
            if(o instanceof Move<?> m) {
                return structure.equals(m.structure) && elements.equals(m.elements);
            }
            
            return false;
        }

        @Override
        public int hashCode() {
            int result = structure != null ? structure.hashCode() : 0;
            result = 31 * result + (elements != null ? elements.hashCode() : 0);
            return result;
        }
    }


    /**
     * Addition and removal are the same thing. 
     * Just a different type so they aren't mistaken, seems safer than a 
     * {@code boolean isAddition();}.
     * Maybe just merge and make it {@code added();} and {@code removed();}
     * + could also merge w/ moved? {@code moved();}
     * 
     * @param <E> Type of elements in the structure.
     */
    private abstract static class AbstractMut<E> extends StructureChange {

        /**
         * The structure.
         */
        private final MutableStructure<E> structure;

        /**
         * The elements.
         */
        private final List<CoordinatesElement<E>> elements;


        /**
         * Constructor.
         *
         * @param structure   The structure in which the change happened.
         * @param elements    The concerned elements.
         * @param topLeft     The top left coordinates of the area in which the change happened.
         * @param bottomRight The bottom right coordinates of the area in which the change happened.
         */
        private AbstractMut(MutableStructure<E> structure, List<CoordinatesElement<E>> elements, 
                            Point2D topLeft, Point2D bottomRight) {
            super(topLeft, bottomRight);
            this.structure = structure;
            this.elements = List.copyOf(elements);
        }


        /**
         * Getter for the structure in which the change happened.
         *
         * @return The structure in which the change happened.
         */
        public MutableStructure<E> structure() {
            return structure;
        }

        /**
         * The concerned elements.
         *
         * @return The concerned elements.
         */
        public List<CoordinatesElement<E>> elements() {
            return elements;
        }

        
        @Override
        public String toString() {
            return "structure = " + structure +
                ", elements = " + elements +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;

            if(o instanceof AbstractMut<?> m) {
                return structure.equals(m.structure) && elements.equals(m.elements);
            }

            return false;
        }

        @Override
        public int hashCode() {
            int result = structure != null ? structure.hashCode() : 0;
            result = 31 * result + (elements != null ? elements.hashCode() : 0);
            return result;
        }
    }


    /**
     * Concrete implementation of {@link StructureChange}.
     *
     * @param <E> Type of elements in the structure.
     */
    public static class Addition<E> extends AbstractMut<E> {
        
        /**
         * Constructor.
         *
         * @param structure   The structure in which the change happened.
         * @param elements    The concerned elements.
         * @param topLeft     The top left coordinates of the area in which the change happened.
         * @param bottomRight The bottom right coordinates of the area in which the change happened.
         */
        private Addition(MutableStructure<E> structure, List<CoordinatesElement<E>> elements, 
                        Point2D topLeft, Point2D bottomRight) {
            super(structure, elements, topLeft, bottomRight);
        }
        

        @Override
        public String toString() {
            return "Addition {" + super.toString();
        }
        
        @Override
        public boolean equals(Object o) {
            // don't want removal to return true if same elements and structure
            if(o instanceof Addition<?> a) {
                return super.equals(a);
            }

            return false;
        }
    }


    /**
     * Concrete implementation of {@link StructureChange}.
     *
     * @param <E> Type of elements in the structure.
     */
    public static class Removal<E> extends AbstractMut<E> {

        /**
         * Constructor.
         *
         * @param structure   The structure in which the change happened.
         * @param elements    The concerned elements.
         * @param topLeft     The top left coordinates of the area in which the change happened.
         * @param bottomRight The bottom right coordinates of the area in which the change happened.
         */
        private Removal(MutableStructure<E> structure, List<CoordinatesElement<E>> elements,
                        Point2D topLeft, Point2D bottomRight) {
            super(structure, elements, topLeft, bottomRight);
        }


        @Override
        public String toString() {
            return "Removal {" + super.toString();
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof Removal<?> a) {
                return super.equals(a);
            }

            return false;
        }
    }
}
