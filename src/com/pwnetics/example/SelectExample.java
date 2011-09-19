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


package com.pwnetics.example;

import com.pwnetics.alg.QuickSelectDoubleArray;

/**
 * Example usage of {@link QuickSelectDoubleArray}.
 * Note that {@link QuickSelectDoubleArray} is randomized by default, so repeated runs will show different output.
 *
 * <p>One of the more interesting example outputs is the following:</p>
 * <pre>
 * original:	{6.0, 8.0, 7.0, 5.0, 3.0, 0.0, 9.0, 1.0, 2.0, 4.0}
 * select(5):	{8.0, 7.0, 9.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0}
 * select(2):	{9.0, 8.0, 7.0, 6.0, 4.0, 5.0, 3.0, 2.0, 1.0, 0.0}
 * </pre>
 *
 * <p>If you notice any order in the output apart from what is defined by the {@link QuickSelectDoubleArray} documentation, it is coincidental.</p>
 *
 * @author romanows
 */
public class SelectExample {

	public static void main(String[] args) {
		QuickSelectDoubleArray quickSelect = new QuickSelectDoubleArray();
		double [] values = new double[] {6, 8, 7, 5, 3, 0, 9, 1, 2, 4};

		System.out.println("original:\t" + toString(values));

		if(quickSelect.containsNaN(values)) {
			throw new IllegalArgumentException("QuickSelect doesn't work with arrays containing NaN values");
		}

		// Puts the 6th largest value (which is 4.0) at values[5] and all larger values below it.
		quickSelect.select(values, 5);
		System.out.println("select(5):\t" + toString(values));

		// Puts the 3rd largest value (which is 7.0) at values[2] and all larger values below it.
		quickSelect.select(values, 2);
		System.out.println("select(2):\t" + toString(values));
	}


	static String toString(double[] values) {
		StringBuilder sb = new StringBuilder("{");
		for(double v : values) {
			sb.append(v).append(", ");
		}
		return sb.replace(sb.length()-2, sb.length(), "}").toString();
	}
}
