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
  frameRate(120);
  initColors();
  initElements();
  sort=0;
  // handle first element as sorted
  e[0].sorted = true;
}

void draw()
{
  background(25);
  // show elements
  for(Element el : e) el.show();
  // make a insertionsort step
  if(sort==0) sort = visualInsertionsortStep();
}

// print an integer-array
void printarr(int[] a)
{
  for(int n : a){
    print(n + " ");
  }
  println();
}