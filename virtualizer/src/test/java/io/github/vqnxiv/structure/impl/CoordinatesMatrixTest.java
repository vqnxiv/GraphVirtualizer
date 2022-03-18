package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.RandomLayout;
import io.github.vqnxiv.structure.CoordinatesElement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


class CoordinatesMatrixTest {
    
    private record Pojo(String name) { }
    
    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three")); 
    
    CoordinatesMatrix<Pojo> matrix = new CoordinatesMatrix<>(l);
    
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
    void hasNext() {
        var itr = matrix.iterator();
        
        for(int i = 0; i < l.size(); i++) {
            assertTrue(itr.hasNext());
            assertDoesNotThrow(itr::next);
        }
        
        assertFalse(itr.hasNext());
        assertThrows(NoSuchElementException.class, itr::next);
    }

    @Test
    void initialLayout() {
        matrix = new CoordinatesMatrix<>(l, RandomLayout::new);
        for(var e : matrix) {
            System.out.println(e);
        }
    }

    @Test
    void containsTest() {
        matrix = new CoordinatesMatrix<>(l, RandomLayout::new);
        var c = matrix.iterator().next();
        var c2 = new CoordinatesElement<>(c);
        c2.setX(c.getX() / 2);

        assertTrue(matrix.contains(c));
        assertTrue(matrix.contains(c2.getElement()));
        assertFalse(matrix.contains(c2));
    }
    
}
