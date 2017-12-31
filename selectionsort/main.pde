/**
 * Mainfile of Selectionsort visualization.
 * ========================================
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
// still sorting?
int sort;

void settings()
{
   size(w,h);   
}

void setup()
{
  frameRate(60);
  c = getColors();
  e = getElements();
  //e = testElements();
  sort = 0;
}

void draw()
{
  background(25);
  for(Element el : e) el.show();
  if(sort==0) sort = visualSelectionsortStep();
}

// print an integer-array
static void printarr(int[] a)
{
  for(int v : a )
  {
    print(v + " ");
  }
  println();
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