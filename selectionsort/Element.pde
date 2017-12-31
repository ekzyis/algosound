/**
 * This class saves the data for visualization.
 * ============================================
 * @author ekzyis
 * @date December 2017
 */
class Element{
  /**
   * Values which are passed to swap function.
   * They define which member should be swapped.
   */
  // swap values
  final static int VALUES = 1; // 2^0
  // swap colors
  final static int COLORS = 2; // 2^1
  // swap coordinates
  final static int COORDINATES = 4; // 2^2
  // (x,y)-position, height=value, width
  int x,y,value,w;
  color c;
  /** 
   * set if this element is currently marked by sorting algorithm
   */
  boolean marked;
  /**
   * set if this element is already sorted
   */
  boolean sorted;
  
  // constructor
  Element(int _x, int _y, int _w, int _v)
  {
    this.x = _x;
    this.y = _y;
    this.w = _w;    
    this.value = _v;
  }
  // how to show this on canvas
  void show()
  {
    if(sorted == true)
    {
      // green transparent background
      fill(color(0,255,0,50));
      noStroke();
      rect(x,0,w,h);
      stroke(0);
    }
    if(marked == true)
    {
      // red vertical line
      fill(color(255,0,0));
      noStroke();
      rect(x+w/2,0,w/2,h);
      stroke(0);
    }    
    fill(c);   
    rect(x,y,w,-value); 
  }
  
  // string representation
  String string()
  {
    return "("+value+","+x+")";
  }
  
  /** 
   * Swap function.
   * This function is meant to be used with the logical operator |.
   * This means, calling e1.swap(e2, Element.VALUES | Element.COLORS);
   * will swap values and colors between Element e1 and e2.
   */
  void swap(Element e, int a)
  {
    //println(a);
    // is bit 0 /(=2^0) set?
    if((a & 1)==1) 
    {
      //println("swapping values");
      int tmp = e.value;
      e.value = this.value;
      this.value = tmp;
    }
    // is bit 1 set?
    if(((a >> 1) & 1)==1)
    {
      //println("swapping colors");
      color tmp = e.c;
      e.c = this.c;
      this.c = tmp;
    }
    // is bit 2 set?
    if(((a >> 2) & 1)==1)
    {
      //println("swapping coordinates");
      int tmpX = e.x;
      int tmpY = e.y;
      e.x = this.x;
      e.y = this.y;
      this.x = tmpX;
      this.y = tmpY;
    }
  }
}

/** # Color-array must not be null before calling this function #
 * init elements with random value and set color
 */
void initElements()
{
  e = new Element[n];
  int elementwidth = w/n;  
  // x offset
  int xd = 0;
  // color "offset"
  int cd = 0;  
  for(int i=0;i<e.length;++i)
  {
    int value = (int)(Math.random()*h);
    e[i] = new Element(xd,h,elementwidth,value);
    e[i].c = c[cd];
    xd += elementwidth;
    cd++;
    if(cd==c.length) cd = 0;
  }
}

// get some test elements for well ... testing purposes
void testElements()
{
  int n = 6;
  e = new Element[n];
  int[] values = {10,2,5,8,112,4};
  int elementwidth = w/n;
  int xd = 0;
  int cd = 0;
  for(int i=0;i<e.length;++i)
  {
    e[i] = new Element(xd,h,elementwidth,values[i]);
    e[i].c = c[cd];
    xd += elementwidth;
    cd++;
    if(cd==c.length) cd = 0;
  }
}

// return int[] from values of elements
static int[] getValues(Element[] e)
{
  int[] values = new int[e.length];
  for(int i=0;i<e.length;++i)
  {
    values[i] = e[i].value;
  }
  return values;
}

// print an element array
static void printarr(Element[] e)
{
  print("{");
  for(Element el : e)
  {
    print(el.string());
  }
  print("}");
  println();
}

// checks if an Element[] is in ascending order
boolean isSorted(Element[] e)
{
  for(int i=0;i<e.length-1;++i)
  {
    if(e[i].value>e[i+1].value || e[i].x>e[i+1].x) return false;
  }
  return true;
}