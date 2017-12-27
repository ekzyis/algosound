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

// do one iteration of for-loop of insertionsort
// (insert one element)
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

// do one insertionsort step
// and also change visuals according
// returns 1 when elements are sorted else 0
int index=1;
int visualInsertionsortStep()
{
  // do one iteration of for-loop
  insertionsortStep(e,index);
  // increase index for next iteration
  index++;
  // if index reached end of array, sorting is complete
  if(index==e.length) return 1;
  // still sorting
  else return 0;  
}