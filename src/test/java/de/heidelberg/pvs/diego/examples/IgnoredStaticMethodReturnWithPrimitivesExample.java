package de.heidelberg.pvs.diego.examples;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(value = Scope.Benchmark)
public class IgnoredStaticMethodReturnWithPrimitivesExample {
	
	@Benchmark
	public double bench() {
		Math.log(10);
		
		Math.min(10, 15);
		Math.abs(10D);
		Math.copySign(15f, 10f);
		
		Array.get(new Object(), 10);
		
		return myMethod(0, 0D, 0F,(short)0, 0L, '0', getClass(), new ArrayList<>());
		
	}
	
	public static double myMethod(int i, double d, float f, short s, long l, char c, Object k, ArrayList<Integer> list) {
		
		list.add(i);
		return i * d;
		
		
	}

}
