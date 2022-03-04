package io.github.vqnxiv.structure;


/**
 * Marker interface which means that the implementing class supports 
 * 'localized' lookups, i.e does not iterate over all the elements
 * it contains any time a request is made.
 * <p>
 * Basically if splitting into smaller disjoint lookups is more
 * efficient than doing one single call with a larger area. <br>
 * (E.g this is not the case with {@link io.github.vqnxiv.structure.impl.CoordinatesList} 
 * when it reaches a large number of elements as it would iterate 
 * over all its elements multiple times.)
 */
public interface LocalizedStructure<E> extends CoordinatesStructure<E> { }
