(//--
/**
 * Sound which should resemble something booting.
 */
SynthDef(\boot, {
	var ampEnv,freqEnv,src;
	ampEnv = Env([0,0,1,1,0], [0.3,0.3,0.1,0.1]);
	freqEnv = Env([0.01,0.01,1,1], [0.3,0.4,0.1],curve:\exp);
	src = SinOsc.ar(EnvGen.ar(freqEnv,doneAction:2)*[1200,1197,1203], mul:0.1)*EnvGen.ar(ampEnv);
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

// Define listener for boot sound.
OSCdef(\bootListener, {
	Synth(\boot);
}, "/boot");

// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/status");

)//--
x = Synth(\boot);