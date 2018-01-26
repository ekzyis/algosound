x = Synth(\boot);

(//--Parentheses begin
/**
 * Futuristic booting sound.
 */
SynthDef(\boot, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.ar(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.ar(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2450,2300,2200,1200,1000], mul:0.1)*ampEnv;
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

)//--Parentheses end
