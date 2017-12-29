// width and height of screen
int w=640, h=360;
// amount of elements to sort
int n=w/5; 
// elements
Element[] e;
// colors
color[] c;

void settings()
{
   size(w,h);   
}

void setup()
{   
  frameRate(60);
  initColors();
  //initRainbow();
  initElements();
  //testElements();
  int[] a = {45,134,57,45,5756,89567,356,357,3,235,26,37,762,52,352,7,3,624,6};
  // demonstration of sorting algorithm
  // printarr(getValues(e));
  e = mergesort(e);
  // printarr(getValues(e));
  assert(isSorted(getValues(e)));
}

void draw()
{
  background(25);
  // show elements
  for(Element el : e) el.show(); 
  
}

// print an integer-array
void printarr(int[] a)
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