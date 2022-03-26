package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.RandomLayout;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.StructureChange;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class MutableMatrixTest {
    
    
    private record Pojo(String name) { }

    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three"));

    MutableMatrix<Pojo> matrix = new MutableMatrix<>(l);

    @Test
    void add() {
        int size = matrix.size();
        assertNotEquals(0, size);
        
        var p = new Pojo("four");
        matrix.addValue(p);
        
        assertEquals(size + 1, matrix.size());
        assertTrue(matrix.containsValue(p));
    }

    @Test
    void addAll() {
        int size = matrix.size();
        
        var p1 = new Pojo("four");
        var p2 = new Pojo("five");
        var p3 = new Pojo("six");
        var l2 = List.of(p1, p2, p3);
        
        matrix.addAllValues(l2);
        assertEquals(size + l2.size(), matrix.size());
        l2.forEach(c -> assertTrue(matrix.containsValue(c)));
    }

    @Test
    void addAt() {
        int size = matrix.size();
        
        var p = new Pojo("four");
        var pC = new Point2D(500d, 500d);
        matrix.addCoordinates(p, pC);
        
        assertEquals(size + 1, matrix.size());
        assertTrue(matrix.containsValue(p));
        assertTrue(matrix.containsCoordinates(new CoordinatesElement<>(p, pC)));
        assertEquals(pC, matrix.coordinatesOf(p).get().getXY());
    }

    @Test
    void testAddAt() {
        int size = matrix.size();

        var p = new Pojo("four");
        var pC = new Point2D(500d, 500d);
        matrix.addCoordinates(new CoordinatesElement<>(p, pC));

        assertEquals(size + 1, matrix.size());
        assertTrue(matrix.containsValue(p));
        assertTrue(matrix.containsCoordinates(new CoordinatesElement<>(p, pC)));
        assertEquals(pC, matrix.coordinatesOf(p).get().getXY());
    }

    @Test
    void addAllAt() {
        int size = matrix.size();

        var p1 = new Pojo("four");
        var p2 = new Pojo("five");
        var p3 = new Pojo("six");
        var m = Map.of(
            p1, new Point2D(10, 10),
            p2, new Point2D(20, 20),
            p3, new Point2D(30, 30)
        );

        matrix.addAllCoordinates(m);
        assertEquals(size + m.size(), matrix.size());
        m.forEach(
            (k, v) -> {
                assertTrue(matrix.containsValue(k));
                assertEquals(v, matrix.coordinatesOf(k).get().getXY());
            }
        );
    }

    @Test
    void testAddAllAt() {
        int size = matrix.size();

        var p1 = new Pojo("four");
        var p2 = new Pojo("five");
        var p3 = new Pojo("six");
        var m = Map.of(
            p1, new Point2D(10, 10),
            p2, new Point2D(20, 20),
            p3, new Point2D(30, 30)
        );
        
        var l2 = m.entrySet()
            .stream()
            .map(e -> new CoordinatesElement<>(e.getKey(), e.getValue()))
            .toList();
        
        matrix.addAllCoordinates(l2);
        assertEquals(size + m.size(), matrix.size());
        m.forEach(
            (k, v) -> {
                assertTrue(matrix.containsValue(k));
                assertEquals(v, matrix.coordinatesOf(k).get().getXY());
            }
        );
        l2.forEach(c -> assertTrue(matrix.containsCoordinates(c)));
    }
    
    
    @Test
    void remove() {
        int size = matrix.size();
        
        var p = new Pojo("one");
        matrix.removeValue(p);

        assertEquals(size - 1, matrix.size());
        assertFalse(matrix.containsValue(p));
    }

    @Test
    void removeAll() {
        int size = matrix.size();

        var p1 = new Pojo("one");
        var p2 = new Pojo("two");
        var p3 = new Pojo("three");
        var l2 = List.of(p1, p2, p3);

        matrix.removeAllValues(l2);
        assertEquals(size - l2.size(), matrix.size());
        l2.forEach(c -> assertFalse(matrix.containsValue(c)));
    }

    @Test
    void removeAt() {
        var p = matrix.iterator().next();

        int size = matrix.size();
        matrix.removeCoordinates(p);

        assertEquals(size - 1, matrix.size());
        assertFalse(matrix.containsCoordinates(p));
    }

    @Test
    void removeAllAt() {
        var itr = matrix.iterator();
        var l2 = List.of(itr.next(), itr.next());

        int size = matrix.size();
        matrix.removeAllCoordinates(l2);

        assertEquals(size - l2.size(), matrix.size());
        l2.forEach(c -> assertFalse(matrix.containsCoordinates(c)));
    }

    @Test
    void removeIf() {
        matrix.removeValuesIf(p -> p.name().equals("one"));
        assertFalse(matrix.containsValue(new Pojo("one")));
    }

    @Test
    void removeCoordinatesIf() {
        var p = new Pojo("one");
        assertTrue(matrix.containsValue(p));
        matrix.removeCoordinatesIf(c -> c.getElement().equals(p));
        assertFalse(matrix.containsValue(p));
    }

    @Test
    void clear() {
        assertFalse(matrix.isEmpty());
        assertNotEquals(0, matrix.size());
        assertTrue(matrix.containsValue(new Pojo("one")));
        
        matrix.clear();
        assertTrue(matrix.isEmpty());
        assertEquals(0, matrix.size());
        assertFalse(matrix.containsValue(new Pojo("one")));
        assertEquals(0d, matrix.getMinimumWidth());
        assertEquals(0d, matrix.getMinimumHeight());
        assertEquals(0d, matrix.getMaximumWidth());
        assertEquals(0d, matrix.getMaximumHeight());
    }


    @Test
    void correctPropertyValues() {
        assertEquals(0d, matrix.getMinimumWidth());
        assertEquals(0d, matrix.getMinimumHeight());
        assertEquals(0d, matrix.getMaximumWidth());
        assertEquals(0d, matrix.getMaximumHeight());

        var itr = matrix.iterator();
        var p = itr.next();
        var p2 = itr.next();
        var p3 = itr.next();
        var p4 = new CoordinatesElement<>(new Pojo("four"), 100d, 80d);
        
        matrix.addCoordinates(p4);

        assertEquals(0d, matrix.getMinimumWidth());
        assertEquals(0d, matrix.getMinimumHeight());
        assertEquals(100d, matrix.getMaximumWidth());
        assertEquals(80d, matrix.getMaximumHeight());

        matrix.removeAllCoordinates(List.of(p, p2, p3));

        assertEquals(100d, matrix.getMinimumWidth());
        assertEquals(80d, matrix.getMinimumHeight());
        assertEquals(100d, matrix.getMaximumWidth());
        assertEquals(80d, matrix.getMaximumHeight());
    }
    
    @Test
    void removeThroughItr() {
        var itr = matrix.iterator();
        var p = itr.next();
        assertTrue(matrix.containsCoordinates(p));
        itr.remove();
        assertFalse(matrix.containsCoordinates(p));
        assertDoesNotThrow(itr::next);
    }
    
    @Test
    void itrThrowsOnComod() {
        var itr = matrix.iterator();
        itr.next();
        matrix.addValue(new Pojo("four"));
        assertThrows(ConcurrentModificationException.class, itr::next);
    }
    
    @Test
    void doubleItrRemoveThrows() {
        var itr = matrix.iterator();
        itr.next();
        itr.remove();
        assertThrows(IllegalStateException.class, itr::remove);
    }

    @Test
    void addListenerTest() {
        AtomicReference<StructureChange.Addition<?>> aRef = new AtomicReference<>();
        matrix.addAdditionListener(this, aRef::set);
        
        var cp = new CoordinatesElement<>(new Pojo("four"), new Point2D(100d, 100d));
        var cp2 = new CoordinatesElement<>(new Pojo("four"), new Point2D(10d, 10d));
        matrix.addAllCoordinates(List.of(cp, cp2));

        var m = aRef.get();
        assertEquals(matrix, m.structure());
        assertEquals(2, m.elements().size());
        assertTrue(m.elements().contains(cp));
        assertTrue(m.elements().contains(cp2));
        assertEquals(cp2.getXY(), m.topLeft());
        assertEquals(cp.getXY(), m.bottomRight());
    }

    @Test
    void rmListenerTest() {
        AtomicReference<StructureChange.Removal<?>> rRef = new AtomicReference<>();
        matrix.addRemovalListener(this, rRef::set);
        
        var rl = new RandomLayout<>(matrix); 
        rl.apply();

        var itr = matrix.iterator();
        var p = itr.next();
        var p2 = itr.next();
        
        var tl = new Point2D(
            Math.min(p.getX(), p2.getX()),
            Math.min(p.getY(), p2.getY())
        );
        
        var br = new Point2D(
            Math.max(p.getX(), p2.getX()),
            Math.max(p.getY(), p2.getY())
        );
        
        matrix.removeAllCoordinates(List.of(p, p2));
        
        var m = rRef.get();
        assertEquals(matrix, m.structure());
        assertEquals(2, m.elements().size());
        assertTrue(m.elements().contains(p));
        assertTrue(m.elements().contains(p2));
        assertEquals(tl, m.topLeft());
        assertEquals(br, m.bottomRight());
    }
}
