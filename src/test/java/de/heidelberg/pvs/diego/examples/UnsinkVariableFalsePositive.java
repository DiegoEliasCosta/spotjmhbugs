package de.heidelberg.pvs.diego.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class UnsinkVariableFalsePositive {

	private int size;
	private float loadFactor;
	private String[] elements;

	@Benchmark
	public Map<String, String> gsc() {
		int localSize = this.size;
		float localLoadFactor = this.loadFactor;
		String[] localElements = this.elements;
		/**
		 * @see UnifiedMap#DEFAULT_INITIAL_CAPACITY
		 */
		Map<String, String> gsc = new HashMap<>(localSize, localLoadFactor);

		for (int i = 0; i < localSize; i++) {
			gsc.put(localElements[i], "dummy");
		}
		return gsc;
	}
	
	@Benchmark
	public Map<String, String> gsc2() {
		int localSize = 100;
		String[] localElements = this.elements;
		/**
		 * @see UnifiedMap#DEFAULT_INITIAL_CAPACITY
		 */
		Map<String, String> gsc = new HashMap<>();

		for (int i = 0; i < localSize; i++) {
			gsc.put(localElements[i], "dummy");
		}
		return gsc;
	}
}
