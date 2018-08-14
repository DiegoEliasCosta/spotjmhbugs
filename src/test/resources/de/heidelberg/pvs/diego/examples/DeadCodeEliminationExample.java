package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class DeadCodeEliminationExample {
	
	private StringBuilder str;

//	@Benchmark
//	public void stringBuilderNotUsed() {
//		StringBuilder str = new StringBuilder();
//		str.append("Blah");
//		str.append("Blah 2");
//	}
//	
//	@Benchmark
//	public void stringBuilderStoredInAField() {
//		StringBuilder str = new StringBuilder();
//		StringBuilder str2 = new StringBuilder(str);
//		if(true) {
//			StringBuilder str3 = new StringBuilder(str2);
//			str3.append("New String");
//		}
//		StringBuilder str4 = new StringBuilder(str);
//		str4.append("Str4");
//		str.append("Blah");
//		str.append("Blah 2");
//		this.str = str2.append("Useless");
//		this.str = str;
//	}
	
//	@Benchmark
//	public StringBuilder stringBuilderUsed() {
//		StringBuilder str = new StringBuilder();
//		str.append("Blah");
//		str.append("Blah 2");
//		return str;
//	}

//	@Benchmark
//	public StringBuilder stringBuilderDead() {
//		StringBuilder str = new StringBuilder();
//		
//		if(str == this.str) {
//			return null;
//		}
//		return str;
//	}
//	
//	@Benchmark
//	public void stringBuilderBeingConsumed(Blackhole bh) {
//		StringBuilder str = new StringBuilder();
//		str.append("Blah");
//		str.append("Blah 2");
//		bh.consume(str);
//	}
	
	@Benchmark
	public StringBuilder stringBuilderBeingUsedAndConsumed() {
		StringBuilder str = new StringBuilder();
		str.append("Blah");
		StringBuilder str2 = new StringBuilder(str);
		this.str = str2.append("Another Blah");
		return str2;
	}
	
}
