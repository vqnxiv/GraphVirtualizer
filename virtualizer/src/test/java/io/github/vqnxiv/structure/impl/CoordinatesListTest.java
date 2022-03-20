package io.github.vqnxiv.structure.impl;


import io.github.vqnxiv.layout.AbstractLayout;
import io.github.vqnxiv.layout.RandomLayout;
import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class CoordinatesListTest {

    private record Pojo(String name) { }

    List<Pojo> l = List.of(new Pojo("one"), new Pojo("two"), new Pojo("three"));

    CoordinatesList<Pojo> cList;
    
    private class PojoLayout extends AbstractLayout<Pojo> {

        /**
         * Constructor.
         *
         * @param s Structure.
         */
        protected PojoLayout(LayoutableStructure<Pojo> s) {
            super(s);
        }

        /**
         * Applies this layout to its structure.
         */
        @Override
        public void apply() {
            var m = new HashMap<CoordinatesElement<Pojo>, Point2D>();
            for(var c : getStructure()) {
                m.put(c, new Point2D(10d, 10d));
            }
            
            getStructure().repositionAllTo(m);
        }
    }
    
    
    @Test
    void initialLayout() {
        cList = new CoordinatesList<>(l, PojoLayout::new);

        cList.forEach(System.out::println);
        
        assertEquals(10d, cList.getMinimumWidth());
        assertEquals(10d, cList.getMinimumHeight());
        assertEquals(10d, cList.getMaximumWidth());
        assertEquals(10d, cList.getMaximumHeight());
        
        cList.forEach(
            c -> assertEquals(c.getXY(), new Point2D(10d, 10d))
        );
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
