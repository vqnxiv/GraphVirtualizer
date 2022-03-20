package io.github.vqnxiv.misc;


import javafx.beans.property.SimpleDoubleProperty;


/**
 * A double property which has bounds on its value.
 * Attempting to set its value to outside of its bounds
 * will instead set it to the closest bound.
 * <p>
 * e.g if lower than the minimum, its value will be set 
 * to the minimum; if higher than the maximum,
 * it will be set to the max.
 */
public class BoundedDoubleProperty extends SimpleDoubleProperty {

    /**
     * Min value for this property.
     */
    private double min = Double.MIN_VALUE;

    /**
     * Max value for this property.
     */
    private double max = Double.MAX_VALUE;


    /**
     * Constructor.
     */
    public BoundedDoubleProperty() {
        super();
    }

    /**
     * Constructor.
     *
     * @param initialValue Initial value.
     */
    public BoundedDoubleProperty(double initialValue) {
        super(initialValue);
    }

    /**
     * Constructor.
     *
     * @param bean Bean.
     * @param name Name.
     */
    public BoundedDoubleProperty(Object bean, String name) {
        super(bean, name);
    }

    /**
     * Constructor.
     *
     * @param bean Bean.
     * @param name Name.
     * @param initialValue Initial value.
     */
    public BoundedDoubleProperty(Object bean, String name, double initialValue) {
        super(bean, name, initialValue);
    }

    /**
     * Constructor.
     *
     * @param min Minimal value.
     * @param max Maximal value.
     */
    public BoundedDoubleProperty(double min, double max) {
        super();
        
        if(max < min) {
            throw new IllegalArgumentException("Maximum bound less than minimum bouund");
        }

        // changes the value if its not within bounds
        setMin(min);
        setMax(max);
    }

    /**
     * Constructor.
     *
     * @param initialValue  Initial value.
     * @param min           Minimal value.
     * @param max           Maximal value.
     */
    public BoundedDoubleProperty(double initialValue, double min, double max) {
        super(initialValue);
        
        if(max < min) {
            throw new IllegalArgumentException("Maximum bound inferior to minimum bound");
        }

        setMin(min);
        setMax(max);
    }

    /**
     * Constructor.
     *
     * @param bean  Bean.
     * @param name  Name.
     * @param min   Minimal value.
     * @param max   Maximal value.
     */
    public BoundedDoubleProperty(Object bean, String name, double min, double max) {
        super(bean, name);
        setMin(min);
        setMax(max);
    }

    /**
     * Constructor.
     *
     * @param bean          Bean.
     * @param name          Name.
     * @param initialValue  Initial value.
     * @param min           Minimal value.
     * @param max           Maximal value.
     */
    public BoundedDoubleProperty(Object bean, String name, double initialValue, double min, double max) {
        super(bean, name, initialValue);
        setMin(min);
        setMax(max);
    }


    /**
     * Setter for this property's minimum.
     * If the current value is lower than the new minimum,
     * it will be set to the new minimum.
     *
     * @param min New minimum value.
     * @throws IllegalArgumentException if the new min is greater than the max.
     */
    public void setMin(double min) {
        if(min > this.max) {
            throw new IllegalArgumentException("Minimum bound greater than maximum bouund");
        }
        
        this.min = min;
        if(get() < min) {
            set(min);
        }
    }

    /**
     * Setter for this property's maximum.
     * If the current value is greater than the new maximum,
     * it will be set to the new maximum. 
     *
     * @param max New maximum value.
     * @throws IllegalArgumentException if the new max is less than the min.
     */
    public void setMax(double max) {
        if(max < this.min) {
            throw new IllegalArgumentException("Maximum bound less than minimum bouund");
        }
        
        this.max = max;
        if(get() > max) {
            set(max);
        }
    }

    /**
     * Getter for this property's minimum. 
     *
     * @return This property's minimum value.
     */
    public double getMin() {
        return min;
    }

    /**
     * Getter for this property's maximum. 
     *
     * @return This property's maximum value.
     */
    public double getMax() {
        return max;
    }


    /**
     * Setter for this property's value.
     *
     * @param v New value.
     */
    @Override
    public void set(double v) {
        // avoid setting if same value
        if(v == get()) {
            return;
        }

        // avoid setting if already at min
        if(v <= min) {
            if(get() != min) {
                super.set(min);
            }
            return;
        }

        // avoid setting if already at max
        if(v >= max) {
            if(get() != max) {
                super.set(max);
            }
            return;
        }

        super.set(v);
    }

    /**
     * Setter for this property's value.
     *
     * @param number New value.
     */
    @Override
    public void setValue(Number number) {
        set(number.doubleValue());
    }
}
