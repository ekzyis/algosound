// width and height of screen
int w=640, h=360;
// amount of elements to sort
int n=w/4;
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
  background(25);  
  initColors();
  //initRainbow();
  initElements();
  
}
  void draw()
{
  for(Element el : e) el.show();
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
     e[i] = new Element(xd,h-value,elementwidth,value);
     e[i].c = c[cd];
     xd += elementwidth;
     cd++;
     if(cd==c.length) cd = 0;
  }
}

// init colors
void initColors()
{
   c = new color[4];
   // redish
   c[0] = color(255,50,50);
   // greenish
   c[1] = color(50,255,50);
   // blueish
   c[2] = color(50,50,255);
   // purple
   c[3] = color(200,50,200);
}

// rainbow colors
void initRainbow()
{
  c = new color[29];
  c[0] = color(128,0,0);
  c[1] = color(130,40,40);
  c[2] = color(141,83,59);
  c[3] = color(153,102,117);
  c[4] = color(153,102,169);
  c[5] = color(128,0,128);
  c[6] = color(101,0,155);
  c[7] = color(72,0,225);
  c[8] = color(4,0,208);
  c[9] = color(0,68,220);
  c[10] = color(1,114,226);
  c[11] = color(1,159,232);
  c[12] = color(11,175,162);
  c[13] = color(23,179,77);
  c[14] = color(0,212,28);
  c[15] = color(0,255,0);
  c[16] = color(128,255,0);
  c[17] = color(200,255,0);
  c[18] = color(255,255,0);
  c[19] = color(255,219,0);
  c[20] = color(255,182,0);
  c[21] = color(255,146,0);
  c[22] = color(255,109,0);
  c[23] = color(255,73,0);
  c[24] = color(255,0,0);
  c[25] = color(255,0,128);
  c[26] = color(255,105,180);
  c[27] = color(255,0,255);
  c[28] = color(168,0,185);
}