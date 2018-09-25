package de.heidelberg.pvs.diego.examples;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(value = Scope.Benchmark)
public class IgnoredStaticMethodReturnWithPrimitivesExample {
	
	@Benchmark
	public void bench() {
		Math.log(10); // +1
		
		Math.min(10, 15); // +1
		Math.abs(10D); // +1
		Math.copySign(15f, 10f); // +1
		
		Array.get(new Object(), 10); // +0 with object
		
		byte[] array = null;
		long[] longArray = null;
		mySecondMethod(array, longArray); // +0 - Arrays are objects
		
		myMethod(0, 0D, 0F,(short)0, 0L, '0', getClass(), new ArrayList<>()); // +0 it has an array
		
	}
	
	public static double myMethod(int i, double d, float f, short s, long l, char c, Object k, ArrayList<Integer> list) {
		
		list.add(i);
		return i * d;
		
	}
	
	public static long mySecondMethod(byte[] array, long[] longArray) {
		return 0L;
		
	}

}
