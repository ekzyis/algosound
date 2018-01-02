/**
 * Implementation of returning recursion by manipulating 
 * recursionStack of type String.
 * =====================================================
 * @author ekzyis
 * date January 2018
 */
 
/**
 * This function simulates leaving the recursion
 * by analyzing and manipulating the recursionStack string.
 *
 * Returns parameters for next frame.
 */
int[] visualLeaveRecursion()
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
  println("new recursionStack="+recursionStack);
  println("returning: "+nextParam[0]+", "+nextParam[1]);
  // return parameters for next frame.
  return nextParam;
}