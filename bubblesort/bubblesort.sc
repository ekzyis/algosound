// show network address
NetAddr.localAddr

// define synth
(
SynthDef('swapped', {
	arg pitch=200;
	var sound = SinOsc.ar(pitch, mul:0.2).dup;
	var linen = Env.linen(0.01, sustainTime:0, releaseTime:0.1);
	var env = EnvGen.kr(linen, doneAction:2);
	Out.ar(0, sound * env);
}).add;
)
Synth('swapped');

// define listener
(
OSCdef('swaplistener1', {
	arg msg;
	Synth('swapped', [pitch:msg[1]]);
}, "/swapped")
)

(
SynthDef('notSwapped', {
	arg pitch=200;
	var sound = Pulse.ar(pitch, mul:0.2).dup;
	var linen = Env.linen(0.01, sustainTime:0, releaseTime:0.1);
	var env = EnvGen.kr(linen, doneAction:2);
	Out.ar(0, sound*env);
}).add;
)
Synth('notSwapped')

(
OSCdef('swaplistener2', {
	arg msg;
	Synth('notSwapped', [pitch:msg[1]]);
}, "/notSwapped")
)