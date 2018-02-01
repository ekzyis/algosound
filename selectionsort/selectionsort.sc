FreqScope.new
Stethoscope.new
s.queryAllNodes

// Test synths after creating
x = Synth(\boot);
y = Synth(\swapwave)
y.set(\gate, 0)
y.set(\freq, 700);
y.set(\freqlag, 1)
y.free

(//--Parentheses begin
~sinewave = nil;
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
	arg freq=440, freqlag=0.01, amp=1, amplag=0.5, gate=1;
	var sig, ampmod;
	// Make higher pitches less loud.
	ampmod = freq.expexp(200,4000,0.2,0.02);
	sig = SinOsc.ar(
		Lag.kr([freq*0.6, freq*0.8, freq, freq*1.2],freqlag),
		mul:Lag.kr([amp, amp*0.9, amp*0.8, amp*0.7]*ampmod, amplag));
	sig = sig * EnvGate(1,gate,amplag,doneAction:2);
	Out.ar(0, Mix(sig)!2);
}).add;

// Define listener for boot sound.
OSCdef(\bootListener, {
	// Why is this not posted?
	"playing boot sound.".postln;
	// Play boot sound
	Synth(\boot);
}, "/boot");

// Define listener for start of sinewave.
OSCdef(\sortListener, {
	// Why is this not posted?
	"creating swapwave".postln;
	~sinewave = Synth(\swapwave);
}, "/wave_start");

// Define listener for pausing of sinewave.
OSCdef(\pauseListener, {
	// Why is this not posted?
	"pausing swapwave.".postln;
	~sinewave.set(\amp, 0);
}, "/wave_pause");

// Define listener for resuming of sinewave.
OSCdef(\resumeListener, {
	// Why is this not posted?
	"resuming swapwave.".postln;
	~sinewave.set(\amp, 1);
}, "/wave_resume");

// Define listener for modifying.
OSCdef(\modListener, {
	arg msg;
	// Why is this not posted?
	"modulating swapwave.".postln;
	~sinewave.set(\amp, 1);
	~sinewave.set(\freq, msg[1]);
}, "/wave_set");

/**
 * Define listener for freeing of synth.
 */
OSCdef(\freeListener, {
	if(~sinewave.isNil, {
		// Synth does not exist. Do nothing.
	}, {
		// Why is this not posted?
		"freeing swapwave.".postln;
		// Synth does exist. Free it using gate.
		~sinewave.set(\gate, 0);
		// Wait until the synth is freed, then set it to nil.
		Routine
		{
			1.2.wait;
			/**
			 * When this is uncommented, it can happen that a synth gets created
			 * while this routine is waiting. This leads to a non-freed synth
			 * set to nil. After this, the synth can no longer be accessed and can not be freed.
			 * Due to this, it is better ro risk some "FAILURE IN SERVER /n_set Node XXXX not found"
			 * messages than the user having to free all synths manually to free the orphan synth.
			 * ---Steps to reproduce bug#1:
			 * Send a /wave_free-message and within 1.2 seconds a /wave_start-message.
			 * ---Steps t reproduce bug#2:
			 * Repeatedly send /wave_free-messages before the synth would actually be nil.
			 */
			//~sinewave = nil;
		}.play;

	});
}, "/wave_free");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/status");

)//--Parentheses end
