# Overview
This code implements two algorithms: QuickSelect and QuickMedian.   
QuickSelect can be used to find the <code>k</code>-th largest value   
in a sequence and the <code>k</code> largest values in expected O(n)   
time.  QuickMedian can be used to find the median value of a sequence   
in expected O(n) time.   
  
There are short examples in the "examples" package and a short runtime   
analysis, below.   
  
Currently, the implementations only works on double arrays.  It should   
be easy to port to other and arbitrary sequences; I'd be willing to    
spend the time on it if someone expresses interest.   
  
Brian Romanowski   


# Empirical Runtime Evaluation
There are plots of runtime evaluation data and an analysis writeup in   
the <code>doc/</code> directory.  The gist of the analysis is that   
QuickMedian's use of QuickSelect allows it to find the median of an   
array in linear time, with a low constant factor, and that it clearly   
outperforms a sort-based median algorithm.  
  
See the analysis.txt file for more details, but in the image below,  
the increasing lines connect the reddish, circle dots of the runtime   
data points of SortMedian.  Each of these series is a sequence of   
SortMedian runs over an array that was subject to a different sorting   
condition.   
  
The bottom, relatively flat lines, connect the blueish, square   
dots of the runtime data points of QuickMedian.  Each of these series   
is a sequence of QuickMedian using a different pivot picking method.  
  
Sorry for the poor choice of colors and the relatively weak   
explanations; this writeup was a bit rushed.  I plan to edit the   
analysis.txt file for language and style.  

<a href="http://github.com/romanows/QuickSelect/raw/master/doc/QuickSelectEval_large1.png"><img src="http://github.com/romanows/QuickSelect/raw/master/doc/QuickSelectEval_preview1.png" alt="Thumbnail image linking to the full image that analyzes QuickMedian vs. SortMedian runtimes.  See analysis.txt for a writeup of the evaluation data." /></a>


# Bugs
Please file bug reports and bug fixes in the GitHub issue tracker.   
Feel free to shoot the author an email if you find this software  
useful, it would make his day.  


# LICENSE
This software is released under the Simplified BSD License, see   
LICENSE.TXT.  
