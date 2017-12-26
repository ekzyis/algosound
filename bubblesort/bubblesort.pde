// bubblesort implementation
void bubblesort(int[] a)
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

// check one pair of integers in array and swap if needed
// returns 1 when swapped else 0
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