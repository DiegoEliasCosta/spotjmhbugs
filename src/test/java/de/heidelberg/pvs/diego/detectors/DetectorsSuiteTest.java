package de.heidelberg.pvs.diego.detectors;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    BenchmarkMethodDetectorTest.class,
    DeadStoreBenchmarkDetectorTest.class,
    FixtureWithInvocationScopeDetectionTest.class,
    IgnoredMethodReturnDetectorTest.class,
    IgnoredStaticMethodReturnDetectorTest.class,
    IgnoredStaticMethodReturnWithPrimitivesDetectorTest.class,
    LoopInsideBenchmarkDetectorTest.class,
    NotForkedBenchmarkDetectorTest.class,
    SingleShotBenchmarkDetectorTest.class,
    StateFinalFieldDetectorTest.class,
    UnsafeLoopInsideBenchmarkDetectorTest.class,
    UnsinkedVariableBenchmarkDetectorTest.class,
})

public class DetectorsSuiteTest {

}
