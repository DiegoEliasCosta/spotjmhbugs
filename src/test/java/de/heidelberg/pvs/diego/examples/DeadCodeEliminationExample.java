package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(value = Scope.Benchmark)
public class DeadCodeEliminationExample {
	
	private StringBuilder str;

	@Benchmark
	public StringBuilder stringBuilderDead() {
		StringBuilder str = new StringBuilder();
		if(str == this.str) {
			return null;
		}
		return str;
	}
	
	@Benchmark 
	public void stringBuilderBeingConsumed(Blackhole bh) {
		StringBuilder str = new StringBuilder();
		str.append("Blah");
		str.append("Blah 2");
		bh.consume(str);
		
		StringBuilder str2 = new StringBuilder();
		str2.append("Blah");
		str2.append("Blah 2");
		
	}
	
	@Benchmark
	public StringBuilder stringBuilderBeingUsedAndConsumed() {
		StringBuilder str = new StringBuilder();
		str.append("Blah");
		StringBuilder str2 = new StringBuilder(str);
		this.str = str2.append("Another Blah");
		return str2;
	}
	
	@Benchmark
	public void benchmarkWithDeadStore() {
		StringBuilder str = new StringBuilder(); // +1
		StringBuilder str2 = new StringBuilder(); // +1
	}
	
	
	public StringBuilder notBenchmark() {
		StringBuilder str = new StringBuilder();
		StringBuilder str2 = new StringBuilder();
		return str2;
	}
	
}
