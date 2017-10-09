package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;

public class IgnoreMethodReturnExample {

	@Benchmark
	public double benchmarkMethod1() {
		double x = Math.PI;
		return x;
	}
	
	
	@Benchmark
	public double benchmarkMethod2() {
		Math.log(Math.PI); // WRONG 1 (+1)
		double x = Math.PI;
		return x;
	}
	
	@Benchmark
	public double benchmarkMethod3() {
		return Math.PI;
	}
	
	public double noBenchmarkMethod() {
		benchmarkMethod1(); // NOT A BENCHMARK 
		double x = benchmarkMethod2();
		return x;
	}
	
}
