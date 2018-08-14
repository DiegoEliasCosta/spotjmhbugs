package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;

public class UnsinkedVariableExample {

	@Benchmark
	public double benchmarkMethod1() {
		double x = Math.PI;
		double y = 12;
		return y;
	}
	
}
