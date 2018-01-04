/**
 * Mainfile of Quicksort visualization.
 * ====================================
 * @author ekzyis
 * @date January 2018
 */
 
void setup()
{
  for(int i=0;i<100;++i)
  {
    int[] a = getRndArr(10000,100000);
    //printarr(a);
    quicksort(a);
    //printarr(a);
    assert(isSorted(a));
  }
  print("success!");
}

// get random int values to sort
int[] getRndArr(int n, int max)
{
  int[] ret = new int[n];
  for(int i=0;i<ret.length;++i)
  {
    ret[i] = (int)(Math.random()*max);
  }
  return ret;
}

// print an integer-array
static void printarr(int[] a)
{
  print("{");
  print(a[0]);
  for(int i=1;i<a.length;++i)
  {
    print(", "+a[i]);
  }
  print("}");
  println();
}

// checks if an int[] is in ascending order
boolean isSorted(int[] a)
{
  for(int i=0;i<a.length-1;++i)
  {
    //println(i);
    if(a[i]>a[i+1]) 
    {
      println(i);
      return false;
    }
  }
  return true;
}