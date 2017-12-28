// insertionsort implementation
static void insertionsort(int[] a){
 for(int i=1;i<a.length;++i)
 {
    int value = a[i];
    int j = i;
    while(j>0 && a[j-1]>value)
    {
       a[j] = a[j-1];
       j = j-1;
    }
    a[j] = value;
 }
}

/** do one iteration of for-loop of insertionsort
 * (insert one element)
 */
static void insertionsortStep(Element[] e, int i)
{
  assert(i<e.length);
  int value = e[i].value;
  color c = e[i].c;
  int j = i;
  while(j>0 && e[j-1].value>value)
  {
    e[j].value = e[j-1].value;
    e[j].c = e[j-1].c;
    j = j-1;
  }
  e[j].value = value;
  e[j].c = c;
}

/** 
 * mark the two elements with a red line 
 * who are getting compared by insertionsort 
 * in the while loop
 *
 * returns index of next element to compare
 * else -1 if position to insert is found
 */
static int insertionsortCompare(Element[] e, int valueIndex, int j)
{
  assert(valueIndex<e.length);
  // unmark old element
  e[j].marked = false;
  int value = e[valueIndex].value;
  // mark value
  e[valueIndex].marked = true;
  if(j>0 && e[j-1].value>value) {
    e[j-1].marked = true;
    return j-1;
  }
  else return -1;
}

/** 
 * do one insertionsort step
 * and also change visuals according
 * 
 * returns 1 when elements are sorted else 0
 */
int index=1;
int compareIndex=index;
int visualInsertionsortStep()
{  
  compareIndex = insertionsortCompare(e,index,compareIndex);
  // do one iteration of for-loop
  if(compareIndex == -1) 
  {
    insertionsortStep(e,index);
    // increase index for next iteration
    index++;
    compareIndex = index; 
  }  
  // if index reached end of array, sorting is complete
  if(index==e.length) return 1;
  // still sorting
  else return 0;  
}