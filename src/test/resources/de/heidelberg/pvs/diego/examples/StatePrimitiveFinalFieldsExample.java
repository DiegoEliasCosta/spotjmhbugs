package de.heidelberg.pvs.diego.examples;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class StatePrimitiveFinalFieldsExample {
	
	@SuppressWarnings("unused")
	private final double finalVariable = 0.356d;
	
	public final int publicFinalVariable = 10;
	
	public final static String publicstaticFinalVariable = "This is a constant";
	
	public final static Object thisIsAObject = new Object();
	
	public int nonFinalVariable; // non-final should not be reported

}
