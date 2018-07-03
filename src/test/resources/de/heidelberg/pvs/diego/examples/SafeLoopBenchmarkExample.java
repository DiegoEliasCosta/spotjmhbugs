package de.heidelberg.pvs.diego.examples;

import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

public class SafeLoopBenchmarkExample {
	
	List<Integer> myList;
	
	int size;
	
	@Setup
	public void setup() {
		for (int i = 0; i < size; i++) {
			myList.add(i);
		}
	}
	
	
	@Benchmark
	public void safeLoopBenchmark(Blackhole bh) {
		
		for(Integer element: myList) {
			bh.consume(element);
		}
		
	}
	
	@Benchmark
	public void safeLoopBenchmark2(Blackhole bh) {
		
		for(int i = 0; i < 10000; i += 10) {
			bh.consume(i);
		}
		
	}

}
