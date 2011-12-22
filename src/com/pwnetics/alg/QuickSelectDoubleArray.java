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

import java.util.EnumSet;
import java.util.Random;


/**
 * Implements the <a href="http://en.wikipedia.org/wiki/Selection_algorithm#Partition-based_general_selection_algorithm">QuickSelect</a> algorithm.
 * QuickSelect is a kind of partial sort that runs in expected <code>O(n)</code> time and worst-case <code>O(n^2)</code> time, where <code>n</code> is the length of the input.
 * It is particularly helpful for quickly finding the n-best values in a beam search or calculating the median.
 *
 * <p>
 * Given an array X, this algorithm rearranges elements in X to produce an array Y such that <code>Y[i] == sort(X)[i]</code> for some user-specified index <code>i</code>.
 * In addition, <code>Y[:i].containsAll(sort(X)[:i])</code> is true as is <code>Y[i:].containsAll(sort(X)[i:])</code>, although it is not guaranteed that <code>Y[j] == sort(X)[j]</code> for any <code>j != i</code>.
 * </p>
 *
 * <p>
 * These properties allow one to pick out the median value quickly, with something vaguely like <code>QuickSelect(X,X.length/2)</code> when X has an odd number of elements.
 * The top N values in X are placed at the beginning with <code>QuickSelect(X,N-1)</code>, although the top N values will not be in any particular order.
 * </p>
 *
 * @author romanows
 */
public class QuickSelectDoubleArray {

	/**
	 * We use quicksort-like pivot value picking heuristics.
	 * {@link QuickSelectDoubleArray#DEFAULT_PIVOT_METHOD} is reasonable pivot method specification for the average dataset.
	 *
	 * <ul>
	 *   <li>A <code>RANDOM</code> pivot method will randomly choose a value in the array to be the pivot each time</li>
	 *   <li>A <code>MEDIAN_OF_THREE</code> pivot method will deterministically sample 3 values from pre-defined and constant indexes in the array and use the median as the pivot</li>
	 *   <li>A <code>RANDOM | MEDIAN_OF_THREE</code> combined pivot method will randomly choose 3 values and use the median as the pivot</li>
	 *   <li>Using none of these (<code>EnumSet.noneOf(PivotMethod)</code>) will always choose the middle value in the array as the pivot</li>
	 * </ul>
	 *
	 * <p>The <code>RANDOM | MEDIAN_OF_THREE</code> method is probably the most robust to accidental performance-killer arrays, so this is set as the <code>DEFAULT</code>.
	 * It requires more computation to choose the pivot, but this should wash out in savings from good pivot locations.</p>
	 */
	public static enum PivotMethod {RANDOM, MEDIAN_OF_THREE};

	/** Default {@link PivotMethod} used when partitioning */
	public static final EnumSet<PivotMethod> DEFAULT_PIVOT_METHOD = EnumSet.of(PivotMethod.RANDOM, PivotMethod.MEDIAN_OF_THREE);

	/** Default median-of-three threshold value; the median-of-three pivot method is disabled when the number of pivot candidate elements is less than the median-of-three threshold number */
	public static final int DEFAULT_MEDIAN_OF_THREE_THRESHOLD = 24;

	/** Default sorting order */
	public static final boolean DEFAULT_IS_DESCENDING = true;

	/** Pivot picking method */
	private final EnumSet<PivotMethod> pivotMethod;

	/** Whether the sorting order is descending or ascending */
	private final boolean isDescending;

	/** Random number generator used for the randomized pivot picking methods */
	private final Random random;

	/** Median-of-three pivot method is disabled when the number of pivot candidate elements is less than the median-of-three threshold number */
	private final int medianOfThreeThreshold;


	/**
	 * Constructor.
	 * Creates a QuickSelect object that places the largest values before the selected index.
	 */
	public QuickSelectDoubleArray() {
		this(DEFAULT_IS_DESCENDING);
	}


	/**
	 * Constructor.
	 * Creates a Partitioner with reasonable defaults.
	 * @param isDescending if true, the largest array values are placed before the selected index; if false, the smallest array values are placed before the selected index.
	 */
	public QuickSelectDoubleArray(boolean isDescending) {
		this(isDescending, DEFAULT_PIVOT_METHOD, null, DEFAULT_MEDIAN_OF_THREE_THRESHOLD);
	}


	/**
	 * Constructor.
	 * @param isDescending if true, the largest array values are placed before the selected index; if false, the smallest array values are placed before the selected index.
	 * @param pivotMethod specifies the pivot picking method
	 * @param random random number generator when randomizing pivot picking; if null, constructor will create a new Random() to use
	 * @param medianOfThreeThreshold median-of-three pivot method will be used for array subsets greater than or equal to this value, otherwise a (simpler) pivot picking procedure will be used.
	 */
	public QuickSelectDoubleArray(boolean isDescending, EnumSet<PivotMethod> pivotMethod, Random random, int medianOfThreeThreshold) {
		this.pivotMethod = pivotMethod;
		this.isDescending = isDescending;
		this.medianOfThreeThreshold = medianOfThreeThreshold;

		if(pivotMethod.contains(PivotMethod.RANDOM) && random == null) {
			this.random = new Random();
		} else {
			this.random = random;
		}
	}


