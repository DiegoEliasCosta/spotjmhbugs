package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

@Fork(value = 0)
public class NotForkedBenchmarkExample {
	
	@Benchmark
	@Fork(2)
	public void forkedBenchmark() {

	}
	
	@Benchmark
	@Fork(3)
	public void forkedBenchmark2() {

	}
	
	@Benchmark
	@Fork(0)
	public void unForkedBenchmark() {

	}

}
