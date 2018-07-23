package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;

public class DeadCodeEliminationExample {
	
	@Benchmark
	public void stringBuilderNotUsed() {
		StringBuilder str = new StringBuilder();
		str.append("Blah");
		str.append("Blah 2");
	}
	
	@Benchmark
	public StringBuilder stringBuilderUsed() {
		StringBuilder str = new StringBuilder();
		str.append("Blah");
		str.append("Blah 2");
		return str;
	}

}
