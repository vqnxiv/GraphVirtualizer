package io.github.vqnxiv.layout;


import io.github.vqnxiv.structure.CoordinatesElement;
import io.github.vqnxiv.structure.LayoutableStructure;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Simple implementation of {@link ConstrainedLayout}
 * which randomly positions the elements its structure 
 * within its bounds.
 * 
 * @param <E> Type of elements of the {@link LayoutableStructure}.
 *           
 * @see ConstrainedLayout
 */
public class RandomLayout<E> extends AbstractConstrainedLayout<E> {

    /**
     * Constructor.
     * 
     * @param s Structure.
     */
    public RandomLayout(LayoutableStructure<E> s) {
        super(s);
    }

    /**
     * Constructor.
     *
     * @param s Structure.
     * @param maxWidth  Initial maximum allowed width.
     * @param maxHeight Initial maximum allowed height.
     */
    public RandomLayout(LayoutableStructure<E> s, double maxWidth, double maxHeight) {
        super(s, maxWidth, maxHeight);
    }

    /**
     * {@inheritDoc}
     *
     * @param maxWidth  Maximum allowed width.
     * @param maxHeight Maximum allowed height.
     */
    @Override
    public void applyWithinBounds(double maxWidth, double maxHeight) {
        maxAllowedWidth.set(maxWidth);
        maxAllowedHeight.set(maxHeight);
        apply();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        double maxW = -1d;
        double maxH = -1d;
        
        double x, y;

        Map<CoordinatesElement<E>, Point2D> m = new HashMap<>();
        
        for(var e : structure) {
            x = ThreadLocalRandom.current().nextDouble(getMaxAllowedWidth());
            y = ThreadLocalRandom.current().nextDouble(getMaxAllowedHeight());

            maxW = Math.max(x, maxW);
            maxH = Math.max(y, maxH);

            m.put(e, new Point2D(x, y));
        }

        maxUsedWidth.set(maxW);
        maxUsedHeight.set(maxH);
        structure.repositionAllTo(m);
    }
}
