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

// check one pair of integers in array and swap if not in ascending order
// returns 1 if swapped else 0
static int bubblesortStep(int[] a, int i)
{
  assert(i<a.length-1);
  if(a[i]>a[i+1])
  {
    int tmp = a[i+1];
    a[i+1] = a[i];
    a[i] = tmp;
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
  // get element values as int[]
  int[] a = new int[e.length];
  // also get colors for visualization
  int[] elcolors = new color[e.length];
  for(int i=0;i<a.length;++i)
  {
    a[i] = e[i].value;
    elcolors[i] = e[i].c;
  }
  //printarr(a); 
  // let bubblesort be bubblesort
  int swap = bubblesortStep(a,sort);
  if(swap==1)
  {
    // also swap colors 
    color tmp = elcolors[sort];
    elcolors[sort] = elcolors[sort+1];
    elcolors[sort+1] = tmp;
  }
  // update elements according to what bubblesort has spoken
  for(int i=0;i<a.length;++i)
  {
     e[i].value = a[i];
     e[i].c = elcolors[i];
  } 
  // increase sorting index for next iteration
  sort++;
  if(sort == a.length-1) sort = 0;
  return swap;
}