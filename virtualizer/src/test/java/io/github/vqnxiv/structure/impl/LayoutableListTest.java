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


class LayoutableListTest {
    
    private record Pojo(String name) { }

    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three"));

    LayoutableList<Pojo> cList = new LayoutableList<>(l);
    
    @Test
    void canRepositionNormally() {
        var itr = cList.iterator();
        var p = itr.next();
        var p2 = itr.next();
        
        cList.repositionTo(p, new Point2D(500, 500));
        assertTrue(p.getX() == 500d && p.getY() == 500d);
        
        cList.repositionTo(p2, new Point2D(1_100, 1_100));
        assertTrue(p2.getX() == 1_100 && p2.getY() == 1_100);
        
        cList.repositionTo(p, new Point2D(500, 5_000));
        assertTrue(p.getX() == 500 && p.getY() == 5_000);
        
        cList.repositionTo(p, new Point2D(5_000, 5_000));
        assertTrue(p.getX() == 5_000 && p.getY() == 5_000);
    }
    
    @Test
    void repositionMultiple() {
        var itr = cList.iterator();
        var p = itr.next();
        var p2 = itr.next();
        
        cList.repositionAllTo(
            Map.of(p, new Point2D(500, 500), p2, new Point2D(1_100, 1_100))
        );

        assertTrue(p.getX() == 500d && p.getY() == 500d);
        assertTrue(p2.getX() == 1_100 && p2.getY() == 1_100);
    }
    
    
    @Test
    void canRepositionThroughItr() {
        var itr = cList.iterator();
        itr.next();
        assertDoesNotThrow(() -> itr.reposition(500d, 500d));
        assertDoesNotThrow(itr::next);
    }
    
    @Test
    void normalRepositionMakesItrThrow() {
        var itr = cList.iterator();
        
        var p = itr.next();
        cList.repositionTo(p, 500d, 500d);
        assertThrows(ConcurrentModificationException.class, itr::next);
    }

    @Test
    void iteratorGetsAllElementsAndEnds() {
        var l2 = new ArrayList<Pojo>();
        int i = 0;
        for(var e : cList) {
            l2.add(e.getElement());
        }
    
        assertEquals(l.size(), l2.size());
        assertTrue(l.containsAll(l2));
        assertTrue(l2.containsAll(l));
    }
    
    @Test
    void correctPropertyValues() {
        assertEquals(0d, cList.getMinimumWidth());
        assertEquals(0d, cList.getMinimumHeight());
        assertEquals(0d, cList.getMaximumWidth());
        assertEquals(0d, cList.getMaximumHeight());

        var itr = cList.iterator();
        var p = itr.next();
        var p2 = itr.next();
        var p3 = itr.next();
        
        cList.repositionTo(p, 500d, 500d);

        assertEquals(0d, cList.getMinimumWidth());
        assertEquals(0d, cList.getMinimumHeight());
        assertEquals(500d, cList.getMaximumWidth());
        assertEquals(500d, cList.getMaximumHeight());

        cList.repositionTo(p2, 510d, 510d);

        assertEquals(0d, cList.getMinimumWidth());
        assertEquals(0d, cList.getMinimumHeight());
        assertEquals(510d, cList.getMaximumWidth());
        assertEquals(510d, cList.getMaximumHeight());

        cList.repositionTo(p3, 200d, 200d);

        assertEquals(200d, cList.getMinimumWidth());
        assertEquals(200d, cList.getMinimumHeight());
        assertEquals(510d, cList.getMaximumWidth());
        assertEquals(510d, cList.getMaximumHeight());

        cList.repositionTo(p2, 400d, 400d);

        assertEquals(200d, cList.getMinimumWidth());
        assertEquals(200d, cList.getMinimumHeight());
        assertEquals(500d, cList.getMaximumWidth());
        assertEquals(500d, cList.getMaximumHeight());
        
        cList.repositionAllTo(
            Map.of(
                p, new Point2D(400d, 400d),
                p3, new Point2D(400d, 400d)
            )
        );

        assertEquals(400d, cList.getMinimumWidth());
        assertEquals(400d, cList.getMinimumHeight());
        assertEquals(400d, cList.getMaximumWidth());
        assertEquals(400d, cList.getMaximumHeight());
    }
    
    @Test
    void moveListenerTest() {
        AtomicReference<StructureChange.Move<?>> cRef = new AtomicReference<>();
        cList.addMoveListener(this, cRef::set);

        var itr = cList.iterator();
        var p = itr.next();
        var cp = new CoordinatesElement<>(p);
        var dst = new Point2D(673d, 2d);
        cList.repositionTo(p, dst);
        
        var m = cRef.get();
        assertEquals(cList, m.structure());
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
        
        cList.addMoveListener(this, csmr);

        var itr = cList.iterator();
        var p = itr.next();
        cList.repositionTo(p, 100d, 100d);
        
        assertNotNull(cRef.get());
        cList.removeMoveListener(this, csmr);
        cRef.set(null);
        cList.repositionTo(p, 200d, 200d);
        assertNull(cRef.get());
    }
}
