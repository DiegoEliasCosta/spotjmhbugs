package de.heidelberg.pvs.diego.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(value = Scope.Benchmark)
public class UnsinkedVariablesFalsePositiveExamples {

	private int cardinality = 0;

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void matchEverythingAlphaNumeric(Blackhole bh) {
		// Adapted from druid BoundFilterBenchmark
		final List<Integer> bitmapIndex = new ArrayList<>();
		boolean check = bitmapIndex.size() == cardinality ;
		bh.consume(check);
	}

}