	/**
	 * Get the median value of three values from a given array.
	 * @param values array containing values
	 * @param idx1 index of a median candidate
	 * @param idx2 index of a median candidate
	 * @param idx3 index of a median candidate
	 * @return the index of the median with respect to the given candidates
	 */
	protected int medianIdx(double [] values, int idx1, int idx2, int idx3) {
		if(values[idx1] <= values[idx2]) {
			if(values[idx3] >= values[idx2]) {
				return idx2;
			} else if(values[idx1] >= values[idx3]) {
				return idx1;
			} else {
				return idx3;
			}
		} else {
			if(values[idx2] >= values[idx3]) {
				return idx2;
			} else if(values[idx3] >= values[idx1]) {
				return idx1;
			} else {
				return idx3;
			}
		}
	}


	/**
	 * Choose the pivot to be the median-of-three samples from a subset of the given array.
	 *
	 * @param values array whose elements will be reordered
	 * @param startIdx starting index of the array subset
	 * @param subArraySize number of elements in the array subset
	 * @return median-of-three pivot index
	 */
	protected int getPivotIdxMedianThree(double [] values, int startIdx, int subArraySize) {
		// Picking elements at indices of about 0.25, 0.5, and 0.75.
		int i1 = startIdx + (subArraySize >>> 2);
		int i2 = startIdx + (subArraySize >>> 1);
		int i3 = startIdx + ((3 * subArraySize) >>> 2);

		return medianIdx(values, i1, i2, i3);
	}


	/**
	 * Choose the pivot to be the median-of-three samples from a subset of the given array.
	 *
	 * @param values array whose elements will be reordered
	 * @param startIdx starting index of the array subset
	 * @param subArraySize number of elements in the array subset
	 * @return median-of-three pivot index
	 */
	protected int getPivotIdxMedianThreeRandom(double [] values, int startIdx, int subArraySize) {
		return medianIdx(values, startIdx + random.nextInt(subArraySize), startIdx + random.nextInt(subArraySize), startIdx + random.nextInt(subArraySize));
	}


	/**
	 * Get the pivot index for a partition step using the configured pivot picking method.
	 * @param values array whose elements will be reordered
	 * @param startIdx starting index of the array subset
	 * @param subArraySize number of elements in the array subset
	 * @return pivot index
	 */
	protected int getPivotIdx(double [] values, int startIdx, int subArraySize) {
		if(pivotMethod.contains(PivotMethod.RANDOM)) {
			if(pivotMethod.contains(PivotMethod.MEDIAN_OF_THREE) && subArraySize >= medianOfThreeThreshold) {
				return getPivotIdxMedianThreeRandom(values, startIdx, subArraySize);
			} else {
				return startIdx + random.nextInt(subArraySize);
			}
		} else {
			if(pivotMethod.contains(PivotMethod.MEDIAN_OF_THREE) && subArraySize >= medianOfThreeThreshold) {
				return getPivotIdxMedianThree(values, startIdx, subArraySize);
			} else {
				return startIdx + (subArraySize >>> 1);
			}
		}
	}



	/**
	 * Implements the QuickSelect algorithm to reorder the given array.
	 * After calling this method, values[selectIdx] == sort(values)[selectIdx], and all of the elements before selectIdx will either be >= or <= values[selectIdx] depending on how the isDescending parameter was set upon construction.
	 * The given array is modified in-place.
	 * See the {@link QuickSelectDoubleArray} class documentation for more details.
	 *
	 * <p>An assert checks that the given array does not contain a NaN value element; if this assert is disabled, behavior is undefined.</p>
	 *
	 * <p>Note that if the values[selectIdx] value is duplicated in the array, then that value will occur at selectIdx but may also occur at some index i < selectIdx or i > selectIdx.</p>
	 *
	 * @param values array whose elements will be reordered, undefined behavior if an element is NaN (an {@link AssertionError} if assertions are enabled)
	 * @param selectIdx number of smallest values to move to the start of the array
	 */
	public void select(double [] values, int selectIdx) {
		if(values == null) {
			throw new IllegalArgumentException();
		}
		select(values, selectIdx, 0, values.length);
	}


