/*
* @Author: ekzyis
* @Date:   04-01-2018 22:54:23
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:06:24
*/
// Parentheses to execute all important code at once
(
// Define synth
SynthDef('mousePressed', {
	arg pitch=200;
	var sound = SinOsc.ar(pitch, mul:0.2).dup;
	var linen = Env.linen(0.01, sustainTime:0, releaseTime:1);
	var env = EnvGen.kr(linen, doneAction:2);
	Out.ar(0, sound * env);
}).add;
// Define listener
OSCdef('mouseListener', {
	arg msg;
	Synth('mousePressed', [pitch:msg[1]+msg[2]]);
}, "/mousePressed")
)

// Play synth
Synth('mousePressed');

// Show network address
NetAddr.localAddr
