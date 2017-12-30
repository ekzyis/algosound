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
  //initElements();
  testElements();
  // demonstration of sorting algorithm
  
  printarr(getValues(e));
  printarr(e);
  e = mergesort(e);
  printarr(e);
  printarr(getValues(e));
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

// print an element array
void printarr(Element[] e)
{
  for(Element el : e)
  {
    print(el.string());
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