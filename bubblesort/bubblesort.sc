FreqScope.new
Stethoscope.new

// Test synths after creating
x = Synth(\boot);
y = Synth(\swapwave)
y.set(\gate, 0)
y.set(\freq1, 600); y.set(\freq2, 400);
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

/**
 * Define listener for freeing of synth.
 */
OSCdef(\freeListener, {
	if(~sinewave.isNil, {
		// Synth does not exist. Do nothing.
	}, {
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
