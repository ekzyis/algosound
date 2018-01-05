/**
 * Mainfile of Bubblesort visualization.
 * ====================================
 * @author ekzyis
 * @date December 2017
 */

import netP5.*;
import oscP5.*;
import supercollider.*;

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

OscP5 osc;
NetAddress supercollider;

void settings()
{
   size(w,h);   
}

void setup()
{   
  frameRate(30);
  c = getColors();
  e = getElements();
  /** 
   * Count how many steps bubblesort needs
   * sort this elements. This count will be used
   * to know when a new frame is no longer needed to calculate
   * since bubblesort is finished.
   */
  sortSteps = countBubblesortSteps(e);
  stepNumber = 0;
  
  osc = new OscP5(this, 12000);
  supercollider = new NetAddress("127.0.0.1", 57120);
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
  }
  else
  {
    // unmark first two elements
    e[0].marked = false;
    e[1].marked = false;
  }
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