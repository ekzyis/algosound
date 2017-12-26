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
  // make a bubblesort step and changes visuals according
  visualBubblesortStep();
}

void printarr(int[] a)
{
  for(int v : a )
  {
    print(v + " ");
  }
  println();
}

// do one bubblesort swap operation 
// and also change colors of elements involved if needed
// returns 1 if swapped else 0
int sort = 0;
int visualBubblesortStep()
{
  // get element values as int[]
  int[] a = new int[e.length];
  // also get colors for visualization
  int[] elcolors = new color[e.length];
  for(int i=0;i<a.length;++i)
  {
    a[i] = e[i].value;
    elcolors[i] = e[i].c;
  }
  printarr(a); 
  // let bubblesort be bubblesort
  int swap = bubblesortStep(a,sort);
  if(swap==1)
  {
    // also swap colors 
    color tmp = elcolors[sort];
    elcolors[sort] = elcolors[sort+1];
    elcolors[sort+1] = tmp;
  }
  // update elements according to what bubblesort has spoken
  for(int i=0;i<a.length;++i)
  {
     e[i].value = a[i];
     e[i].c = elcolors[i];
  } 
  // increase sorting index for next iteration
  sort++;
  if(sort == a.length-1) sort = 0;
  printarr(a);
  return swap;
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