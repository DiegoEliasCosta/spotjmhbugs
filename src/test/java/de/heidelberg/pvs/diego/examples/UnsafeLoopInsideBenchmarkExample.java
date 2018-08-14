package de.heidelberg.pvs.diego.examples;

import java.util.concurrent.ConcurrentHashMap;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(value = Scope.Benchmark)
public class UnsafeLoopInsideBenchmarkExample {
	
private final ConcurrentHashMap<String, Long> map1 = new ConcurrentHashMap<>();
	
	@Benchmark
    public long iterMap1Element() {
        long total = 0;
        for (final Long value : map1.values()) {
            total += value;
        }
        return total;
    }


	@Benchmark
    public long iterMap2Element() {
        int total = 0;
        for (final Long value : map1.values()) {
            total = (int) (value + 10); 
        }
        return total;
    }
	
	@Benchmark
	public float anotherLoopBench() {
		float total = 0;
        for (int i = 0; i < 100; i ++) {
            total = i + 30; 
        }
        return total;
	}
	
	@Benchmark
	public short anotherLoopBenchShort() {
		short total = 0;
        for (int i = 0; i < 100; i ++) {
            total = (short) (i + 30); 
        }
        return total;
	}
	
	@Benchmark
	public int badBenchmarkButItShouldNotBeReported() {
        for (int i = 0; i < 100; i ++) {
        	int total = 0;
            total = i + 30; 
            if(total > 1000) {
            	return total;
            }
        }
        return 0;
	}
	
}
