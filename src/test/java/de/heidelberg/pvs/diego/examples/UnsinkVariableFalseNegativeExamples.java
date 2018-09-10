package de.heidelberg.pvs.diego.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class UnsinkVariableFalseNegativeExamples {

	private final List<List<Integer>> integersJDK2 = new ArrayList<>();

//	@Benchmark
//	public void serial_lazy_jdk() {
//		List<Integer> evens = this.integersJDK.stream().filter(each -> each % 2 == 0).collect(Collectors.toList());
//		Assert.assertEquals(LIST_SIZE / 2, evens.size());
//	}

	@Benchmark
	public void serial_lazy_jdk_2() {
		List<Integer> flatMap = this.integersJDK2.stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

}
