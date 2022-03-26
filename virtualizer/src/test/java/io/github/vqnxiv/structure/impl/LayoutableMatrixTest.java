package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.StructureChange;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LayoutableMatrixTest {
    
    private record Pojo(String name) { }
    
    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three")); 
    
    LayoutableMatrix<Pojo> matrix = new LayoutableMatrix<>(l);
    
    
    @Test
    void canRepositionNormally() {
        var itr = matrix.iterator();
        // System.out.println(matrix);
        
        // only getting some elements
        var p = itr.next();
        var p2 = itr.next();
        
        matrix.repositionTo(p, new Point2D(500, 500));
        // System.out.println(matrix);
        assertTrue(p.getX() == 500d && p.getY() == 500d);
        
        matrix.repositionTo(p2, new Point2D(1_100, 1_100));
        // System.out.println(matrix);
        assertTrue(p2.getX() == 1_100 && p2.getY() == 1_100);
        
        matrix.repositionTo(p, new Point2D(500, 5_000));
        // System.out.println(matrix);
        assertTrue(p.getX() == 500 && p.getY() == 5_000);
        
        matrix.repositionTo(p, new Point2D(5_000, 5_000));
        // System.out.println(matrix);
        assertTrue(p.getX() == 5_000 && p.getY() == 5_000);
    }
    
    @Test
    void repositionMultiple() {
        var itr = matrix.iterator();
        var p = itr.next();
        var p2 = itr.next();
        
        matrix.repositionAllTo(
            Map.of(p, new Point2D(500, 500), p2, new Point2D(1_100, 1_100))
        );

        assertTrue(p.getX() == 500d && p.getY() == 500d);
        assertTrue(p2.getX() == 1_100 && p2.getY() == 1_100);
    }
    
    
    @Test
    void canRepositionThroughItr() {
        var itr = matrix.iterator();
        // System.out.println(matrix);
        var p = itr.next();
        assertDoesNotThrow(() -> itr.reposition(500d, 500d));
        assertTrue(p.getX() == 500d && p.getY() == 500d);
        assertDoesNotThrow(() -> itr.reposition(600d, 500d));
        assertTrue(p.getX() == 600d && p.getY() == 500d);
        // System.out.println(matrix);
        assertDoesNotThrow(itr::next);
    }
    
    @Test
    void normalRepositionMakesItrThrow() {
        var itr = matrix.iterator();
        
        var p = itr.next();
        matrix.repositionTo(p, 500d, 500d);
        assertThrows(ConcurrentModificationException.class, itr::next);
    }

    @Test
    void iteratorGetsAllElementsAndEnds() {
        var l2 = new ArrayList<Pojo>();

        for(var e : matrix) {
            l2.add(e.getElement());
        }
    
        assertEquals(l.size(), l2.size());
        assertTrue(l.containsAll(l2));
        assertTrue(l2.containsAll(l));
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

        matrix.repositionTo(p, 500d, 500d);

        assertEquals(0d, matrix.getMinimumWidth());
        assertEquals(0d, matrix.getMinimumHeight());
        assertEquals(500d, matrix.getMaximumWidth());
        assertEquals(500d, matrix.getMaximumHeight());

        matrix.repositionTo(p2, 510d, 510d);

        assertEquals(0d, matrix.getMinimumWidth());
        assertEquals(0d, matrix.getMinimumHeight());
        assertEquals(510d, matrix.getMaximumWidth());
        assertEquals(510d, matrix.getMaximumHeight());

        matrix.repositionTo(p3, 200d, 200d);

        assertEquals(200d, matrix.getMinimumWidth());
        assertEquals(200d, matrix.getMinimumHeight());
        assertEquals(510d, matrix.getMaximumWidth());
        assertEquals(510d, matrix.getMaximumHeight());

        matrix.repositionTo(p2, 400d, 400d);

        assertEquals(200d, matrix.getMinimumWidth());
        assertEquals(200d, matrix.getMinimumHeight());
        assertEquals(500d, matrix.getMaximumWidth());
        assertEquals(500d, matrix.getMaximumHeight());

        matrix.repositionAllTo(
            Map.of(
                p, new Point2D(400d, 400d),
                p3, new Point2D(400d, 400d)
            )
        );

        assertEquals(400d, matrix.getMinimumWidth());
        assertEquals(400d, matrix.getMinimumHeight());
        assertEquals(400d, matrix.getMaximumWidth());
        assertEquals(400d, matrix.getMaximumHeight());
    }

    @Test
    void moveListenerTest() {
        AtomicReference<StructureChange.Move<?>> cRef = new AtomicReference<>();
        matrix.addMoveListener(this, cRef::set);

        var itr = matrix.iterator();
        var p = itr.next();
        var cp = new CoordinatesElement<>(p);
        var dst = new Point2D(673d, 2d);
        matrix.repositionTo(p, dst);
        
        var m = cRef.get();
        assertEquals(matrix, m.structure());
        assertEquals(1, m.elements().size());
        assertTrue(m.elements().containsKey(cp));
        assertEquals(dst, m.elements().get(cp));
        assertEquals(cp.getXY(), m.topLeft());
        assertEquals(dst, m.bottomRight());
    }
    
    @Test
    void removeListenerTest() {
        AtomicReference<StructureChange.Move<?>> cRef = new AtomicReference<>(null);
        Consumer<StructureChange.Move<?>> csmr = cRef::set;
        
        matrix.addMoveListener(this, csmr);

        var itr = matrix.iterator();
        var p = itr.next();
        matrix.repositionTo(p, 100d, 100d);
        
        assertNotNull(cRef.get());
        matrix.removeMoveListener(this, csmr);
        cRef.set(null);
        matrix.repositionTo(p, 200d, 200d);
        assertNull(cRef.get());
    }
}
