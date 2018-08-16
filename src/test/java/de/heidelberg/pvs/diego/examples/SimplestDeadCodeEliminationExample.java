package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(value = Scope.Benchmark)
public class SimplestDeadCodeEliminationExample {

	private StringBuilder str;

	@Benchmark
	public void stringBuilderNotUsed() {
		StringBuilder str = new StringBuilder();
		str.append("Blah");
		str.append("Blah 2");
		
	}
	
}
