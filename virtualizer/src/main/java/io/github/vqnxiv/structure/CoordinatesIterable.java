package io.github.vqnxiv.structure;


/**
 * Extension of the {@link Iterable} interface which
 * ensures that {@link #iterator()} returns a {@link CoordinatesIterator}.
 * 
 * @param <E> Type of elements.
 */
public interface CoordinatesIterable<E> extends Iterable<E> {
    
    /**
     * Returns a iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    // todo: rename to cIterator so it doesnt clash? 
    //  + allows to only return one if Layoutable or smth <= no
    @Override
    CoordinatesIterator<E> iterator();
}
