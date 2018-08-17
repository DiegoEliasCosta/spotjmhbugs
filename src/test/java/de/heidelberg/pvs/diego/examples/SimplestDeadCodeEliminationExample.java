package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(value = Scope.Benchmark)
public class SimplestDeadCodeEliminationExample {

	private StringBuilder str;

	@Benchmark
	public void stringBuilderNotUsed() {
		StringBuilder str = new StringBuilder();
		str.append("Blah");
		str.append("Blah 2");
		
	}
	
	@Benchmark
	public void stringBuilderUsedInComparison(Blackhole bh) {
		StringBuilder str = new StringBuilder();
		str.append("Blah");
		str.append("Blah 2");
		
		boolean exists = str != null;
		bh.consume(exists);
	}
	
}
