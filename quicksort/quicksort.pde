/**
 * Quicksort implementation.
 * =========================
 *
 * In this quicksort implementation,
 * the element in the middle becomes
 * the pivot element.
 * 
 * @author ekzyis
 * @date January 2018
 */ //<>//
static void quicksort(int a[])
{
  int lower = 0;
  int upper = a.length-1;
  quicksort(a, lower, upper);   
}
static void quicksort(int a[], int lower, int upper)
{
  int l = lower;
  int r = upper;
  int pivot = a[(int)((l + r)/2)];
  do 
  {
    while (a[l]<pivot) 
    { 
      l++;  
    }
    while (a[r]>pivot)
    { //<>//
      r--;
    }                                                                   
    if (l<=r) 
    {
      int tmp = a[l];      
      a[l] = a[r];
      a[r]=tmp;
      l++;
      r--; 
    }
  }while(l<=r);
  if (lower<r) 
  {                           
    quicksort(a,lower, r);  
  }                          
  if (l<upper) 
  {          
    quicksort(a, l, upper);  
  }                                              
}