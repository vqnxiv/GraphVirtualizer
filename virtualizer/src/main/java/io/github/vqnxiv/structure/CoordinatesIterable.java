package io.github.vqnxiv.structure;


/**
 * Extension of the {@link Iterable} interface which
 * ensures that {@link #iterator()} returns a {@link CoordinatesIterator}.
 * 
 * @param <E> Type of elements.
 */
public interface CoordinatesIterable<E> extends Iterable<E> {
    
    /**
     * {@inheritDoc}
     *
     * @return an Iterator.
     */
    @Override
    CoordinatesIterator<E> iterator();
}
