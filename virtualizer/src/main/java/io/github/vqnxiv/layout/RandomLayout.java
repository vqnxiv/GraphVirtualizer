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
     * @param minWidth  Initial minimum allowed width.
     * @param minHeight Initial minimum allowed height.
     * @param maxWidth  Initial maximum allowed width.
     * @param maxHeight Initial maximum allowed height.
     */
    public RandomLayout(LayoutableStructure<E> s, 
                        double minWidth, double minHeight, 
                        double maxWidth, double maxHeight) {
        super(s, minWidth, minHeight, maxWidth, maxHeight);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        double minW = getMaxAllowedWidth();
        double minH = getMaxAllowedHeight();
        double maxW = getMinAllowedWidth();
        double maxH = getMinAllowedHeight();
        
        double x, y;

        Map<CoordinatesElement<E>, Point2D> m = new HashMap<>();

        for(var e : structure) {
            x = ThreadLocalRandom.current().nextDouble(getMinAllowedWidth(), getMaxAllowedWidth());
            y = ThreadLocalRandom.current().nextDouble(getMinAllowedHeight(), getMaxAllowedHeight());

            minW = Math.min(x, minW);
            minH = Math.min(y, minH);
            maxW = Math.max(x, maxW);
            maxH = Math.max(y, maxH);

            m.put(e, new Point2D(x, y));
        }

        setUsedDimensions(minW, minH, maxW, maxH);
        structure.repositionAllTo(m);
    }
}
