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

import org.apache.bcel.Const;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

/**
 * Detector that identified a loop definition in the @Benchmark method
 * 
 * @author diego.costa
 *
 */
public class LoopInsideBenchmarkDetector extends AbstractJMHBenchmarkMethodDetector {

	private static final String JMH_LOOP_INSIDE_BENCHMARK = "JMH_LOOP_INSIDE_BENCHMARK";

	public LoopInsideBenchmarkDetector(BugReporter bugReporter) {
		super(bugReporter);
	}

	@Override
	protected void analyzeBenchmarkMethodOpCode(int seen) {

		switch (seen) {

		/**
		 * This is a naive first approach for finding loops on code. GOTOs are
		 * used for loops in general but can also appear on some error handling
		 * mechanisms. So far, I have not fully understood SpotBugs way of
		 * dealing with loops but should update this method as soon as I find a
		 * more reliable way of identifying those.
		 * 
		 * A good link for more info on the subject
		 * https://stackoverflow.com/questions/6792305/identify-loops-in-java-byte-code
		 * 
		 */
		case Const.GOTO_W:
		case Const.GOTO:

			BugInstance bugInstance = new BugInstance(this, JMH_LOOP_INSIDE_BENCHMARK, NORMAL_PRIORITY)
					.addClassAndMethod(this).addSourceLine(this);

			super.bugReporter.reportBug(bugInstance);

		}

	}

}
