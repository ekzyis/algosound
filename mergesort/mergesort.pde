/**
 * Mergesort implementation for a set of elements.
 * ===============================================
 * @author ekzyis
 * @date December 2017
 */

// divide a set into two subsets
static Element[] mergesort(Element[] e)
{
  if(e.length <= 1) return e;
  else
  {
    // truncating division
    int cut = e.length/2;
    //println(cut);
    // make a subset of all elements left of cut-Index
    Element[] left = (Element[])(subset(e,0,cut));
    //print("left=");printarr(left); //<>//
    // divide this subset again into two subsets
    left = mergesort(left);
    // make a subset of all elements right of cut-Index
    Element[] right = (Element[])(subset(e,cut));
    //print("right=");printarr(right); //<>//
    // divide this subset also again into two subsets
    right = mergesort(right);
    // merge both subsets
    return merge(left,right);
  }
}

// merge two subsets assuming both are sorted
static Element[] merge(Element[] left, Element[] right)
{
  //println("--- entering merge ---");
  //print("left=");printarr(left);print("right=");printarr(right);
  /** 
   * if one list is empty, return the other one
   * since we can assume it's already sorted
   */
  if(left.length == 0) return right;
  else if(right.length == 0) return left;
  /**
   * create array to hold all elements //<>//
   */ //<>//
  Element[] newlist = new Element[0];
  // "pointer" of lists
  int j=0,k=0;
  // the "sorting" part
  do
  {  
    // compare the elements of both arrays
    if(left[j].value>right[k].value) 
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
      left[j].swap(right[k],Element.COORDINATES);
      newlist = (Element[])(append(newlist,right[k]));
      restoreOrder(left,j);
      restoreOrder(right,k);
      k++;
    }
    else
    {
      newlist = (Element[])(append(newlist,left[j]));
      j++;
    }
    //print("newlist=");printarr(newlist);print("updated left=");printarr(left);print("updated right=");printarr(right); //<>//
  }while(j<left.length && k<right.length);
  // put the rest of the elements in newlist
  if(j<left.length)
  {
    for(;j<left.length;j++) newlist = (Element[])(append(newlist,left[j]));
  }
  else if(k<right.length)
  {
    for(;k<right.length;k++) newlist = (Element[])(append(newlist,right[k]));
  }
  // newlist is sorted and contains all elements of both subsets
  //print("newlist=");printarr(newlist);
  return newlist;
}

// WE SHALL RESTORE ORDER BY SUPERIOR FORCE - General Marder
/**
 * restore order of a previously sorted set of elements 
 * in which element at index has been swapped during merging
 */
static void restoreOrder(Element[] list, int index)
{
  for(int i=index;i<list.length-1;++i)
  {
    Element e1 = list[i];
    Element e2 = list[i+1];
    if(e1.value > e2.value)
    {
      e1.swap(e2,Element.VALUES);
    }
    if(e1.x > e2.x)
    {
      e1.swap(e2,Element.COORDINATES);
    }
  }
}
 
 