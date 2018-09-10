package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;

public class DeadCodeFalsePositives {
	
	@Benchmark
	public StringBuilder withExceptions() {
		StringBuilder str = new StringBuilder();
		StringBuilder str2 = new StringBuilder(str);
		try {
			str.append("Blah");
			str2 = str;
		} catch(RuntimeException e) { // e Should not appear as DeadCode
			System.err.println(e);
		}
		return str2;
	}

}
