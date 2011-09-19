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

import com.pwnetics.math.QuickMedianDoubleArray;

/**
 * Example usage of {@link QuickMedianDoubleArray}.
 *
 * <p>Example output follows:</p>
 * <pre>
 * The median of {6.0, 8.0, 7.0, 5.0, 3.0, 0.0, 9.0, 1.0, 2.0, 4.0, 10.0} is:	5.0
 * The median of {6.0, 8.0, 7.0, 5.0, 3.0, 0.0, 9.0, 1.0, 2.0, 4.0} is:		4.5
 * </pre>
 *
 * @author romanows
 */
public class MedianExample {
	public static void main(String[] args) {
		QuickMedianDoubleArray quickMedian = new QuickMedianDoubleArray();
		double [] values;

		values = new double[] {6, 8, 7, 5, 3, 0, 9, 1, 2, 4, 10};
		System.out.println("The median of " + SelectExample.toString(values) + " is:\t" + quickMedian.median(values));

		values = new double[] {6, 8, 7, 5, 3, 0, 9, 1, 2, 4};
		System.out.println("The median of " + SelectExample.toString(values) + " is:\t\t" + quickMedian.median(values));
	}
}
