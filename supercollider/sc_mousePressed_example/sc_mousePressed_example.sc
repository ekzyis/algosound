// define synth
(
SynthDef('mousePressed', {
	arg pitch=200;
	var sound = SinOsc.ar(pitch, mul:0.2).dup;
	var linen = Env.linen(0.01, sustainTime:0, releaseTime:1);
	var env = EnvGen.kr(linen, doneAction:2);
	Out.ar(0, sound * env);
}).add;
)
// play synth
Synth('mousePressed');
// show network address
NetAddr.localAddr
// define listener
(
OSCdef('mouseListener', {
	arg msg;
	Synth('mousePressed', [pitch:msg[1]+msg[2]]);
}, "/mousePressed")
)
