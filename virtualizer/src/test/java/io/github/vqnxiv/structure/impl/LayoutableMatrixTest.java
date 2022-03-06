package io.github.vqnxiv.structure.impl;


import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LayoutableMatrixTest {
    
    private record Pojo(String name) { }
    
    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three")); 
    
    LayoutableMatrix<Pojo> matrix = new LayoutableMatrix<>(l);
    
    
    @Test
    void canRepositionNormally() {
        var itr = matrix.iterator();
        System.out.println(matrix);
        
        // only getting some elements
        var p = itr.next();
        var p2 = itr.next();
        
        matrix.repositionTo(p, new Point2D(500, 500));
        System.out.println(matrix);
        matrix.repositionTo(p2, new Point2D(1_100, 1_100));
        System.out.println(matrix);
        matrix.repositionTo(p, new Point2D(500, 5_000));
        System.out.println(matrix);
        matrix.repositionTo(p, new Point2D(5_000, 5_000));
        System.out.println(matrix);
        System.out.println(matrix.maximumWidth().get() + " " + matrix.maximumHeight().get());
    }
    
    @Test
    void canRepositionThroughItr() {
        var itr = matrix.iterator();
        System.out.println(matrix);
        itr.next();
        assertDoesNotThrow(() -> itr.reposition(500d, 500d));
        System.out.println(matrix);
        assertDoesNotThrow(itr::next);
    }
    
    @Test
    void normalRepositionMakesItrThrow() {
        var itr = matrix.iterator();
        
        var p = itr.next();
        matrix.move(p, 500d, 500d);
        assertThrows(ConcurrentModificationException.class, itr::next);
    }

    @Test
    void iteratorGetsAllElementsAndEnds() {
        var l2 = new ArrayList<Pojo>();
        int i = 0;
        for(var e : matrix) {
            l2.add(e.getElement());
            if(i++ == 5) {
                break;
            }
        }

        assertTrue(l.containsAll(l2));
        assertTrue(l2.containsAll(l));
    }

}
