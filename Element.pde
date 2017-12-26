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
    rect(x,y-value,w,value); 
  }
}