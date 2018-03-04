/*
* @Author: ekzyis
* @Date:   30-01-2018 00:28:20
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:04:15
*/
/**
 * Methods about color handling are defined here.
 * ==============================================
 */

// init colors
color[] getColors()
{
   color[] colors = new color[4];
   // redish
   colors[0] = color(255,50,50);
   // greenish
   colors[1] = color(50,255,50);
   // blueish
   colors[2] = color(50,50,255);
   // purple
   colors[3] = color(200,50,200);
   return colors;
}

// rainbow color
color[] getRainbow()
{
  color[] colors = new color[29];
  colors[0] = color(128,0,0);
  colors[1] = color(130,40,40);
  colors[2] = color(141,83,59);
  colors[3] = color(153,102,117);
  colors[4] = color(153,102,169);
  colors[5] = color(128,0,128);
  colors[6] = color(101,0,155);
  colors[7] = color(72,0,225);
  colors[8] = color(4,0,208);
  colors[9] = color(0,68,220);
  colors[10] = color(1,114,226);
  colors[11] = color(1,159,232);
  colors[12] = color(11,175,162);
  colors[13] = color(23,179,77);
  colors[14] = color(0,212,28);
  colors[15] = color(0,255,0);
  colors[16] = color(128,255,0);
  colors[17] = color(200,255,0);
  colors[18] = color(255,255,0);
  colors[19] = color(255,219,0);
  colors[20] = color(255,182,0);
  colors[21] = color(255,146,0);
  colors[22] = color(255,109,0);
  colors[23] = color(255,73,0);
  colors[24] = color(255,0,0);
  colors[25] = color(255,0,128);
  colors[26] = color(255,105,180);
  colors[27] = color(255,0,255);
  colors[28] = color(168,0,185);
  return colors;
}
