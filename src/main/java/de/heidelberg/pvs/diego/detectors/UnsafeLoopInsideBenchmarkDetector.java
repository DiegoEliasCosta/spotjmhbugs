package de.heidelberg.pvs.diego.detectors;

import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack.Item;

/**
 * Detector that identified a loop definition in the @Benchmark method
 * 
 * @author diego.costa
 *
 */
public class UnsafeLoopInsideBenchmarkDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_UNSAFELOOP_INSIDE_BENCHMARK = "JMH_UNSAFELOOP_INSIDE_BENCHMARK";

	private State checkState;
	private Set<Integer> storeRegister;
	
	static boolean DEBUG = true;

	public UnsafeLoopInsideBenchmarkDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	public void visitMethod(Method obj) {
		super.visitMethod(obj);
		checkState = State.START;
		storeRegister = new HashSet<>();

	}

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {

		switch (seen) {

		case Const.ISTORE:
		case Const.ISTORE_1:
		case Const.ISTORE_2:
		case Const.ISTORE_3:
		case Const.LSTORE:
		case Const.LSTORE_1:
		case Const.LSTORE_2:
		case Const.LSTORE_3:
		case Const.FSTORE:
		case Const.FSTORE_1:
		case Const.FSTORE_2:
		case Const.FSTORE_3:
			registeringNumericalVariable();
			break;

		case Const.GOTO_W:
		case Const.GOTO:

			analyzeLoop();
			break;

		case Const.LADD:
		case Const.IADD:
		case Const.FADD:
			analyzeAdition();
			break;

		// FIXME: Find a way to infer getting out of the loop

		}

	}

	private void analyzeAdition() {

		if (checkState == State.INSIDE_LOOP) {

			// Get both items from stack
			Item stackItem = stack.getStackItem(0);
			Item stackItem2 = stack.getStackItem(1);

			boolean wasItem1DefinedOutside = storeRegister.contains(stackItem.getRegisterNumber());
			boolean wasItem2DefinedOutside = storeRegister.contains(stackItem2.getRegisterNumber());

			if (wasItem1DefinedOutside || wasItem2DefinedOutside) {
				checkState = State.ADITION_MADE;
				if (DEBUG)
					System.out.println(String.format("CheckState %s", checkState));
			}

		}
	}

	private void analyzeLoop() {

		if (checkState == State.NUMERIC_CREATED_OUTSIDE_lOOP) {
			// FIXME: This does not cover all cases
			checkState = State.INSIDE_LOOP;
			if (DEBUG)
				System.out.println(String.format("CheckState %s", checkState));
		}

	}

	private void registeringNumericalVariable() {

		if (checkState == State.START && getNextOpcode() == Const.GOTO) {
			// This means that the current numeric variable has been assigned for the loop
			// and should not be analyzed
			return;
		}

		if (checkState == State.START) {
			checkState = State.NUMERIC_CREATED_OUTSIDE_lOOP;
			if (DEBUG)
				System.out.println(String.format("CheckState %s", checkState));
		}

		if (checkState == State.ADITION_MADE) {

			boolean isAccumulating = storeRegister.contains(getRegisterOperand());

			if (isAccumulating) {
				BugInstance bugInstance = new BugInstance(this, JMH_UNSAFELOOP_INSIDE_BENCHMARK, NORMAL_PRIORITY)
						.addClassAndMethod(this).addSourceLine(this);

				super.bugReporter.reportBug(bugInstance);

				// FIXME: This only allow us to get the first case
				checkState = State.DONE;
				if (DEBUG)
					System.out.println(String.format("CheckState %s", checkState));
			}
		}

		// Register the store
		storeRegister.add(getRegisterOperand());
	}


	enum State {
		START, NUMERIC_CREATED_OUTSIDE_lOOP, NUMERIC_ON_STACK, INSIDE_LOOP, ADITION_MADE, DONE

	}

}
