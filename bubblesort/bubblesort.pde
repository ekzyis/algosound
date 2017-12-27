// bubblesort implementation
static void bubblesort(int[] a)
{
  boolean swap;
  do
  {
    swap = false;
     for(int i=0; i<a.length-1; ++i)
     {
       if(a[i]>a[i+1])
       {
          int tmp = a[i+1];
          a[i+1] = a[i];
          a[i] = tmp;
          swap = true;
       }
     }
  }while(swap);
}

// check one pair of elements in array and swap if not in ascending order
// returns 1 if swapped else 0
static int bubblesortStep(Element[] e, int i)
{
  assert(i<e.length-1);
  if(e[i].value>e[i+1].value)
  {
    int tmp = e[i+1].value;
    color tmp2 = e[i+1].c;
    e[i+1].value = e[i].value;
    e[i+1].c = e[i].c;
    e[i].value = tmp;
    e[i].c = tmp2;
    return 1;
  }
  return 0;
}

// do one bubblesort swap operation 
// and also change colors of elements involved if needed
// returns 1 if swapped else 0
int sort = 0;
int visualBubblesortStep()
{ 
  // let bubblesort be bubblesort
  int swap = bubblesortStep(e,sort);
  // increase sorting index for next iteration
  sort++;
  if(sort == e.length-1) sort = 0;
  return swap;
}