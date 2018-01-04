// define synth
(
SynthDef('swapped', {
	arg pitch=200;
	var sound = SinOsc.ar(pitch, mul:0.2).dup;
	var linen = Env.linen(0.01, sustainTime:0, releaseTime:1);
	var env = EnvGen.kr(linen, doneAction:2);
	Out.ar(0, sound * env);
}).add;
)
// play synth
Synth('swapped');
// show network address
NetAddr.localAddr
// define listener
(
OSCdef('swaplistener', {
	arg msg;
	Synth('swapped', [pitch:msg[1]]);
}, "/swapped")
)