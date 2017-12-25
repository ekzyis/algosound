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