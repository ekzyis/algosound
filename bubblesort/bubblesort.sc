// Test synths after creating
x = Synth(\boot);
x = Synth(\placeholder)

(//--Parentheses begin

/**
 * Futuristic booting sound.
 */
SynthDef(\boot, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
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
SynthDef(\placeholder, { |freq1=440,freq2=880|
	var sig = SinOsc.ar([freq1,freq2])*EnvGen.kr(Env.perc(0.01,0.1,0.1),doneAction:2);
	Out.ar(0, Pan2.ar(Mix(sig)));
}).add;
OSCdef(\swapListener, {
	arg msg;
	Synth(\placeholder, [\freq1, msg[1], \freq2, msg[2]]);
}, "/swap");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);
// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/status");

)//--Parentheses end
FreqScope.new

