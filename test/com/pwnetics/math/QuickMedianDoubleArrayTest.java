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


package com.pwnetics.math;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import com.pwnetics.alg.QuickSelectDoubleArrayTest;

public class QuickMedianDoubleArrayTest {

	private static final double NaN = Double.NaN;
	private static final double INF = Double.POSITIVE_INFINITY;
	private static final double NINF = Double.NEGATIVE_INFINITY;
	private static final double MAX = Double.MAX_VALUE;
	private static final double NMAX = -MAX;
	private static final double MAX_1 = MAX - Double.MIN_VALUE;
	private static final double NMAX_1 = -MAX_1;
	private static final double MAX_2 = MAX - 2 * Double.MIN_VALUE;
	private static final double NMAX_2 = -MAX_2;


	/**
	 * Doesn't handle special floating point values correctly.
	 * @param x
	 * @param y
	 * @return
	 */
	private double averageBigDecimal(double x, double y) {
		return new BigDecimal("0.5").multiply(new BigDecimal(x).add(new BigDecimal(y))).doubleValue();
	}


	@Test
	public void testAverage() {
		QuickMedianDoubleArray med = new QuickMedianDoubleArray();
		double x,y;
		x = MAX; y = MAX; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = MAX; y = MAX - Double.MIN_VALUE; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = MAX; y = MAX - 2*Double.MIN_VALUE; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = MAX; y = NMAX; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = MAX; y = NMAX_1; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = MAX; y = NMAX_2; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = 0; y = 10; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = 0; y = 11; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = 3; y = 11; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = 0; y = -10; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = 0; y = -11; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = 3; y = -11; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = -3; y = 11; assertTrue(averageBigDecimal(x,y) == med.average(x,y));
		x = -3; y = -11; assertTrue(averageBigDecimal(x,y) == med.average(x,y));

		// Edge cases involving Infinity and NaN
		assertTrue(med.average(MAX, MAX) == MAX);
		assertTrue(med.average(Double.MIN_VALUE, Double.MIN_VALUE) == Double.MIN_VALUE);
		assertTrue(med.average(0.0, 0.0) == 0.0);
		assertTrue(med.average(INF, INF) == INF);  // average([inf,inf]) == inf
		assertTrue(med.average(NINF, NINF) == NINF);  // average([-inf,-inf]) == -inf
		assertTrue(med.average(INF, 0.0) == INF);  // average([inf,0.0]) == inf
		assertTrue(med.average(NINF, 0.0) == NINF);  // average([-inf,0.0]) == -inf
		assertTrue(Double.isNaN(med.average(INF, NINF)));  // isnan(average([inf,-inf]))
		assertTrue(Double.isNaN(med.average(INF, NaN)));  // isnan(average([inf,nan]))
		assertTrue(Double.isNaN(med.average(NaN, NaN)));    // isnan(average([nan,nan]))
		assertTrue(Double.isNaN(med.average(NaN, 4.2)));  // isnan(average([nan,4.2]))
	}


	@Test
	public void testMedian() {
		QuickMedianDoubleArray med = new QuickMedianDoubleArray();
		try {
			med.median(null);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		try {
			med.median(new double[0]);
			assertTrue("should throw exception", false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);  // Exception correctly thrown
		}

		double [] v;
		double m;

		// Trivial
		v = new double[] {42.42};
		assertTrue(42.42 == med.median(v));

		// Trivial arrays with boundary values
		v = new double[] {MAX};
		assertTrue(MAX == med.median(v));
		v = new double[] {NMAX};
		assertTrue(NMAX == med.median(v));
		v = new double[] {INF};
		m = med.median(v);
		assertTrue(Double.isInfinite(m) && m > 0);
		v = new double[] {NINF};
		m = med.median(v);
		assertTrue(Double.isInfinite(m) && m < 0);

		// More interesting arrays
		v = new double[] {MAX_1, MAX, MAX, MAX};
		assertTrue(MAX == med.median(v));

		// Note, arrays of length 2 aren't necessarily a good test because these get passed to a special function
		m = med.median(new double[] {INF, INF, INF});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {INF, INF});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {INF, INF, INF, INF});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {NINF, NINF, NINF});
		assertTrue(Double.isInfinite(m) && m < 0);
		m = med.median(new double[] {NINF, NINF});
		assertTrue(Double.isInfinite(m) && m < 0);
		m = med.median(new double[] {NINF, NINF, NINF, NINF});
		assertTrue(Double.isInfinite(m) && m < 0);
		m = med.median(new double[] {INF, NINF, INF});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {INF, NINF, NINF});
		assertTrue(Double.isInfinite(m) && m < 0);
		m = med.median(new double[] {NINF, INF});
		assertTrue(Double.isNaN(m));
		m = med.median(new double[] {NINF, NINF, INF, INF});
		assertTrue(Double.isNaN(m));
		m = med.median(new double[] {INF, 0.0});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {INF, 0.0, INF, 0.0});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {INF, MAX});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {INF, MAX, INF, MAX});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {INF, NMAX});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {INF, NMAX, INF, NMAX});
		assertTrue(Double.isInfinite(m) && m > 0);
		m = med.median(new double[] {NINF, MAX});
		assertTrue(Double.isInfinite(m) && m < 0);
		m = med.median(new double[] {NINF, MAX, NINF, MAX});
		assertTrue(Double.isInfinite(m) && m < 0);
		m = med.median(new double[] {NINF, NMAX});
		assertTrue(Double.isInfinite(m) && m < 0);
		m = med.median(new double[] {NINF, NMAX, NINF, NMAX});
		assertTrue(Double.isInfinite(m) && m < 0);

		// Random testing
		Random random = new Random(33);
		for(int trial=0; trial<200; trial++) {
			double [] reference = new double[10 + random.nextInt(2000)];
			v = new double[reference.length];
			for(int i=0; i<reference.length; i++) {
				reference[i] = random.nextDouble();
			}

			double [] sorted = new double[reference.length];
			System.arraycopy(reference, 0, sorted, 0, reference.length);
			Arrays.sort(sorted);

			System.arraycopy(reference, 0, v, 0, reference.length);
			m = med.median(v);
			QuickSelectDoubleArrayTest.assertSelect(v, reference.length-1, reference);  // median calculation isn't destructive
			assertTrue(SortingMedianDoubleArray.median(reference) == m);
		}
	}
}
