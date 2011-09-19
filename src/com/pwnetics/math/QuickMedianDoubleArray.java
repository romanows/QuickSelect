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

import com.pwnetics.alg.QuickSelectDoubleArray;

/**
 * Uses {@link QuickSelectDoubleArray} to find the median in expected O(n) time.
 *
 * @author romanows
 */
public class QuickMedianDoubleArray {
	private final QuickSelectDoubleArray quickSelect;


	/** Constructor */
	public QuickMedianDoubleArray() {
		quickSelect = new QuickSelectDoubleArray();
	}


	/**
	 * Constructor.
	 * @param quickSelect {@link QuickSelectDoubleArray} object to use for selecting the middle elements for the median calculation
	 */
	public QuickMedianDoubleArray(QuickSelectDoubleArray quickSelect) {
		this.quickSelect = quickSelect;
	}

	/**
	 * Finds the average of two values.
	 * An assert checks that the given values are not NaN; if disabled, undefined behavior will occur.
	 *
	 * @param x one of two values to average
	 * @param y one of two values to average
	 * @return average of two given values
	 */
	protected double average(double x, double y) {
		assert(!Double.isNaN(x) && !Double.isNaN(y));
		if(Double.isInfinite(x) || Double.isInfinite(y)) {
			// Overflow isn't a problem; the (x+y) addition is always +/- infinity or NaN
			return (x + y) / 2.0;
		}

		if(x >= y) {
			if(y <= 0 && x >= 0) {
				// If x and y are opposite signs, we can use (x+y)/2.0 and be sure the sum doesn't overflow
				return (x + y) / 2.0;
			}
			// Otherwise, we first calculate the delta, which we know is smaller than either of the two numbers, and thus doesn't overflow
			return ((x - y) / 2.0) + y;
		} else {
			if(y >= 0 && x <= 0) {
				return (x + y) / 2.0;
			}
			return ((y - x) / 2.0) + x;
		}
	}


	/**
	 * The median is the middle value in an odd-length list or the average of the two middle numbers in an even-length list.
	 * Behavior is undefined for list that contain {@link Double#NaN} values.
	 * This method may reorder the elements in the given array.
	 * The median is found in O(n) time using the QuickSelect algorithm.
	 *
	 * @param values array over which to calculate the median, must not contain {@link Double#NaN} elements.
	 * @return the median value in the list
	 */
	public double median(double [] values) {
		if(values == null || values.length == 0) {
			throw new IllegalArgumentException(new NullPointerException());
		}

		double median;
		if(values.length == 0) {
			median = values[0];
		} else if(values.length == 1) {
			median = values[0];
		} else if(values.length == 2) {
			median = average(values[0], values[1]);
		} else if((values.length & 1) == 1) {
			// Length of values is odd, median is the value where half of the numbers are lower or equal and half are higher or equal
			int middleIdx = values.length >>> 1;
			quickSelect.select(values, middleIdx);
			median = values[middleIdx];
		} else {
			// Length of values is even, median is the average of the two numbers such that half of the numbers are lower or equal to the first and half are higher or equal to the second and the first is lower or equal to the second
			int highMiddleIdx = values.length >>> 1;
			int lowMiddleIdx = highMiddleIdx - 1;

			int [] pivotBounds = quickSelect.select(values, highMiddleIdx, 0, values.length);
			double highMedianValue = values[highMiddleIdx];

			if(pivotBounds[0] == lowMiddleIdx) {
				median = average(values[lowMiddleIdx], highMedianValue);
			} else {
				if(pivotBounds[0] < 0) {
					pivotBounds[0] = 0;
				} else {
					pivotBounds[0]++;
				}
				quickSelect.select(values, lowMiddleIdx, pivotBounds[0], highMiddleIdx);
				median = average(values[lowMiddleIdx], highMedianValue);
			}
		}

		return median;
	}
}
