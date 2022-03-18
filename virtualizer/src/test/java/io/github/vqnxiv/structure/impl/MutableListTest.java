package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.structure.CoordinatesElement;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class MutableListTest {

    private record Pojo(String name) { }

    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three"));

    MutableList<Pojo> cList = new MutableList<>(l);

    
    @Test
    void add() {
        int size = cList.size();
        assertNotEquals(0, size);
        
        var p = new Pojo("four");
        cList.add(p);
        
        assertEquals(size + 1, cList.size());
        assertTrue(cList.contains(p));
    }

    @Test
    void addAll() {
        int size = cList.size();
        
        var p1 = new Pojo("four");
        var p2 = new Pojo("five");
        var p3 = new Pojo("six");
        var l2 = List.of(p1, p2, p3);
        
        cList.addAll(l2);
        assertEquals(size + l2.size(), cList.size());
        l2.forEach(c -> assertTrue(cList.contains(c)));
    }

    @Test
    void addAt() {
        int size = cList.size();
        
        var p = new Pojo("four");
        var pC = new Point2D(500d, 500d);
        cList.addAt(p, pC);
        
        assertEquals(size + 1, cList.size());
        assertTrue(cList.contains(p));
        assertTrue(cList.contains(new CoordinatesElement<>(p, pC)));
        assertEquals(pC, cList.coordinatesOf(p).get().getXY());
    }

    @Test
    void testAddAt() {
        int size = cList.size();

        var p = new Pojo("four");
        var pC = new Point2D(500d, 500d);
        cList.addAt(new CoordinatesElement<>(p, pC));

        assertEquals(size + 1, cList.size());
        assertTrue(cList.contains(p));
        assertTrue(cList.contains(new CoordinatesElement<>(p, pC)));
        assertEquals(pC, cList.coordinatesOf(p).get().getXY());
    }

    @Test
    void addAllAt() {
        int size = cList.size();

        var p1 = new Pojo("four");
        var p2 = new Pojo("five");
        var p3 = new Pojo("six");
        var m = Map.of(
            p1, new Point2D(10, 10),
            p2, new Point2D(20, 20),
            p3, new Point2D(30, 30)
        );

        cList.addAllAt(m);
        assertEquals(size + m.size(), cList.size());
        m.forEach(
            (k, v) -> {
                assertTrue(cList.contains(k));
                assertEquals(v, cList.coordinatesOf(k).get().getXY());
            }
        );
    }

    @Test
    void testAddAllAt() {
        int size = cList.size();

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
        
        cList.addAllAt(l2);
        assertEquals(size + m.size(), cList.size());
        m.forEach(
            (k, v) -> {
                assertTrue(cList.contains(k));
                assertEquals(v, cList.coordinatesOf(k).get().getXY());
            }
        );
        l2.forEach(c -> assertTrue(cList.contains(c)));
    }



    @Test
    void remove() {
        int size = cList.size();
        
        var p = new Pojo("one");
        cList.remove(p);

        assertEquals(size - 1, cList.size());
        assertFalse(cList.contains(p));
    }

    @Test
    void removeAll() {
        int size = cList.size();

        var p1 = new Pojo("one");
        var p2 = new Pojo("two");
        var p3 = new Pojo("three");
        var l2 = List.of(p1, p2, p3);

        cList.removeAll(l2);
        assertEquals(size - l2.size(), cList.size());
        l2.forEach(c -> assertFalse(cList.contains(c)));
    }

    @Test
    void removeAt() {
        var p = cList.iterator().next();

        int size = cList.size();
        cList.removeAt(p);

        assertEquals(size - 1, cList.size());
        assertFalse(cList.contains(p));
    }

    @Test
    void removeAllAt() {
        var itr = cList.iterator();
        var l2 = List.of(itr.next(), itr.next());

        int size = cList.size();
        cList.removeAllAt(l2);

        assertEquals(size - l2.size(), cList.size());
        l2.forEach(c -> assertFalse(cList.contains(c)));
    }

    @Test
    void removeIf() {
        cList.removeIf(p -> p.name().equals("one"));
        assertFalse(cList.contains(new Pojo("one")));
    }

    @Test
    void removeCoordinatesIf() {
        var p = new Pojo("one");
        assertTrue(cList.contains(p));
        cList.removeCoordinatesIf(c -> c.getElement().equals(p));
        assertFalse(cList.contains(p));
    }

    @Test
    void clear() {
        assertFalse(cList.isEmpty());
        assertNotEquals(0, cList.size());
        assertTrue(cList.contains(new Pojo("one")));
        
        cList.clear();
        assertTrue(cList.isEmpty());
        assertEquals(0, cList.size());
        assertFalse(cList.contains(new Pojo("one")));
    }
    
}
