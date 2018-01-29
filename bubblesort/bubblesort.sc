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
SynthDef(\placeholder, { |freq1=440,freq2=440|
	var sig = SinOsc.ar([freq1,freq2])*EnvGen.kr(Env.perc(0.01,0.2,0.05),doneAction:2);
	Out.ar(0, Pan2.ar(Mix(sig)));
}).add;
~old1 = 880;
~old2 = 880;
OSCdef(\swapListener, {
	arg msg;
	var freq1=~old1, freq2=~old2;
	// Increment or decrement frequencies.
	if(msg[1]>~old1,
		{
			freq1 = freq1+10;
		},
		{
			freq1 = freq1-10;
		}
	);
	if(msg[2]>~old2,
		{
			freq2 = freq2+10;
		},
		{
			freq2 = freq2-10;
		}
	);
	// But don't exceed given limits.
	if((freq1<msg[3])||(freq1>msg[4]),
		{
			freq1 = ~old1;
		},
		{}
	);
	if((freq2<msg[3])||(freq2>msg[4]),
		{
			freq2 = ~old1;
		},
		{}
	);
	"-".postln;
	freq1.postln;
	freq2.postln;
	"-".postln;
	Synth(\placeholder, [\freq1, msg[1], \freq2, msg[2]] );
	~old1 = freq1;
	~old2 = freq2;
}, "/swap");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);
// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/status");

)//--Parentheses end
