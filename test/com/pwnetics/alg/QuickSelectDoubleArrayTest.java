/*
Copyright 2011 Brian Romanowski. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.

THIS SOFTWARE IS PROVIDED BY BRIAN ROMANOWSKI ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BRIAN ROMANOWSKI OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors.
*/


package com.pwnetics.alg;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class QuickSelectDoubleArrayTest {
	/* Wolfram Alpha doesn't seem to handle cases of Infinity very well for some of these operations.
	 * Let's base our truth values on Numpy and mathy reasoning: "from numpy import average, median, inf, nan, isnan"
	 *
	 * In Numpy 1.6.1, the "nan" value from Numpy will sort last, which means you can change the median of a
	 * sequence by inserting NaN's into that sequence!   Python's built-in sort() seems to have undefined
	 * behavior when nan's are in the list, with some lists it sorts reasonably, but not always.
	 */

	private static final double NaN = Double.NaN;
	private static final double INF = Double.POSITIVE_INFINITY;
	private static final double NINF = Double.NEGATIVE_INFINITY;
	private static final double MAX = Double.MAX_VALUE;
	private static final double NMAX = -MAX;
	private static final double MAX_1 = MAX - Double.MIN_VALUE;
	private static final double MAX_2 = MAX - 2 * Double.MIN_VALUE;


	/**
	 * Tests whether the subset of elements in the array values[0:selectIdx + 1] completely contains the elements in the array firstValues.
	 * @param values
	 * @param selectIdx
	 * @param firstValues
	 */
	public static void assertSelect(double [] values, int selectIdx, double [] firstValues) {
		assertTrue("error in test", selectIdx == firstValues.length - 1);

		List<Double> fv = new ArrayList<Double>();
		for(double d : firstValues) {
			fv.add(d);
		}

		nextPartitionValue: for(int i=0; i<=selectIdx; i++) {
			Iterator<Double> it = fv.iterator();
			while(it.hasNext()) {
				if(Double.compare(values[i], it.next()) == 0) {
					it.remove();
					continue nextPartitionValue;
				}
			}
			assertTrue("no actual partition value for test partition value: " + values[i], false);
		}
	}


	@Test
	public void testPartitionIllegalArguments() {
		// Using this constructor so we can feed it a deterministic random number generator
		QuickSelectDoubleArray p = new QuickSelectDoubleArray(QuickSelectDoubleArray.DEFAULT_IS_DESCENDING, QuickSelectDoubleArray.DEFAULT_PIVOT_METHOD, new Random(42L), QuickSelectDoubleArray.DEFAULT_MEDIAN_OF_THREE_THRESHOLD);

		try {
			p.select(null, 0);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.select(new double[10], -1);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}
	}


	@Test
	public void testPartitionSublistIllegalArguments() {
		QuickSelectDoubleArray p = new QuickSelectDoubleArray();

		try {
			p.select(null, 0, 0, 0);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.select(new double[] {}, 0, 0, 0);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.select(new double[10], -1, 0, 10);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.select(new double[10], 1, 0, 11);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.select(new double[10], 10, 0, 11);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.select(new double[] {42.2}, 1, 1, 1);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.select(new double[] {42.2, 42.2}, 1, 0, 1);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}
	}


	@Test
	public void testPartitionSublistSimplest() {
		// Using this constructor so we can feed it a deterministic random number generator
		QuickSelectDoubleArray p = new QuickSelectDoubleArray(QuickSelectDoubleArray.DEFAULT_IS_DESCENDING, QuickSelectDoubleArray.DEFAULT_PIVOT_METHOD, new Random(42L), QuickSelectDoubleArray.DEFAULT_MEDIAN_OF_THREE_THRESHOLD);

		double [] v;

		// Trivial arrays
		v = new double[] {42.42};
		p.select(v, 0, 0, v.length);
		assertTrue(v[0] == 42.42);

		// Trivial arrays with boundary values
		v = new double[] {MAX};
		p.select(v, 0, 0, v.length);
		assertTrue(v[0] == MAX);
		v = new double[] {NMAX};
		p.select(v, 0, 0, v.length);
		assertTrue(v[0] == NMAX);
		v = new double[] {INF};
		p.select(v, 0, 0, v.length);
		assertTrue(v[0] == INF);
		v = new double[] {NINF};
		p.select(v, 0, 0, v.length);
		assertTrue(v[0] == NINF);

		// More interesting arrays
		v = new double[] {MAX_1, MAX, MAX, MAX};
		p.select(v, 0, 0, v.length);
		assertTrue(v[0] == MAX);
		v = new double[] {MAX_1, MAX, MAX, MAX};
		p.select(v, 1, 0, v.length);
		assertTrue(v[0] == MAX && v[1] == MAX);
		v = new double[] {MAX_1, MAX, MAX, MAX};
		p.select(v, 2, 0, v.length);
		assertTrue(v[0] == MAX && v[1] == MAX && v[2] == MAX);
		v = new double[] {MAX_1, MAX, MAX, MAX};
		p.select(v, 2, 0, 3);
		assertSelect(v, 2, new double[] {MAX_1, MAX, MAX});
		v = new double[] {MAX_1, MAX, MAX, MAX};
		p.select(v, 1, 0, 3);
		assertSelect(v, 1, new double[] {MAX, MAX});

		// Testing beginIndex
		v = new double[] {MAX_1, MAX, MAX_1, MAX};
		p.select(v, 0, 0, 4);
		assertTrue(v[0] == MAX);
		v = new double[] {MAX_1, MAX, MAX_1, MAX};
		p.select(v, 1, 1, 4);
		assertTrue(v[1] == MAX);
		v = new double[] {MAX_1, MAX, MAX_1, MAX};
		p.select(v, 2, 0, 4);
		assertTrue(v[1] == MAX_1);
		v = new double[] {MAX_1, MAX, MAX_1, MAX};
		p.select(v, 2, 1, 4);
		assertTrue(v[1] == MAX);
		v = new double[] {MAX_1, MAX, MAX_1, MAX};
		p.select(v, 2, 2, 4);
		assertTrue(v[1] == MAX);
		p.select(v, 2, 1, 3);
		assertTrue(v[1] == MAX_1);
		v = new double[] {MAX_1, MAX, MAX_1, MAX};
	}


	@Test
	public void testPartitionSublistReverse() {
		double [] v;

		v = new double[] {MAX_1, MAX, MAX, MAX};
		new QuickSelectDoubleArray(false).select(v, 0, 0, 4);
		assertTrue(v[0] == MAX_1);
		v = new double[] {MAX_1, MAX, MAX, MAX};
		new QuickSelectDoubleArray(false).select(v, 1, 0, 4);
		assertTrue(v[0] == MAX_1 && v[1] == MAX);
		v = new double[] {MAX_1, MAX, MAX, MAX};
		new QuickSelectDoubleArray(false).select(v, 2, 0, 4);
		assertTrue(v[2] == MAX);
		assertSelect(v, 2, new double[] {MAX_1, MAX, MAX});
		v = new double[] {MAX_1, MAX, MAX, MAX};
		new QuickSelectDoubleArray(false).select(v, 2, 0, 3);
		assertSelect(v, 2, new double[] {MAX_1, MAX, MAX});
		assertTrue(v[2] == MAX);
		v = new double[] {MAX_1, MAX, MAX, MAX};
		new QuickSelectDoubleArray(false).select(v, 1, 0, 3);
		assertSelect(v, 1, new double[] {MAX_1, MAX});
		assertTrue(v[1] == MAX);
	}


	@Test
	public void testPartitionSublistSimple() {
		// Using this constructor so we can feed it a deterministic random number generator
		QuickSelectDoubleArray p = new QuickSelectDoubleArray(QuickSelectDoubleArray.DEFAULT_IS_DESCENDING, QuickSelectDoubleArray.DEFAULT_PIVOT_METHOD, new Random(42L), QuickSelectDoubleArray.DEFAULT_MEDIAN_OF_THREE_THRESHOLD);

		double [] v;

		// More interesting arrays
		double [] reference = new double[] {MAX_1, MAX_2, NINF, INF, INF, MAX, MAX};
		v = new double[reference.length];

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 2, 0, v.length);
		assertSelect(v, reference.length-1, reference);
		assertSelect(v, 2, new double[] {INF, INF, MAX});
		assertTrue(v[2] == MAX);

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 2, 0, v.length - 1);
		assertSelect(v, reference.length-1, reference);
		assertSelect(v, 2, new double[] {INF, INF, MAX});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 2, 0, v.length - 2);
		assertSelect(v, reference.length-1, reference);
		assertSelect(v, 2, new double[] {INF, INF, MAX_1});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 2, 0, v.length - 3);
		assertSelect(v, reference.length-1, reference);
		assertSelect(v, 2, new double[] {INF, MAX_1, MAX_2});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 2, 0, v.length - 4);
		assertSelect(v, reference.length-1, reference);
		assertSelect(v, 2, new double[] {NINF, MAX_1, MAX_2});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 1, 0, v.length - 5);
		assertSelect(v, reference.length-1, reference);
		assertSelect(v, 1, new double[] {MAX_1, MAX_2});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 0, 0, v.length - 6);
		assertSelect(v, reference.length-1, reference);
		assertSelect(v, 0, new double[] {MAX_1});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 0, 0, 1);
		assertSelect(v, reference.length-1, reference);

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 3, 0, v.length);
		assertSelect(v, reference.length-1, reference);  assertSelect(v, 3, new double[] {INF, INF, MAX, MAX});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 4, 0, v.length);
		assertSelect(v, reference.length-1, reference);  assertSelect(v, 4, new double[] {INF, INF, MAX, MAX, MAX_1});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 5, 0, v.length);
		assertSelect(v, reference.length-1, reference);  assertSelect(v, 5, new double[] {INF, INF, MAX, MAX, MAX_1, MAX_2});

		System.arraycopy(reference, 0, v, 0, reference.length);  p.select(v, 6, 0, v.length);
		assertSelect(v, reference.length-1, reference);
	}


	@Test
	public void testPartitionSublistRandom() {
		// Using this constructor so we can feed it a deterministic random number generator
		QuickSelectDoubleArray p = new QuickSelectDoubleArray(QuickSelectDoubleArray.DEFAULT_IS_DESCENDING, QuickSelectDoubleArray.DEFAULT_PIVOT_METHOD, new Random(42L), QuickSelectDoubleArray.DEFAULT_MEDIAN_OF_THREE_THRESHOLD);

		Random random = new Random(37);
		for(int trial=0; trial<100; trial++) {
			double [] reference = new double[990 + random.nextInt(20)];
			double [] v = new double[reference.length];
			int [] selectIdx = new int[] {0,1,reference.length-1, reference.length - 2, (int)(reference.length * 0.5)};

			for(int i=0; i<reference.length; i++) {
				reference[i] = random.nextDouble();
			}

			double [] sorted = new double[reference.length];
			System.arraycopy(reference, 0, sorted, 0, reference.length);
			Arrays.sort(sorted);

			for(int sIdx : selectIdx) {
				System.arraycopy(reference, 0, v, 0, reference.length);
				int[] selectBounds = p.select(v, sIdx, 0, v.length);
				assertSelect(v, reference.length-1, reference);
				assertSelect(v, sIdx, Arrays.copyOfRange(sorted, sorted.length - sIdx - 1, sorted.length));
				assertTrue(v[sIdx] == sorted[sorted.length - sIdx - 1]);  // test that the last element in the partition is in the correct sorted position

				// Test return selection index fixed-point boundaries
				if(selectBounds[0] >= 0) {
					assertTrue(v[selectBounds[0]] == sorted[sorted.length - selectBounds[0] - 1]);
				}
				if(selectBounds[1] >= 0) {
					assertTrue(v[selectBounds[1]] == sorted[sorted.length - selectBounds[1] - 1]);
				}
			}
		}
	}


	@Test
	public void testContainsNaNIllegalArguments() {
		QuickSelectDoubleArray p = new QuickSelectDoubleArray();

		try {
			p.containsNaN(null);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}
	}


	@Test
	public void testContainsNaNSublistIllegalArguments() {
		QuickSelectDoubleArray p = new QuickSelectDoubleArray();
		try {
			p.containsNaN(null, 0, 0);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.containsNaN(new double[10], -2, -1);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.containsNaN(new double[10], -1, 5);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.containsNaN(new double[10], 5, 5);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			p.containsNaN(new double[10], 0, 11);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}
	}


	@Test
	public void testContainsNaNSublist() {
		QuickSelectDoubleArray p = new QuickSelectDoubleArray();

		assertTrue(!p.containsNaN(new double[10], 0, 10));
		assertTrue(!p.containsNaN(new double[10], 0, 5));
		assertTrue(!p.containsNaN(new double[] {MAX, NINF, INF}, 0, 3));
		assertTrue(p.containsNaN(new double[] {NaN}, 0, 1));
		assertTrue(p.containsNaN(new double[] {INF / NINF}, 0, 1));
		assertTrue(p.containsNaN(new double[] {NaN,0,1}, 0, 3));
		assertTrue(p.containsNaN(new double[] {NaN,0,1}, 0, 2));
		assertTrue(p.containsNaN(new double[] {NaN,0,1}, 0, 1));
		assertTrue(!p.containsNaN(new double[] {NaN,0,1}, 1, 3));
		assertTrue(p.containsNaN(new double[] {0,NaN,0}, 0, 3));
		assertTrue(p.containsNaN(new double[] {0,NaN,0}, 0, 2));
		assertTrue(!p.containsNaN(new double[] {0,NaN,0}, 0, 1));
		assertTrue(p.containsNaN(new double[] {0,NaN,0}, 1, 3));
		assertTrue(!p.containsNaN(new double[] {0,NaN,0}, 2, 3));
		assertTrue(p.containsNaN(new double[] {0,1,NaN}, 0, 3));
		assertTrue(!p.containsNaN(new double[] {0,1,NaN}, 0, 2));
		assertTrue(p.containsNaN(new double[] {0,1,NaN}, 1, 3));
		assertTrue(p.containsNaN(new double[] {0,1,NaN}, 2, 3));
	}
}
