package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;

public class BenchmarkTestClass {
	
	@Benchmark
	public String benchmarkToString() {
		return super.toString();
	}
	
	@Benchmark
	public void voidBenchmark() {
		
	}
	
	

}
