/**
 * Mainfile of Mergesort visualization.
 * ====================================
 * @author ekzyis
 * @date December 2017
 */

// width and height of screen
int w=640, h=360;
// amount of elements to sort
int n=w/5; 
// elements
Element[] e;
// colors
color[] c;
// startindex of subset
int start;
// length of subset
int len;

void settings()
{
   size(w,h);   
}

void setup()
{   
  frameRate(30);
  c = getColors();
  e = getElements();
  //e = getTestElements();
  start = 0;
  len = e.length;
  printarr(e);
  //demonstration of sorting algorithm
  //printarr(e);
  //e = mergesort(e);
  //printarr(e);
  //assert(isSorted(e));
}

void draw()
{
  background(25);
  // show elements
  for(Element el : e) el.show(); 
  if(sorting()) {
    int[] nextFrame = visualMergesortStep(e,start,len);
    start = nextFrame[0];
    len = nextFrame[1];
  }
}

/**
 * Checks if sorting is complete with recursionStack.
 * If stack reaches zero, sorting complete.
 */
boolean sorting()
{
  return (recursionStack.length()!=0); 
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

// print an arraylist of integers
static void printlist(ArrayList<int[]> list)
{
  print("{");
  for(int[] a : list)
  {
    print("{");
    print(a[0]);
    for(int i=1;i<a.length;++i)
    {
      print(", "+a[i]);
    }
    print("}");
  }
  println("}");
}

// checks if an int[] is in ascending order
boolean isSorted(int[] a)
{
  for(int i=0;i<a.length-1;++i)
  {
    if(a[i]>a[i+1]) return false;
  }
  return true;
}