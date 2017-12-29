// make one mergesort step
static Element[] mergesort(Element[] e)
{
  if(e.length <= 1) return e;
  else
  {
    int cut = e.length/2;
    Element[] left = (Element[])(subset(e,0,cut));
    left = mergesort(left);
    Element[] right = (Element[])(subset(e,cut));
    right = mergesort(right);
    return merge(left,right);
  }
}
// make one merge step
static Element[] merge(Element[] left, Element[] right)
{
  /** 
   * if one list is empty, return the other one
   * since we can assume it's already sorted
   */
  if(left.length == 0) return right;
  else if(right.length == 0) return left;
  /** 
   * create new list big enough to hold all elements 
   * of both lists
   */
  Element[] newlist = new Element[left.length + right.length];
  assert(newlist.length>=2);
  // "pointer" of lists
  int i=0,j=0,k=0;
  // the "sorting" part
  do
  {  
    if(left[j].value>right[k].value) 
    {
      newlist[i] = right[k];
      k++;
    }
    else 
    {
      newlist[i] = left[j];
      j++;
    }
    i++;
  }while(j<left.length && k<right.length);
  // put the rest of the elements in newlist
  if(j<left.length)
  {
    for(;j<left.length;++j)
    {
      newlist[i] = left[j];
      i++;
    }
  }
  else if(k<right.length)
  {
    for(;k<right.length;++k)
    {
      newlist[i] = right[k];
      i++;
    }
  }
  else
  {
    // this should never be reached
    assert(false);
  }
  // newlist should be sorted now
  return newlist;
}