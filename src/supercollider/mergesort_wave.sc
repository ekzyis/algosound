FreqScope.new
Stethoscope.new
s.queryAllNodes

// Test synths after creating
x = Synth(\wave_boot_MERGESORT);
y = Synth(\wave_algowave_MERGESORT);
y.set(\gate, 0)
y.set(\freq, 700);
y.set(\freqlag, 1)
y.free

(//--Parentheses begin
/**
 * Futuristic booting sound.
 */
SynthDef(\wave_boot_MERGESORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

/**
 * Synth which will be modified by individual swaps happening while sorting.
 */
SynthDef(\wave_algowave_MERGESORT, {
	arg freq=440, freqlag=0.1, amptotal=1, amp=0.2, amplag=0.5, gate=1;
	var sig, ampmod;
	// Make higher pitches less loud.
	freq = [freq*0.6, freq*0.8, freq, freq*1.2];
	ampmod = freq.expexp(200,4000,amp,0.02);
	sig = SinOsc.ar(
		Lag.kr(freq,freqlag),
		mul:Lag.kr(ampmod, amplag)*Lag.kr(amptotal,amplag));
	sig = sig * EnvGate(1,gate,amplag,doneAction:2);
	Out.ar(0, Mix(sig)!2);
}).add;

// Define listener for boot sound.
OSCdef(\wave_boot_OSC_MERGESORT, {
	"playing boot sound.".postln;
	Synth(\wave_boot_MERGESORT);
}, "/wave_boot_MERGESORT");

// Define listener for start of algowave-synth.
OSCdef(\wave_start_OSC_MERGESORT, {
	"creating algowave.".postln;
	~algowave = Synth(\wave_algowave_MERGESORT);
}, "/wave_start_MERGESORT");

// Define listener for pausing of algowave-synth.
OSCdef(\wave_pause_OSC_MERGESORT, {
	"pausing algowave.".postln;
	~algowave.set(\amptotal, 0);
}, "/wave_pause_MERGESORT");

// Define listener for resuming of algowave-synth.
OSCdef(\wave_resume_OSC_MERGESORT, {
	"resuming algowave.".postln;
	~algowave.set(\amptotal, ~amp);
}, "/wave_resume_MERGESORT");

// Define listener for modifying.
OSCdef(\wave_set_OSC_MERGESORT, {
	arg msg;
	~algowave.set(\amptotal, ~amp);
	~algowave.set(\freq, msg[1]);
}, "/wave_set_MERGESORT");

// Realtime modulating of synths
OSCdef(\wave_set_freqlag_OSC_MERGESORT, {
	arg msg;
	"\\wave_set_freqlag_OSC_MERGESORT - arguments: [".post;msg[1].post;"]".postln;
	~algowave.set(\freqlag, msg[1]);
}, "/wave_set_freqlag_MERGESORT");

~amp = 1;
OSCdef(\wave_set_amp_OSC_MERGESORT, {
	arg msg;
	"\\wave_set_amp_OSC_MERGESORT - arguments: [".post;msg[1].post;"]".postln;
	~amp = msg[1];
}, "/wave_set_amp_MERGESORT");

OSCdef(\wave_set_amplag_OSC_MERGESORT, {
	arg msg;
	"\\wave_set_amplag_OSC_MERGESORT - arguments: [".post;msg[1].post;"]".postln;
	~algowave.set(\amplag, msg[1]);
}, "/wave_set_amplag_MERGESORT");
/**
 * Define listener for freeing of synth.
 * KNOWN ISSUES: After freeing, another free-attempt will
 * cause a
 *  FAILURE IN SERVER /n_free Node XXXX not found
 * error.
 * Solution: Check if synth is already freed.
 * STATUS: Did not find a function like this :(
 * Tried with SYNTH.isNil but this leads to other possible
 * more severe bugs like orphaned synths.
 */
OSCdef(\wave_free_OSC_MERGESORT, {
	"freeing algowave.".postln;
	// Free it using gate.
	~algowave.set(\gate, 0);
}, "/wave_free_MERGESORT");

// Create address to fire messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\wave_status_OSC_MERGESORT, {
	if(x==0,
		{ Synth(\wave_boot_MERGESORT); x = 1; },
		{}
	);
	~address.sendMsg("/hello");
}, "/wave_hello_MERGESORT");

)//--Parentheses end
