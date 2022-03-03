package io.github.vqnxiv.misc;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class BoundedDoublePropertyTest {


    @Test
    @DisplayName("Initial min value on creation")
    void getInitialMin() {
        BoundedDoubleProperty defaultBDP = new BoundedDoubleProperty();
        assertEquals(Double.MIN_VALUE, defaultBDP.getMin());

        BoundedDoubleProperty bdp = new BoundedDoubleProperty(0, 10);
        assertEquals(0, bdp.getMin());
    }

    @Test
    @DisplayName("Initial max value on creation")
    void getInitialMax() {
        BoundedDoubleProperty defaultBDP = new BoundedDoubleProperty();
        assertEquals(Double.MAX_VALUE, defaultBDP.getMax());

        BoundedDoubleProperty bdp = new BoundedDoubleProperty(0, 10);
        assertEquals(10, bdp.getMax());
    }
    
    @Test
    @DisplayName("Initial value on creation")
    void getInitialValue() {
        BoundedDoubleProperty defaultBDP = new BoundedDoubleProperty();
        assertEquals(0, defaultBDP.get());
        
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(5);
        assertEquals(5, bdp.get());
        
        BoundedDoubleProperty bdpAuto = new BoundedDoubleProperty(2, 10);
        assertEquals(2, bdpAuto.get());

        BoundedDoubleProperty bdpAuto2 = new BoundedDoubleProperty(-5, -1);
        assertEquals(-1, bdpAuto2.get());
    }
    
    @Test
    @DisplayName("Constructor throws on max < min")
    void throwingConstructor() {
        assertThrows(IllegalArgumentException.class,
            () -> new BoundedDoubleProperty(10, 9)
        );
    }
    
    @Test
    @DisplayName("Update min to less than or equal to the value")
    void setMinLeqThanValue() {
        double bdpValue = 5d;
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(bdpValue, bdpValue / 2, bdpValue * 2);
        assertEquals(bdpValue / 2, bdp.getMin());

        bdp.setMin(bdp.get() - 1);
        assertEquals(bdpValue, bdp.get());
        assertEquals(bdpValue-1, bdp.getMin());

        bdp.setMin(bdp.get());
        assertEquals(bdpValue, bdp.get());
        assertEquals(bdpValue, bdp.getMin());
    }

    @Test
    @DisplayName("Update max to greater than or equal to the value")
    void setMaxGeqThanValue() {
        double bdpValue = 5d;
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(bdpValue, bdpValue / 2, bdpValue * 2);
        assertEquals(bdpValue * 2, bdp.getMax());

        bdp.setMax(bdp.get() + 1);
        assertEquals(bdpValue, bdp.get());
        assertEquals(bdpValue+1, bdp.getMax());

        bdp.setMax(bdp.get());
        assertEquals(bdpValue, bdp.get());
        assertEquals(bdpValue, bdp.getMax());
    }
    
    @Test
    @DisplayName("Update min to greater than the value")
    void setMinGreaterThanValue() {
        double bdpValue = 5d;
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(bdpValue, bdpValue / 2, bdpValue * 2);
        assertEquals(bdpValue / 2, bdp.getMin());

        bdp.setMin(bdp.get() + 1);
        assertNotEquals(bdpValue, bdp.get());
        assertEquals(bdpValue+1, bdp.getMin());
        assertEquals(bdp.getMin(), bdp.get());
    }

    @Test
    @DisplayName("Update max to less than the value")
    void setMaxLessThanValue() {
        double bdpValue = 5d;
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(bdpValue, bdpValue / 2, bdpValue * 2);
        assertEquals(bdpValue * 2, bdp.getMax());

        bdp.setMax(bdp.get() - 1);
        assertNotEquals(bdpValue, bdp.get());
        assertEquals(bdpValue-1, bdp.getMax());
        assertEquals(bdp.getMax(), bdp.get());
    }
    
    @Test
    @DisplayName("Throwing on min > max")
    void setMinGreaterThanMax() {
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(0, 10);
        assertThrows(IllegalArgumentException.class,
            () -> bdp.setMin(bdp.getMax() + 1)
        );
    }

    @Test
    @DisplayName("Throwing on max < min")
    void setMaxLowerThanMin() {
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(0, 10);
        assertThrows(IllegalArgumentException.class,
            () -> bdp.setMax(bdp.getMin() - 1)
        );
    }

    @Test
    @DisplayName("Update value between bounds")
    void setBetweenBounds() {
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(0, 10);
        assertNotEquals(5, bdp.get());
        bdp.set(5);
        assertEquals(5, bdp.get());
    }

    @Test
    @DisplayName("Update value between bounds")
    void setValueBetweenBounds() {
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(0, 10);
        assertNotEquals(5, bdp.getValue());
        bdp.setValue(5);
        assertEquals(5, bdp.getValue());
    }
    
    @Test
    @DisplayName("Update value above max")
    void setGreaterThanMax() {
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(0, 10);
        assertNotEquals(bdp.getMax(), bdp.get());
        
        bdp.set(bdp.getMax() + 1);
        assertEquals(bdp.getMax(), bdp.get());
    }

    @Test
    @DisplayName("Update value lower than min")
    void setLowerThanMin() {
        BoundedDoubleProperty bdp = new BoundedDoubleProperty(-10, 1);
        assertNotEquals(bdp.getMin(), bdp.get());

        bdp.set(bdp.getMin() - 1);
        assertEquals(bdp.getMin(), bdp.get());
    }
}