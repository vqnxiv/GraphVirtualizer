package io.github.vqnxiv.structure.impl;


import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;


class LayoutableMatrixTest {
    
    private record Pojo(String name) { }
    
    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three")); 
    
    LayoutableMatrix<Pojo> matrix = new LayoutableMatrix<>(l);
    
    
    @Test
    void test() {
        var itr = matrix.iterator();
        var p = itr.next();
        var p2 = itr.next();
        
        matrix.repositionTo(p, new Point2D(500, 500));
        matrix.repositionTo(p2, new Point2D(1_100, 1_100));
        matrix.repositionTo(p, new Point2D(500, 5_000));
        matrix.repositionTo(p, new Point2D(5_000, 5_000));
    }
    
}
