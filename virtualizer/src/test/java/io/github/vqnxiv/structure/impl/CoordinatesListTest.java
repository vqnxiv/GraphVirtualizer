package io.github.vqnxiv.structure.impl;

import io.github.vqnxiv.layout.RandomLayout;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
