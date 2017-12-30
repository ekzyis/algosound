class Element{
  // (x,y)-position, height=value, width
  int x,y,value,w;
  color c;
  /** 
   * set if this element is currently marked by insertionsort
   */
  boolean marked;
  /**
   * set if this element is already sorted
   */
  boolean sorted;
  
  Element(int _x, int _y, int _w, int _v)
  {
    this.x = _x;
    this.y = _y;
    this.w = _w;    
    this.value = _v;
  }
  
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
    return "("+x+","+y+","+w+","+value+")";
  }
  
  // swap function for visual elements
  void swap(Element e)
  {
    int tmpValue = e.value;
    color tmpColor = e.c;
    e.value = this.value;
    e.c = this.c;
    this.value = tmpValue;
    this.c = tmpColor;
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
  e = new Element[5];
  int[] values = {103,23,51,96,10};
  int elementwidth = w/5;
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