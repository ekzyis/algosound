/**
 * Implementation of visualization steps for mergesort.
 * ====================================================
 * @author ekzyis
 * @date December 2017
 */
 
/**
 * Changes $e to visualize one step in mergesort.
 *
 * This non-recursive function simulates the behaviour of mergesort 
 * to divide all sets which are langer than 1 into two subsets.
 * It consists of cleaning the old frame, calculating new frame
 * and preparing for the next frame.
 * The parameters $e, $s and $len are the given (sub)set (=$e),
 * and the start(=$s) and length(=$len) of the next subset for this frame.
 * 
 * Idea
 * ====
 *
 * Before calling this function, a stack each for the left subsets and the
 * right subsets gets initialized. The ArrayLists will be handled as stacks.
 * The elements who should be unmarked next frame also get a place to be saved.
 * When the function gets called, it will follow these steps:
 * (0. print current stacks and recursion stack.)
 * 1. check if merging is planned.
 *    - if yes, call function visualMerge()
 *    - if not, continue
 * 2. check if given subset can be splitted.
 *    - if yes, call visualSplit()
 *    - if not, call leaveRecursion()
 * 4. In each step, the parameters with which to call this function
 * next frame gets returned.
 * Return parameters for next frame.
 */
// elements to be unmarked next frame
ArrayList<Element> unmarkMe = new ArrayList<Element>();
/** 
 * Simulating the recursion stack.
 * List will save start index and length of subsets.
 */
ArrayList<int[]> leftStack = new ArrayList<int[]>();
ArrayList<int[]> rightStack = new ArrayList<int[]>();
/**
 * keeping track of recursion level.
 * l means splitting left subset, r means splitting right subset.
 * 0 is the start array, where we are starting to sort from.
 */
String recursionStack = "0";
//marking color for cut index
color mark = color(128,64,255);
int[] visualMergesortStep(Element[] e, int s, int len)
{
  print("visualMergesortStep(): e=");printarr(e); 
  println("s="+s+", len="+len);
  print("leftStack=");printlist(leftStack);
  print("rightStack=");printlist(rightStack);
  println("recursionLevel="+recursionStack);
  println("recursionLevel.length()="+recursionStack.length());
  // get current recursion level
  char currentLevel = recursionStack.charAt(recursionStack.length()-1); //<>//
  // unmark previous elements
  clearMarkers();
  
  // check if merging is planned.
  if(currentLevel=='m') //<>//
  {
    // get starting index and length from stack
    int[] l = leftStack.get(leftStack.size()-1); //<>//
    int[] r = rightStack.get(rightStack.size()-1);
    // set parameters for next frame
    return visualMerge(e,l,r);
  }
  // is given subsets size bigger than 1?
  else if(len>1) 
  {
    // splitting is possible!
    println("---splitting---");
    return visualSplit(e,s,len);
  }
  else
  {
    // no more splitting possible! go one recursion level down.
    println("---leaving recursion---");
    return visualLeaveRecursion();
  }
}

void clearMarkers()
{
  if(!unmarkMe.isEmpty())
  {
    for(int i=0;i<unmarkMe.size();++i)
    {
      unmarkMe.get(i).marked = false;
    }  
  }
}