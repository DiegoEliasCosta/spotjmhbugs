/**
 * MIT License
 *
 * Copyright (c) 2018 Diego Costa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.heidelberg.pvs.diego.detectors;

import java.util.BitSet;

import org.apache.bcel.Const;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

/**
 * Checker for identifying methods call with ignored return values on JMH benchmarks.
 * 
 * @author diego.costa
 *
 */
public class IgnoredMethodReturnDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_IGNORED_METHOD_RETURN = "JMH_IGNORED_METHOD_RETURN";

	/**
	 * Operation codes related to a method call
	 */
	private static final BitSet INVOKE_OPCODE_SET = new BitSet();
	static {
		INVOKE_OPCODE_SET.set(Const.INVOKEINTERFACE);
		INVOKE_OPCODE_SET.set(Const.INVOKESPECIAL);
		INVOKE_OPCODE_SET.set(Const.INVOKEVIRTUAL);
	}

	public IgnoredMethodReturnDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	private boolean isDiscardOperation(int nextOpcode) {
		return nextOpcode == Const.POP || nextOpcode == Const.POP2;
	}

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {
		
		// Investigate the method calls (invoke op code)
		if (INVOKE_OPCODE_SET.get(seen)) {

			int nextOpcode = getNextOpcode();
			if (isDiscardOperation(nextOpcode)) {

				// Create the bug
				BugInstance bugInstance = new BugInstance(this, JMH_IGNORED_METHOD_RETURN, LOW_PRIORITY)
						.addClassAndMethod(this).addSourceLine(this);

				super.bugReporter.reportBug(bugInstance);
			}

		}

	}

}
