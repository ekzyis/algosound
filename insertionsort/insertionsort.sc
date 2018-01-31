// Test synths after creating
x = Synth(\boot);
y = Synth(\swapwave)
y.set(\gate, 0)
y.set(\freq1, 600); y.set(\freq2, 400);
y.set(\freqlag, 1)
~sinewave.set(\freqlag, 0.1)
y.free

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

/**
 * Swapwave which will be modified by individual swaps happening while sorting.
 */
SynthDef(\swapwave, {
	arg freq1=440, freq2=440, freqlag=0.1, amp=0.1, amplag=0.5, gate=1;
	var sig = SinOsc.ar(Lag.kr([freq1,freq2], freqlag)!2, mul:Lag.kr(amp, amplag))*EnvGate(1,gate,amplag,doneAction:2);
	Out.ar(0, sig);
}).add;

// Define listener for boot sound.
OSCdef(\bootListener, {
	// Play boot sound
	Synth(\boot);
}, "/boot");

// Define listener for start of sinewave.
OSCdef(\sortListener, {
	~sinewave = Synth(\swapwave);
}, "/wave_start");

// Define listener for pausing of sinewave.
OSCdef(\pauseListener, {
	~sinewave.set(\amp, 0);
}, "/wave_pause");

// Define listener for resuming of sinewave.
OSCdef(\resumeListener, {
	~sinewave.set(\amp, 0.1);
}, "/wave_resume");

// Define listener for modifying.
OSCdef(\modListener, {
	arg msg;
	~sinewave.set(\amp, 0.1);
	~sinewave.set(\freq1, msg[1], \freq2, msg[2]);
}, "/wave_set");

// Define listener for freeing of synth.
OSCdef(\freeListener, {
	~sinewave.set(\gate, 0);
}, "/wave_free");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/status");

)//--Parentheses end
