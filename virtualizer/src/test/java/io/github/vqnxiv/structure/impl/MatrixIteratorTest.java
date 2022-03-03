package io.github.vqnxiv.structure.impl;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


class MatrixIteratorTest {
    
    private record Pojo(String name) { }
    
    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three")); 
    
    CoordinatesMatrix<Pojo> matrix = new CoordinatesMatrix<>(l);
    
    @Test
    void iteratorGetsAllElements() {
        var l2 = new ArrayList<Pojo>();
        for(var e : matrix) {
            l2.add(e.getElement());
        }
        
        assertEquals(l, l2);
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
    
}
