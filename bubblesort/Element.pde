class Element{
  int x,y,value,w;
  color c;
  Element(int _x, int _y, int _w, int _v)
  {
    this.x = _x;
    this.y = _y;
    this.w = _w;    
    this.value = _v;
  }
  
  void show()
  {
    fill(c);   
    rect(x,value,w,-value); 
  }
}

// ** Color-array must not be null before calling this function **
// init elements with random value and set color
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