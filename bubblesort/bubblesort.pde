/**
 * Bubblesort implementation.
 * ==========================
 * @author ekzyis
 * @date December 2017
 */

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

/**
 * count how many swaps are needed to sort this elements
 */
static int countBubblesortSteps(Element[] e)
{
  int[] a = getValues(e);
  int counter = 0;
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
       counter++;
     }
  }while(swap);
  return counter;  
}

/** 
 * check one pair of elements in array and swap if not in ascending order
 * 
 * returns 1 if swapped else 0
 */
 int xd = 0;
static int bubblesortStep(Element[] e, int i)
{
  assert(i<e.length-1);
  if(e[i].value>e[i+1].value)
  {
    // swap values and colors
    e[i].swap(e[i+1], Element.VALUES | Element.COLORS);
    return 1;
  }
  return 0;
}

/** 
 * do one bubblesort swap operation 
 * and also change colors of elements involved if needed
 *
 * returns 1 if swapped else 0
 */
int index = 0;
int visualBubblesortStep()
{ 
  // unmark element marked on last frame
  if(index>0) e[index-1].marked = false;  
  else 
  {
    /**
     * unmark last two elements in list
     * since probably sort got reset to 0
     */
    e[e.length-1].marked = false;
    e[e.length-2].marked = false;
  }
  // mark elements which are going to get compared this frame
  e[index].marked = true;
  e[index+1].marked = true;
  // let bubblesort be bubblesort
  int swap = bubblesortStep(e,index);
  // increase index for next iteration
  index++;
  // restart bubblesort's for-loop
  if(index == e.length-1) 
  {  
    index = 0;
  }
  return swap;
}