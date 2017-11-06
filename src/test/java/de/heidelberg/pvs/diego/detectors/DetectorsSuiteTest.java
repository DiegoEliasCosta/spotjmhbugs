package de.heidelberg.pvs.diego.detectors;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   IgnoredMethodReturnDetectorTest.class,
   LoopInsideBenchmarkDetectorTest.class,
   StateFinalFieldDetectorTest.class,
   NotForkedBenchmarkDetectorTest.class,
   FixtureWithInvocationScopeDetectionTest.class,
   SingleShotBenchmarkDetectorTest.class,
})

public class DetectorsSuiteTest {

}
