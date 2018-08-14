package de.heidelberg.pvs.diego.examples;

import java.util.concurrent.ConcurrentHashMap;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * 
 * Example taken from Log4J CollectionsBenchmark
 * 
 * @author diego.costa
 *
 */
@State(value = Scope.Benchmark)
public class LoopInsideBenchmarkExample {
	
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
