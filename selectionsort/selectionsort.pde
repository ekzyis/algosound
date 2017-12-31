/**
 * Selectionsort implementation.
 * =================================
 * @author ekzyis
 * @date December 2017
 */
 
static void selectionsort(int[] a)
{
  int n = a.length;
  int start = 0;
  do{
    int minIndex = start;
    for(int i=minIndex+1;i<n;++i)
    {
      if(a[i]<minIndex)
      {
        minIndex = i;
      }
    }
    int tmp = a[minIndex];
    a[minIndex] = a[start];
    a[start] = tmp;
    start++;
  }while(start<n);
}

/**
 * do one for loop and swap the elements
 */
static void selectionsortStep(Element[] e, int index)
{
  assert(index<e.length);
  int minIndex = index;
  for(int i=minIndex+1;i<e.length;++i)
  {
    if(e[i].value<e[minIndex].value)
    {
      minIndex = i;
    }
  }
  e[index].swap(e[minIndex], Element.VALUES | Element.COLORS);
}

/**
 * mark the two elements with a red line
 * who are getting compared by selectionsort
 * in the for loop
 *
 * returns index of (updated) minIndex
 */
static int selectionsortCompare(Element[] e, int i, int minIndex)
{
  // unmark last compared element
  e[i-1].marked = false;
  e[i].marked = true;
  e[minIndex].marked = true;
  if(e[i].value<e[minIndex].value)
  {
    e[minIndex].marked = false;
    // return updated minIndex
    return i; 
  }
  else
  {
    return minIndex;
  }
}

/**
 * do one selectionsort step
 * and also change visuals according
 *
 * returns 1 when elements are sorted else 0
 */
 // index of element to swap with element at minIndex
int index = 0;
// minIndex is index of currently lowest found element
int minIndex=index;
// compareIndex is i in for loop
int compareIndex=minIndex+1; 
int visualSelectionsortStep()
{  
  // iterate through array each step until end
  if(compareIndex<e.length) {
    // search for correct minIndex
    minIndex = selectionsortCompare(e,compareIndex,minIndex);
    // increase compareIndex
    compareIndex++;
  }
  // do the swap operation
  else
  {
    // unmark last element and minIndex
    e[compareIndex-1].marked = false;
    e[minIndex].marked = false;
    // swap element at minIndex with element at index 
    selectionsortStep(e,index);
    // mark element now at index as sorted
    e[index].sorted = true;
    // increase index for next iteration
    index++;
    minIndex=index;
    compareIndex=minIndex+1;
  }  
  // if index reached end of array, sorting is done.
  if(index==e.length) return 1;
  else return 0;
}