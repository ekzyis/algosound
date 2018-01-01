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
 * When the function gets called for its first time, 
 * it will be checked if the given (sub)set is
 * big enough for splitting into two subsets:
 *   - If yes, mark the subset in which we are going 
 *   (which will be left), add it to the stack 
 *   and return startindex and length of next frames subset.
 *   - If not, the subset is sorted. Do nothing.
 * Second frame consists of checking again for splitting:
 *   - If possible, continue as in first frame.
 *   - If not possible, we have a subset of size 1.
 *     In recursive mergesort, the recursion would here start to return.
 *     This means for us here, that we return one level of recursion,
 *     and we should go right next frame.
 * Every frame from now on then consists of:
 *   - check if a merge has been planned.
       - If yes, continue merging.
       - If not, check for splitting.
 *   - check if splitting is possible.
 *     - If yes, split, push stacks, and return left subset parameters.
 *     - If not, check where in recursion tree you are currently. 
 *       - you are currently in a left subtree -> go right subtree next frame
 *       - you are currently in a right subtree -> merge subtrees next frame
 *
 * @return     values for $s and $end for next frame
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
String recursionLevel = "0";
//marking color for cut index
color mark = color(128,64,255);
int[] visualMergesortStep(Element[] e, int s, int len)
{
  print("visualMergesortStep(): e=");printarr(e); 
  println("s="+s+", len="+len);
  print("leftStack=");printlist(leftStack);
  print("rightStack=");printlist(rightStack);
  // get current recursion level
  println("recursionLevel="+recursionLevel);
  println("recursionLevel.length()="+recursionLevel.length());
  char currentLevel = recursionLevel.charAt(recursionLevel.length()-1); //<>//
  // declare array for next-frame-parameters
  int[] nextParam = new int[2];
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
    println("---merging---");
    // merge the last two subsets
    // get starting index and length from stack
    int[] l = leftStack.get(leftStack.size()-1);
    int[] r = rightStack.get(rightStack.size()-1);
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
    recursionLevel=recursionLevel.substring(0,recursionLevel.length()-1);
    // save correspondingIndex to get new parameters from stack
    int correspondingIndex = recursionLevel.length()-2;
    /**
     * Replace last character since we don't want
     * to do the same as when we were last time in this recursion level
     */
    char lastChar = recursionLevel.charAt(recursionLevel.length()-1);
    recursionLevel=recursionLevel.substring(0,recursionLevel.length()-1);
    switch(lastChar)
    {
      // when we previously went into left subset, go into right subset next frame
      case 'l': recursionLevel+="r";
        break;
        // when we previously went into right subset, merge next frame
      case 'r': recursionLevel+="m";
        break;
        // this should not happen
      case 'm': assert(false);
    }
    // get parameters from stack
    nextParam = rightStack.get(correspondingIndex);
    print("new leftStack=");printlist(leftStack);
    print("new rightStack=");printlist(rightStack);
    println("new recursionLevel="+recursionLevel);
    println("returning: "+nextParam[0]+", "+nextParam[1]);
    return nextParam;
  }
  // is given subsets size bigger than 1?
  if(len>1) 
  {
    println("---splitting---");
    // splitting is possible!
    // calculate cut index
    int cut = (len/2);
    println("cut="+cut); 
    // create subsets for debugging
    Element[] leftss = (Element[])(subset(e,s,cut));
    print("leftss=");printarr(leftss);
    Element[] rightss = (Element[])(subset(e,s+cut,len-cut));
    print("rightss=");printarr(rightss);
    // add to stacks (only this is needed)
    int[] addleftstack = {s,cut};
    leftStack.add(addleftstack);
    int[] addrightstack = {s+cut,len-cut};
    rightStack.add(addrightstack);
    // go left next frame.
    println("going left next frame.");
    recursionLevel+="l";
    // mark cut index in this frame
    e[cut].mark(mark);
    unmarkMe.add(e[cut]);
    /**
     * Since we go left next frame, next parameters
     * will be indizes of left subset.
     */
    nextParam[0] = s;
    nextParam[1] = cut;
  }
  else
  {
    println("---leaving recursion---");
    // no more splitting possible! go one recursion level down.
    // delete last character
    recursionLevel = recursionLevel.substring(0,recursionLevel.length()-1);
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
        int correspondingSetIndex = recursionLevel.length()-1;
        // last time we went into the left subset. next frame right subset.
        println("going right next frame.");
        recursionLevel+="r";
        nextParam = rightStack.get(correspondingSetIndex);
        break;
      case 'r':
       /** 
        * Last time we went into the right subset.
        * This means, we can now merge with our left subset counterpart
        * in the next frame.
        */
        println("merging next frame.");
        recursionLevel+="m";
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
  }
  print("new leftStack=");printlist(leftStack);
  print("new rightStack=");printlist(rightStack);
  println("new recursionLevel="+recursionLevel);
  println("returning: "+nextParam[0]+", "+nextParam[1]);
  // return parameters for next frame.
  return nextParam;
}