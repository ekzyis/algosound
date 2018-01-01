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
  if(!unmarkMe.isEmpty())
  {
    for(int i=0;i<unmarkMe.size();++i)
    {
      unmarkMe.get(i).marked = false;
    }  
  }
  // check if merging is planned.
  if(currentLevel=='m')
  {
    // get starting index and length from stack
    int[] l = leftStack.get(leftStack.size()-1);
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
    return leaveRecursion();
  }
}

/**
 * This function splits the set e into two subsets.
 * The start and length of each subsets is pushed onto stack.
 * 
 * Returns parameters for next frame.
 */
int[] visualSplit(Element[] e, int s, int len)
{
  // declare array for next-frame-parameters
  int[] nextParam = new int[2];
  // calculate length of cut
  int cutLen = (len/2);
  println("cut="+cutLen); 
  // create subsets for debugging
  Element[] leftss = (Element[])(subset(e,s,cutLen));
  print("leftss=");printarr(leftss);
  Element[] rightss = (Element[])(subset(e,s+cutLen,len-cutLen));
  print("rightss=");printarr(rightss);
  // add to stacks (only this is needed)
  int[] addleftstack = {s,cutLen};
  leftStack.add(addleftstack);
  int[] addrightstack = {s+cutLen,len-cutLen};
  rightStack.add(addrightstack);
  // go left next frame.
  println("going left next frame.");
  recursionStack+="l";
  // mark cut index in this frame
  e[s+cutLen].mark(mark);
  unmarkMe.add(e[s+cutLen]);
  /**
   * Since we go left next frame, next parameters
   * will be indizes of left subset.
   */
   nextParam[0] = s; nextParam[1] = cutLen; 
   print("new leftStack=");printlist(leftStack);
   print("new rightStack=");printlist(rightStack);
   println("new recursionLevel="+recursionStack);
   println("returning: "+nextParam[0]+", "+nextParam[1]);
   return nextParam;
}

/**
 * This function merges the two subsets of e, defined by
 * the startindex of the left subset by l[0] and length l[1]
 * and the startindex of the right subset by r[0] and length r[1].
 * It assumes the two subsets are sorted.
 *
 * Returns parameters for next frame.
 */
int[] visualMerge(Element[] e, int[] l, int[] r)
{
  println("---merging---");
  // merge the last two subsets
  print("l=");printarr(l);
  print("r=");printarr(r);
  // recreate subsets
  Element[] lss = (Element[])(subset(e,l[0],l[1]));
  print("lss=");printarr(lss);
  Element[] rss = (Element[])(subset(e,r[0],r[1]));
  print("rss=");printarr(rss);
  // create list to contain sorted elements
  Element[] newlist = new Element[0];
  // iterator for subsets
  int j=0,k=0;
  // the merging part
  do
  {
    if(lss[j].value > rss[k].value)
    {
      /**
      * Value of element in right list is smaller.
      * Since elements with a lesser value should be 
      * left, the coordinates have to swap but swapping the coordinates 
      * breaks the assumption of mergesort that both subsets
      * are already sorted (according to value AND coordinates!) 
      * thus valid sorting is no longer guaranteed.
      * Before progressing, sorting of both subsets is needed
      * which is done by restoreOrder()
      */
      lss[j].swap(rss[k],Element.COORDINATES);
      newlist = (Element[])(append(newlist,rss[k]));
      restoreOrder(lss,j);
      restoreOrder(rss,k);
      k++;
    }
    else
    {
      newlist = (Element[])(append(newlist,lss[j]));
      j++;
    }
  }while(j<lss.length && k<rss.length);
  // put the rest of the elements in newlist
  if(j<lss.length)
  {
    for(;j<lss.length;j++) newlist = (Element[])(append(newlist,lss[j]));
  }
  else if(k<rss.length)
  {
    for(;k<rss.length;k++) newlist = (Element[])(append(newlist,rss[k]));
  }
  print("newlist=");printarr(newlist);
  /**
   * Newlist contains now merged elements for elements
   * between startindex of left subset and endindex of right subset.
   */
  // update elements
  int it = 0;
  for(int i=l[0];i<(r[0]+r[1]);++i)
  {
    e[i] = newlist[it];
    it++;
  }
  print("updated e=");printarr(e);
  // pop the used subsets from stacks
  leftStack.remove(leftStack.size()-1);
  rightStack.remove(rightStack.size()-1);
  // after merging, recursion level is done.
  // return one recursion: remove last character which is 'm'
  recursionStack=recursionStack.substring(0,recursionStack.length()-1);
  /**
   * Now leave recursion like splitting not possible.
   */
  return leaveRecursion();
}

/**
 * This function simulates leaving the recursion
 * by manipulating the recursionStack string.
 *
 * Returns parameters for next frame.
 */
int[] leaveRecursion()
{
  int[] nextParam = new int[2];
  // delete last character
  char currentLevel = recursionStack.charAt(recursionStack.length()-1);
  recursionStack = recursionStack.substring(0,recursionStack.length()-1);
  switch(currentLevel)
  {
    case 'l': 
      /**
       * The indizes of the right subset of the underlaying recursion level
       * is the subset which got saved together with current left subset 
       * (which now has size 1).
       * Every time a set gets divided into two subsets.
       * This means, the corresponding subset is at the same index;
       * just in rightStack instead of leftStack (et vice versa).
       */
      int correspondingSetIndex = recursionStack.length()-1;
      // last time we went into the left subset. next frame right subset.
      println("going right next frame.");
      recursionStack+="r";
      nextParam = rightStack.get(correspondingSetIndex);
      break;
    case 'r':
     /** 
      * Last time we went into the right subset.
      * This means, we can now merge with our left subset counterpart
      * in the next frame.
      */
      println("merging next frame.");
      recursionStack+="m";
      /**
       * In next frame, two subsets will be merged.
       * The parameters indicate that the 
       * subsets can be popped from stack after merging.
       */
      nextParam[0] = -1;
      nextParam[1] = -1;
      break;
    case 'm': 
    // this should not happen
      assert(false); 
      break;
  }
  print("new leftStack=");printlist(leftStack);
  print("new rightStack=");printlist(rightStack);
  println("new recursionLevel="+recursionStack);
  println("returning: "+nextParam[0]+", "+nextParam[1]);
  // return parameters for next frame.
  return nextParam;
}