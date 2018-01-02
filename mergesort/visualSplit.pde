/**
 * Implementation of splitting given (sub)sets into two subsets.
 * =============================================================
 * @author ekzyis
 * date January 2018
 */
 
/**
 * This function splits the set e into two subsets.
 * The start and length of each subsets is pushed onto stack.
 * The element between those subsets gets marked.
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