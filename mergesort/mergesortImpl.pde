/**
 * natural mergesort implementation
 * ================================
 */
// divide set into two sets
static int[] mergesort(int[] a)
{
  if(a.length <= 1)
  {
    return a;
  }
  else
  {
    int cut = a.length/2;
    int[] left = subset(a,0,cut);
    left = mergesort(left);
    int[] right = subset(a,cut);
    right = mergesort(right);
    return merge(left,right);
  }
}
// merge sets together
static int[] merge(int[] left, int[] right)
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
  int[] newlist = new int[left.length + right.length];
  assert(newlist.length>=2);
  // "pointer" of lists
  int i=0,j=0,k=0;
  // the "sorting" part
  do
  {  
    if(left[j]>right[k]) 
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