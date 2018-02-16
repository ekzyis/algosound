/*
* @Author: ekzyis
* @Date:   04-01-2018 22:54:23
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:06:34
*/
/**
 * This sketch is an example of using
 * SuperCollider in Processing by
 * sending Osc messages.
 *
 * Thanks to:
 * https://www.funprogramming.org/138-Processing-talks-to-SuperCollider-via-OSC.html
 */

import netP5.*;
import oscP5.*;
import supercollider.*;

OscP5 osc;
NetAddress supercollider;

void setup()
{
  size(128,128);
  // initialize osc
  osc = new OscP5(this, 12000);
  // initialize address to local sc server
  // sc will listen for messages at port 57120
  supercollider = new NetAddress("127.0.0.1", 57120);
}

void draw()
{
}

void mousePressed()
{
  // define osc message
  OscMessage msg = new OscMessage("/mousePressed");
  // add data to message for supercollider
  msg.add(map(mouseX, 0, 128, 0, 200));
  msg.add(map(mouseY, 0, 128, 0, 200));
  // send message
  osc.send(msg,supercollider);
}
