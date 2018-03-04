/*
* @Author: ekzyis
* @Date:   31-01-2018 21:37:44
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:05:13
*/
FreqScope.new
Stethoscope.new
s.queryAllNodes

// Test synths after creating
x = Synth(\boot_wave_SELECTIONSORT);
y = Synth(\algowave_wave_SELECTIONSORT)
y.set(\gate, 0)
y.set(\freq, 700);
y.set(\freqlag, 1)
y.free
z = Synth(\minimum_wave_SELECTIONSORT);

(//--Parentheses begin
/**
 * Futuristic booting sound.
 */
SynthDef(\boot_wave_SELECTIONSORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

/**
 * Synth which will be modified by individual element accesses while sorting.
 */
SynthDef(\algowave_wave_SELECTIONSORT, {
	arg freq=440, freqlag=0.1, amptotal=0.3, amp=0.2, amplag=0.5, gate=1;
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

/**
 * This synth represents the current smallest found element.
 */
SynthDef(\minimum_wave_SELECTIONSORT, {
	arg freq=440, pulsefreq=0, amp=0.3, att=0.01, decay=1;
	var sig,env;
	env = EnvGen.ar(Env([1,1],[2]),doneAction:2);
	sig = Mix(
		FreeVerb.ar(
			Decay2.ar(
				Impulse.ar(pulsefreq), att, decay, mul:SinOsc.ar(freq, mul:amp)
	),0.4,0.7));
	sig = sig * env;
	Out.ar(0, sig!2);
}).add;

// Define listener for boot sound.
OSCdef(\boot_wave_OSC_SELECTIONSORT, {
	"playing boot sound.".postln;
	Synth(\boot_wave_SELECTIONSORT);
}, "/boot_wave_SELECTIONSORT");

// Define listener for start of algowave-synth.
OSCdef(\start_wave_OSC_SELECTIONSORT, {
	"creating synths.".postln;
	~algowave = Synth(\algowave_wave_SELECTIONSORT);
}, "/wave_start_SELECTIONSORT");

// Define listener for pausing of algowave-synth.
OSCdef(\pause_wave_OSC_SELECTIONSORT, {
	"pausing sound.".postln;
	~algowave.set(\amptotal, 0);
}, "/wave_pause_SELECTIONSORT");

// Define listener for resuming of algowave-synth.
OSCdef(\resume_wave_OSC_SELECTIONSORT, {
	"resuming sound.".postln;
	~algowave.set(\amptotal, 0.3);
}, "/wave_resume_SELECTIONSORT");

// Define listener for modifying.
OSCdef(\mod_wave1_OSC_SELECTIONSORT, {
	arg msg;
	~algowave.set(\amptotal, 0.3);
	~algowave.set(\freq, msg[1]);
}, "/wave_set_SELECTIONSORT");

OSCdef(\mod_wave2_OSC_SELECTIONSORT, {
	arg msg;
	Synth(\minimum_wave_SELECTIONSORT, [\freq, msg[1]]);
}, "/min_set_SELECTIONSORT");

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
OSCdef(\free_wave_OSC_SELECTIONSORT, {
	"freeing synths.".postln;
	// Free it using gate.
	~algowave.set(\gate, 0);
}, "/wave_free_SELECTIONSORT");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\status_wave_OSC_SELECTIONSORT, {
	if(x==0,
		{ Synth(\boot_wave_SELECTIONSORT); x = 1; },
		{}
	);
	~address.sendMsg("/hello");
}, "/hellowave_SELECTIONSORT");

)//--Parentheses end