	/**
	 * Implements the QuickSelect algorithm to reorder the given array.
	 * After calling this method, values[selectIdx] == sort(values)[selectIdx], and all of the elements before selectIdx will either be >= or <= values[selectIdx] depending on how the isDescending parameter was set upon construction.
	 * The given array is modified in-place.
	 * See the {@link QuickSelectDoubleArray} class documentation for more details.
	 *
	 * <p>An assert checks that the given array does not contain a NaN value element; if this assert is disabled, behavior is undefined.</p>
	 *
	 * <p>Note that if the values[selectIdx] value is duplicated in the array, then that value will occur at selectIdx but may also occur at some index i < selectIdx or i > selectIdx.</p>
	 *
	 * @param values array whose elements will be reordered, undefined behavior if an element is NaN (an {@link AssertionError} if assertions are enabled)
	 * @param selectIdx number of smallest values to move to the start of the array
	 * @param beginIndex starting index, inclusive, of array elements upon which this will operate (i.e., values[startIdx:endIdx])
	 * @param endIndex ending index, exclusive, of array elements upon which this will operate (i.e., values[startIdx:endIdx])
	 * @return a pair of indexes that specify correctly sorted array elements bounding the selectIdx OR -1 to denote that no index was encountered (we got lucky and found the selectIdx almost immediately).  When the indexes are available, then when {@link #isDescending}: sort(values)[ret[0]] >= sort(values)[selectIdx] >= sort(values)[ret[1]]
	 */
	public int [] select(double [] values, int selectIdx, int beginIndex, int endIndex) {
		assert(!containsNaN(values, beginIndex, endIndex));
		if(values == null || selectIdx < 0 || endIndex <= selectIdx || beginIndex > selectIdx || beginIndex < 0 || endIndex > values.length || beginIndex >= endIndex) {
			throw new IllegalArgumentException();
		}

		// During the QuickSelect process, we often partially sort the array several times before finding the selected element.
		// These two indexes will be set to the closest fixed pivot points encountered to the selectIdx.
		// This is mainly useful when running the median calculation on an even-length array, so the second middle point can be computed quickly.
		int beforeSelectIdx = -1;
		int afterSelectIdx = -1;


		// Partition the array around the pivot
		// After this operation, the pivot is in the correct sorted place
		// Then, we partition the left or right span relative to the pivot, depending whether the nth value of the beam lies to the left or right.

		endIndex--; // This becomes an inclusive ending index
		while(true) {
			// Pick a pivot value
			int subArraySize = endIndex - beginIndex + 1;
			int pivotIdx = getPivotIdx(values, beginIndex, subArraySize);
			double pivot = values[pivotIdx];

			// "Swap out pivot", but we've got the midpoint temporary variable holding the value, so we don't need to write it to values[endIdx].
			values[pivotIdx] = values[endIndex];

			// Place values that should go before the pivot, before the pivot
			int beforePivotInsertIdx = beginIndex;
			for(int i=beginIndex; i<endIndex; i++) {  // Notice we don't consider the endIdx, what contains the value we "swapped" with the midpoint
				if((isDescending && values[i] >= pivot) || (!isDescending && values[i] <= pivot)) {
					final double swap = values[i];
					values[i] = values[beforePivotInsertIdx];
					values[beforePivotInsertIdx] = swap;
					beforePivotInsertIdx++;
				}
			}

			// Swaps the pivot back into the correct location.
			// If we only need the beam, we can avoid these copies when the pivot will wind up outside the beam.
			// However, this doesn't seem to give any noticeable speed increases.
			values[endIndex] = values[beforePivotInsertIdx];
			values[beforePivotInsertIdx] = pivot;

			if(beforePivotInsertIdx < selectIdx) {
				beginIndex = beforePivotInsertIdx + 1;  // The pivot index wound up to-the-left-of the selection index: look right
				beforeSelectIdx = beforePivotInsertIdx;
			} else if(beforePivotInsertIdx > selectIdx) {
				endIndex = beforePivotInsertIdx - 1;  // The pivot index wound up to-the-right-of the selection index: look left
				afterSelectIdx = beforePivotInsertIdx;				
			} else {
				break;  // The pivot index wound up exactly-at the selection index; exit the loop
			}
		}

		return new int[] {beforeSelectIdx, afterSelectIdx};
	}


	/**
	 * Returns true if at least one value in the given array is NaN.
	 *
	 * @param values array to test
	 * @return true if at least one value in the given array is NaN; otherwise, false
	 */
	public boolean containsNaN(double [] values) {
		if(values == null) {
			throw new IllegalArgumentException();
		}
		return containsNaN(values, 0, values.length);
	}


	/**
	 * Returns true if at least one value in the given array is NaN.
	 *
	 * @param values array to test
	 * @param beginIndex starting index, inclusive, of array elements upon which this will operate (i.e., values[startIdx:endIdx])
	 * @param endIndex ending index, exclusive, of array elements upon which this will operate (i.e., values[startIdx:endIdx])
	 * @return true if at least one value in the given array is NaN; otherwise, false
	 */
	public boolean containsNaN(double [] values, int beginIndex, int endIndex) {
		if(values == null || beginIndex < 0 || endIndex > values.length || beginIndex >= endIndex) {
			throw new IllegalArgumentException();
		}

		for(int i=beginIndex; i < endIndex; i++) {
			if(Double.isNaN(values[i])) {
				return true;
			}
		}
		return false;
	}
}
