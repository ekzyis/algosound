/**
 * Implementation of merging two subsets together.
 * ===============================================
 * @author ekzyis
 * @date December 2018
 */

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
  // mark the two subsets as merging
  markMerging(e,l,r);
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
  for(Element el : lss) leftRecursion.add(el);
  for(Element el : rss) leftRecursion.add(el);
  // return one recursion: remove last character which is 'm'
  recursionStack=recursionStack.substring(0,recursionStack.length()-1);
  /**
   * Now leave recursion like splitting not possible.
   */
  return visualLeaveRecursion();
}

// mark subsets specified by l and r as merging
void markMerging(Element[] e,int[] l, int[] r)
{
  int i=0;
  int len = l[1]+r[1];
  while(i<len)
  {
    e[l[0]+i].merging = true;
    unmarkMe.add(e[l[0]+i]);
    i++;
  } 
}