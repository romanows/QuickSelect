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

import java.util.Arrays;

/**
 * Sometimes calculates the median of an array via sorting.
 * This is only meant to compare sorting against the QuickSelect algorithm to Not guaranteed to take care of edge cases, it really just handles normal doubles (not infinity or NaN) in a long-ish array.
 *
 * @author romanows
 */
public class SortingMedianDoubleArray {
	private static QuickMedianDoubleArray quickMedian = new QuickMedianDoubleArray();

	/**
	 * Sometimes calculates the median of an array via sorting, but not guaranteed to handle anything like an edge-case.
	 * Sorts the given array in-place.
	 *
	 * @param values values for which to find the median
	 * @return median value of the given array (and sorts the given array)
	 */
	public static double median(double [] values) {
		Arrays.sort(values);
		if((values.length & 1) == 1) {
			return values[values.length >>> 1];
		} else {
			return quickMedian.average(values[values.length >>> 1], values[(values.length >>> 1) - 1]);
		}
	}
}
