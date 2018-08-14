package de.heidelberg.pvs.diego.examples;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class StateInnerClassExample {
	
	@State(Scope.Thread)
	public class MyState {
		
		@SuppressWarnings("unused")
		private static final int constant = 10;
		
		public final long constant2 = 15L; 
		
	}

}
