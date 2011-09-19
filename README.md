# Overview
This code implements two algorithms: QuickSelect and QuickMedian.   
QuickSelect can be used to find the k-th largest value in a sequence   
and the k largest values in expected O(n) time.  QuickMedian can be   
used to find the median value of a sequence in expected O(n) time.   
However, both algorithms are worst-case O(n^2) time.  
  
The <code>examples</code> package contains short examples and <code>doc/analysis.txt</code>   
contains a writeup of an empirical runtime evaluation.   
  
This implementation only works on double arrays.  Let me know if   
you're interested in seeing versions for other array types and   
collections, and that will get done a bit sooner.   
  
Brian Romanowski   
romanows@gmail.com  


# Empirical Runtime Evaluation
There are plots of runtime evaluation data and an analysis writeup at   
<code>doc/analysis.txt</code>.  The image below shows a number of things,  
but the gist is that finding the median using QuickMedian is indeed   
better than finding the median using a sort-based method, all other   
things being equal.   
  
Click on the image to view a larger, annotated version.  
  
<a href="http://github.com/romanows/QuickSelect/raw/master/doc/QuickSelectEval_large1.png"><img src="http://github.com/romanows/QuickSelect/raw/master/doc/QuickSelectEval_preview1.png" alt="Thumbnail image linking to the full image that analyzes QuickMedian vs. SortMedian runtimes.  See analysis.txt for a writeup of the evaluation data." /></a>


# Bugs
Please file bug reports and bug fixes in the GitHub issue tracker.   
Feel free to shoot the author an email if you find this software  
useful, it would make his day.  


# LICENSE
This software is released under the Simplified BSD License, see   
LICENSE.TXT.  
