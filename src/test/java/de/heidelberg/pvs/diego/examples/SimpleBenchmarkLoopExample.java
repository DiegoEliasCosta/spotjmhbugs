package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class SimpleBenchmarkLoopExample {

	@Benchmark
	public void benchmarkWithForLoop(Blackhole bh) {
		
		int n = (int) Math.random();
		for (int i = 0; i < n; i++) {
			bh.consume(i);
		}
		
	}
	
	
	@Benchmark
	public void benchmarkWithWhileLoop(Blackhole bh) {
		
		int n = (int) Math.random();
		
		int i = 0;
		while(i < n) {
			bh.consume(i++);
		}
	}
	
//	@Benchmark
//	public void benchmarkWithExceptionHandling(Blackhole bh) {
//		
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}

	
}
