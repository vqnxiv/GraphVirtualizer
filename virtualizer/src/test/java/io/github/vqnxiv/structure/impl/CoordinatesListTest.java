package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.RandomLayout;
import io.github.vqnxiv.structure.CoordinatesElement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CoordinatesListTest {

    private record Pojo(String name) { }

    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three"));

    CoordinatesList<Pojo> cList;
    
    
    @Test
    void initialLayout() {
        cList = new CoordinatesList<>(l, RandomLayout::new);
        for(var e : cList) {
            System.out.println(e);
        }
    }
    
    @Test
    void containsTest() {
        cList = new CoordinatesList<>(l, RandomLayout::new);
        var c = cList.iterator().next();
        var c2 = new CoordinatesElement<>(c);
        c2.setX(c.getX() / 2);
   
        assertTrue(cList.contains(c));
        assertTrue(cList.contains(c2.getElement()));
        assertFalse(cList.contains(c2));
    }
}
