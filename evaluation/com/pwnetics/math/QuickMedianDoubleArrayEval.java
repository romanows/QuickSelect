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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.pwnetics.alg.QuickSelectDoubleArray;
import com.pwnetics.alg.QuickSelectDoubleArray.PivotMethod;

public class QuickMedianDoubleArrayEval {
	private static final Random random = new Random(42);

	protected static enum SortingCondition {UNSORTED, SORTED, MIDDLE_UNSORTED, REVERSE_SORTED, REVERSE_MIDDLE_UNSORTED};

	protected static class Condition {
		public int size;
		public boolean duplicate;
		public SortingCondition sorting;
		public EnumSet<QuickSelectDoubleArray.PivotMethod> pivotMethod;

		public Condition(int size, boolean duplicate, SortingCondition sorting, EnumSet<QuickSelectDoubleArray.PivotMethod> pivotMethod) {
			this.size = size;
			this.duplicate = duplicate;
			this.sorting = sorting;
			this.pivotMethod = pivotMethod;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(size).append("\t");
			sb.append(duplicate).append("\t");
			sb.append(sorting).append("\t");
			sb.append(pivotMethod);
			return sb.toString();
		}
	}

	private static double [] intArray(int n, boolean duplicates) {
		List<Double> x = new ArrayList<Double>(n);
		if(!duplicates) {
			for(int i=0; i<n; i++) {
				x.add((double)i);
			}
		} else {
			int m = n/2;
			for(int i=0; i<m; i++) {
				x.add((double)i);
			}
			for(int i=0; i<n-m; i++) {
				x.add((double)i);
			}
		}
		Collections.shuffle(x, random);

		double [] y = new double[n];
		for(int i=0; i<n; i++) {
			y[i] = x.get(i);
		}
		assert(y.length == n);
		return y;
	}


	public static void main(String[] args) {
		int [] sizes = new int[] {100, 101, 1000, 1001, 10000, 10001, 100000, 100001};
		List<EnumSet<QuickSelectDoubleArray.PivotMethod>> pivotMethods = new ArrayList<EnumSet<QuickSelectDoubleArray.PivotMethod>>();
		pivotMethods.add(EnumSet.noneOf(QuickSelectDoubleArray.PivotMethod.class));
		pivotMethods.add(EnumSet.of(PivotMethod.MEDIAN_OF_THREE));
		pivotMethods.add(EnumSet.of(PivotMethod.RANDOM));
		pivotMethods.add(EnumSet.of(PivotMethod.MEDIAN_OF_THREE, PivotMethod.RANDOM));

		List<Condition> conditions = new ArrayList<QuickMedianDoubleArrayEval.Condition>();
		for(int size : sizes) {
			for(boolean duplicate : new Boolean[] {true, false}) {
				for(SortingCondition sorting : EnumSet.allOf(SortingCondition.class)) {
					for(EnumSet<QuickSelectDoubleArray.PivotMethod> pivotMethod : pivotMethods) {
						conditions.add(new Condition(size, duplicate, sorting, pivotMethod));
					}
				}
			}
		}


		Map<Condition, Long> conditionToTime = new HashMap<QuickMedianDoubleArrayEval.Condition, Long>();
		Map<Condition, Long> sortedConditionToTime = new HashMap<QuickMedianDoubleArrayEval.Condition, Long>();

		for(int trial=0; trial < 100; trial++) {
			System.out.print(".");
			Collections.shuffle(conditions);
			for(Condition c : conditions) {
				List<double []> valuesList = new ArrayList<double[]>();
				for(int rep=0; rep<1e6/c.size; rep++) {  // Need to do many arrays, otherwise system clock isn't fast enough to catch median computation
					double [] values = intArray(c.size, c.duplicate);

					switch (c.sorting) {
					case UNSORTED:
						// Pass, they're stored unsorted
						break;
					case SORTED:
						Arrays.sort(values);
						break;
					case MIDDLE_UNSORTED:
						Arrays.sort(values,0,(int)(values.length * 0.45));
						Arrays.sort(values,(int)(values.length * 0.55), values.length);
						break;
					case REVERSE_SORTED:
						for(int i=0; i<values.length; i++) {
							values[i] = -values[i];
						}
						Arrays.sort(values);
						for(int i=0; i<values.length; i++) {
							values[i] = -values[i];
						}
						break;
					case REVERSE_MIDDLE_UNSORTED:
						for(int i=0; i<values.length; i++) {
							values[i] = -values[i];
						}
						Arrays.sort(values,0,(int)(values.length * 0.45));
						Arrays.sort(values,(int)(values.length * 0.55), values.length);
						for(int i=0; i<values.length; i++) {
							values[i] = -values[i];
						}
						break;
					}
					valuesList.add(values);
				}

				QuickSelectDoubleArray qs = new QuickSelectDoubleArray(QuickSelectDoubleArray.DEFAULT_IS_DESCENDING, c.pivotMethod, new Random(43), QuickSelectDoubleArray.DEFAULT_MEDIAN_OF_THREE_THRESHOLD);
				QuickMedianDoubleArray qm = new QuickMedianDoubleArray(qs);

				double m = 0.0;
				long elapsedTime = new Date().getTime();
				for(double [] values : valuesList) {
					m += qm.median(values);
				}
				elapsedTime = new Date().getTime() - elapsedTime;

				if(!conditionToTime.containsKey(c)) {
					conditionToTime.put(c, 0L);
					sortedConditionToTime.put(c, 0L);
				}
				conditionToTime.put(c, conditionToTime.get(c) + elapsedTime);

				double sm = 0.0;
				elapsedTime = new Date().getTime();
				for(double [] values : valuesList) {
					sm += SortingMedianDoubleArray.median(values);
				}
				elapsedTime = new Date().getTime() - elapsedTime;
				sortedConditionToTime.put(c, sortedConditionToTime.get(c) + elapsedTime);

				if(m != sm) {  // FindBugs: not a bug, if the two medians are not exactly equivalent, one of the methods is in error
					System.out.println("error: " + m + ", " + sm + " : " + c.toString());
				}
			}
		}

		System.out.println("\n\n");
		for(Entry<Condition, Long> entry : conditionToTime.entrySet()) {
			System.out.println(entry.getKey().toString() + "\t" + entry.getValue() + "\tQUICKMEDIAN");
			System.out.println(entry.getKey().toString() + "\t" + sortedConditionToTime.get(entry.getKey()) + "\tSORTED");
		}
	}
}
