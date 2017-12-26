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
}

void draw()
{
  background(25);
  // show elements
  for(Element el : e) el.show();
  // make a bubblesort step and change visuals according
  visualBubblesortStep();
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