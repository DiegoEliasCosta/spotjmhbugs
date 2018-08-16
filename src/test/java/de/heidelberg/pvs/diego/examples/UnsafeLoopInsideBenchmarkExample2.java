package de.heidelberg.pvs.diego.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(value = Scope.Benchmark)
public class UnsafeLoopInsideBenchmarkExample2 {

	private final ConcurrentHashMap<String, Long> map1 = new ConcurrentHashMap<>();

	private Random rand;
	
	@Benchmark
	public void readSkipping(Blackhole bh) {
		List<Float> columnarFloats = new ArrayList<>();
		int count = columnarFloats.size();
		Float sum = 0F;
		for (int i = 0; i < count; i += rand.nextInt(2000)) {
			sum += columnarFloats.get(i);
		}
		bh.consume(sum);
	}
}
