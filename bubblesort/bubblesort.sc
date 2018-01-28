// Test synths after creating
x = Synth(\boot);
x = Synth(\placeholder1)

(//--Parentheses begin

/**
 * Futuristic booting sound.
 */
SynthDef(\boot, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.ar(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.ar(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;
// Define listener for boot sound.
OSCdef(\bootListener, {
	Synth(\boot);
}, "/boot");

/**
 * This Synth is a placeholder for the actual swap synth(s).
 */
SynthDef(\placeholder, { |freq=440|
	var sig = SinOsc.ar(freq)*EnvGen.kr(Env.perc(0.01,0.2),doneAction:2);
	Out.ar(0, sig);
}).add;
OSCdef(\swapListener, {
	arg msg;
	Synth(\placeholder, [\freq, msg[1]] );
}, "/swap");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);
// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/status");

)//--Parentheses end

