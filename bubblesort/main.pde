// width and height of screen
int w=640, h=360;
// amount of elements to sort
int n=w/5; 
// elements
Element[] e;
// colors
color[] c;
/** 
 * number of needed bubblesort steps to
 * to sort elements
 */
int sortSteps;
// current step number
int stepNumber;
void settings()
{
   size(w,h);   
}

void setup()
{   
  frameRate(120);
  initColors();
  //initRainbow();
  initElements();
  //testElements();
  sortSteps = countBubblesortSteps(e);
  stepNumber = 0;
}

void draw()
{
  background(25);
  // show elements
  for(Element el : e) el.show();  
  // only draw new frame when there is a new sorting frame
  if(stepNumber <= sortSteps) 
  {
    // make a bubblesort step and change visuals according 
    visualBubblesortStep();  
    stepNumber++;
    println(stepNumber);
  }
  else
  {
    // unmark first two elements
    e[0].marked = false;
    e[1].marked = false;
  }
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