package io.github.vqnxiv.node;


import java.util.*;
import java.util.function.Function;


/**
 * This is similar to {@link SetNodePool}, except for
 * the difference that unused nodes are cleared after
 * some time ({@code aliveTimeMS)} if the pool contains 
 * more nodes than its minimum size ({@code coreSize}).
 * 
 * @param <D> Type of decorator.
 *
 * @see DecoratedNode
 * @see SetNodePool
 */
public class TimedNodePool<D> implements DecoratedNodePool<D> {

    /**
     * The default life duration for an unused node, in ms.
     */
    public static final long DEFAULT_KEEP_ALIVE_TIME_MS = 20_000L;

    /**
     * The default minimum pool size. It counts both used and unsed nodes.
     */
    public static final int DEFAULT_CORE_SIZE = 50;


    /**
     * Private class which holds a decoratedNode and it's 'last touched' timestamp.
     */
    private final class TimestampedDecorated {
        
        /**
         * The decoratedNode.
         */
        private final DecoratedNode<D> decoratedNode;

        /**
         * The timestamp.
         */
        private long timestamp;

        /**
         * Constructor.
         * 
         * @param d DecoratedNode.
         * @param t Timestamp.
         */
        private TimestampedDecorated(DecoratedNode<D> d, long t) {
            Objects.requireNonNull(d);
            decoratedNode = d;
            timestamp = t;
        }

        /**
         * Getter for the decoratedNode.
         * 
         * @return The decoratedNode.
         */
        private DecoratedNode<D> getDecorated() {
            return decoratedNode;
        }

        /**
         * Getter for the timestamp.
         * 
         * @return The timestamp.
         */
        private long getTimestamp() {
            return timestamp;
        }

        /**
         * Setter for the timestamp.
         * 
         * @param timestamp New timestamp value.
         */
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            
            if(o instanceof TimedNodePool<?>.TimestampedDecorated td) {
                return decoratedNode.equals(td.getDecorated());
            }
            
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return decoratedNode.hashCode();
        }
    }
    
    
    /**
     * Nodes which aren't in use and can be decoratedNode.
     * <p>
     * This is a deque because LIFO (prioritizing last used nodes over older ones).
     */
    private final Deque<TimestampedDecorated> freeNodes;
    
    /**
     * DecoratedNode nodes which have been retrieved from a 
     * {@link #get(Object)} or {@link #getAll(Collection)}
     * call.
     */
    private final Map<D, TimestampedDecorated> usedNodes;

    /**
     * Factory which creates instances of DecoratedNode.
     */
    private final Function<D, DecoratedNode<D>> factory;

    /**
     * How long an used node should be kept.
     */
    private final long aliveTimeMS;

    /**
     * Minimum pool size. Counts both unused and used nodes.
     */
    private final int coreSize;

    /**
     * Whether the pool is currently processing a batch 
     * (e.g {@link #getAll(Collection)} or {@link #releaseAll(Collection)}).
     * <p>
     * Useful for two things: <br>
     * - making sure we don't remove unused nodes while retrieving multiple
     * (which coumd force to recreate a node when we just released some) <br>
     * - sets the same timestamp for a complete batch so they can be released
     * at once
     */
    private boolean isInBatch = false;

    /**
     * Current timestamp.
     */
    private long now;


    /**
     * Constructor with default keep alive time and core size.
     * 
     * @param factory DecoratedNode factory.
     */
    public TimedNodePool(Function<D, DecoratedNode<D>> factory) {
        this(factory, DEFAULT_KEEP_ALIVE_TIME_MS, DEFAULT_CORE_SIZE);
    }

    /**
     * Constructor.
     * 
     * @param factory       DecoratedNode factory.
     * @param keepAlivetime Keep alive time.
     * @param coreSize      Core size.
     */
    public TimedNodePool(Function<D, DecoratedNode<D>> factory, long keepAlivetime, int coreSize) {
        Objects.requireNonNull(factory);

        this.factory = factory;
        this.aliveTimeMS = keepAlivetime;
        this.coreSize = coreSize;
        freeNodes = new ArrayDeque<>();
        usedNodes = new HashMap<>();
    }


    /**
     * Updates the timestamp if a batch isn't being processed.
     * 
     * @param force Force an update regardless of a possible batch.
     */
    private void now(boolean force) {
        if(!isInBatch || force) {
            now = System.currentTimeMillis();
        }
    }

    /**
     * Checks free nodes and clears out old ones if needed.
     */
    // public?
    private void checkForRemoval() {
        if(isInBatch) {
            return;
        }
        
        now(true);
        synchronized(freeNodes) {
            freeNodes.removeIf(
                e -> freeNodes.size() + usedNodes.size() > coreSize && now - e.getTimestamp() > aliveTimeMS
            );
        }
    }
    
    
    /**
     * {@inheritDoc}
     * <p>
     * Checks for removal after retrieving the node.
     *
     * @param d Decorator.
     * @return An instance of DecoratedNode.
     */
    @Override
    public Optional<DecoratedNode<D>> get(D d) {
        if(usedNodes.containsKey(d)) {
            return Optional.empty();
        }
        
        TimestampedDecorated tdec;

        if(freeNodes.isEmpty()) {
            now(false);
            tdec = new TimestampedDecorated(factory.apply(d), now);
        }
        else {
            tdec = freeNodes.pollLast();
            tdec.getDecorated().setDecorator(d);
        }

        usedNodes.put(d, tdec);
        checkForRemoval();
        return Optional.of(tdec.getDecorated());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks for removal after retrieving the nodes.
     * 
     * @param ds Decorators.
     * @return Multiple instances of DecoratedNode.
     */
    @Override
    public Collection<DecoratedNode<D>> getAll(Collection<D> ds) {
        isInBatch = true;
        List<DecoratedNode<D>> l = new ArrayList<>();
        
        now(true);
        for(D d : ds) {
            get(d).ifPresent(l::add);
        }
        
        isInBatch = false;
        checkForRemoval();
        return l;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks for removal before returning the node to the pool.
     *
     * @param decoratedNode The decoratedNode to return.
     */
    @Override
    public void release(DecoratedNode<D> decoratedNode) {
        checkForRemoval();
        
        TimestampedDecorated t;
        if((t = usedNodes.remove(decoratedNode.getDecorator().get())) != null) {
            t.getDecorated().clearDecoration();
            now(false);
            t.setTimestamp(now);
            freeNodes.addLast(t);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks for removal before returning the nodes to the pool.
     *
     * @param decoratedNodes The decoratedNodes to return.
     */
    @Override
    public void releaseAll(Collection<DecoratedNode<D>> decoratedNodes) {
        checkForRemoval();
        
        isInBatch = true;
        now(true);
        for(var dec : decoratedNodes) {
            release(dec);
        }
        isInBatch = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        freeNodes.clear();
    }

    /**
     * {@inheritDoc}
     *
     * @return The amount of used nodes.
     */
    @Override
    public int used() {
        return usedNodes.size();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks for removal before returning the size.  
     *
     * @return The amount of available nodes in the pool.
     */
    @Override
    public int capacity() {
        checkForRemoval();
        return freeNodes.size();
    }
}
