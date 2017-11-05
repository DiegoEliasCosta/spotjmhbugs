package de.heidelberg.pvs.diego.examples;

import java.util.concurrent.ConcurrentHashMap;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * 
 * Example taken from Log4J CollectionsBenchmark
 * 
 * @author diego.costa
 *
 */
public class LoopInsideBenchmark {
	
    private final ConcurrentHashMap<String, Long> map1 = new ConcurrentHashMap<>();
	
	@Benchmark
    public long iterMap1Element() {
        long total = 0;
        for (final Long value : map1.values()) {
            total += value;
        }
        return total;
    }

}
