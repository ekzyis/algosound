/*
* @Author: ekzyis
* @Date:   31-01-2018 20:46:03
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:00:20
*/
FreqScope.new
Stethoscope.new
s.queryAllNodes

// Test synths after creating
x = Synth(\boot);
y = Synth(\algowave)
y.set(\gate, 0)
y.set(\freq, 500);
y.set(\freqlag, 1)
y.free
z = Synth(\insert);
z.set(\freq, 440);
z.set(\pulsefreq, 5);
z.set(\att, 0.1);
z.set(\amp, 0.2);
z.set(\decay, 0.3);
z.set(\gate, 0);
z.free

(//--Parentheses begin
/**
 * Futuristic booting sound.
 */
SynthDef(\boot_wave_INSERTIONSORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

/**
 * Synth which will be modified by individual element accesses while sorting.
 */
SynthDef(\algowave_wave_INSERTIONSORT, {
	arg freq=440, freqlag=0.1, amptotal=1, amp=0.2, amplag=0.5, gate=1;
	var sig, ampmod, env;
	// Make higher pitches less loud.
	freq = [freq*0.6, freq*0.8, freq, freq*1.2];
	ampmod = freq.expexp(200,4000,amp,0.02);
	env = EnvGate(1,gate,amplag,doneAction:2);
	sig = SinOsc.ar(
		Lag.kr(freq,freqlag),
		mul:Lag.kr(ampmod, amplag)*Lag.kr(amptotal,amplag));
	sig = sig * env;
	Out.ar(0, Mix(sig)!2);
}).add;

/**
 * Synth which represents the value of the element to insert.
 */
SynthDef(\insert_wave_INSERTIONSORT, {
	arg freq=440, pulsefreq=10, amp=0.2, att=0.1, decay=0.5, amplag=0.5, gate=1;
	var sig, env;
	sig = Mix(
		Decay2.ar(
			Impulse.ar(pulsefreq, [0.5,0.45], amp), att, decay, Saw.ar(freq)
	));
	env = EnvGate(1,gate,amplag, doneAction:2);
	sig = sig * env;
	sig = Pan2.ar(sig, 0, amp);
	Out.ar(0, sig);
}).add;

// Define listener for boot sound.
OSCdef(\boot_wave_OSC_INSERTIONSORT, {
	"\\boot_wave_OSC_INSERTIONSORT".postln;
	Synth(\boot);
}, "/boot_wave_INSERTIONSORT");

// Define listener for start of algowave- and insert-synth.
OSCdef(\start_wave_OSC_INSERTIONSORT, {
	"\\start_wave_OSC_INSERTIONSORT".postln;
	~algowave = Synth(\algowave_wave_INSERTIONSORT);
	~insert = Synth.after(~algowave,\insert_wave_INSERTIONSORT, [\amp, 0]);
}, "/wave_start_INSERTIONSORT");

// Define listener for pausing of synths.
OSCdef(\pause_wave_OSC_INSERTIONSORT, {
	"\\pause_wave_OSC_INSERTIONSORT".postln;
	~algowave.set(\amptotal, 0);
	~insert.set(\amp, 0);
}, "/wave_pause_INSERTIONSORT");

// Define listener for resuming of synths.
OSCdef(\resume_wave_OSC_INSERTIONSORT, {
	"\\resume_wave_OSC_INSERTIONSORT".postln;
	~algowave.set(\amptotal, ~amp);
	~insert.set(\amp, ~insertamp);
}, "/wave_resume_INSERTIONSORT");

// Define listener for modifying.
OSCdef(\mod_wave_OSC_INSERTIONSORT, {
	arg msg;
	"\\mod_wave_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~algowave.set(\amptotal, ~amp);
	~insert.set(\amp, ~insertamp);
	~algowave.set(\freq, msg[1]);
	~insert.set(\freq, msg[2]);
}, "/wave_set_INSERTIONSORT");

// Realtime modulating of synths
OSCdef(\mod_wave_freqlag_OSC_INSERTIONSORT, {
	arg msg;
	"\\mod_wave_freqlag_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~algowave.set(\freqlag, msg[1]);
}, "/wave_set_freqlag_INSERTIONSORT");

~amp = 1;
~insertamp = 0.2;
OSCdef(\mod_wave_amp_OSC_INSERTIONSORT, {
	arg msg;
	"\\mod_wave_amp_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~amp = msg[1];
}, "/wave_set_amp_INSERTIONSORT");

OSCdef(\mod_wave_amplag_OSC_INSERTIONSORT, {
	arg msg;
	"\\mod_wave_amplag_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~algowave.set(\amplag, msg[1]);
}, "/wave_set_amplag_INSERTIONSORT");

OSCdef(\mod_wave_pulsefreq_OSC_INSERTIONSORT, {
	arg msg;
	"\\mod_wave_pulsefreq_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~insert.set(\pulsefreq, msg[1]);
}, "/wave_pulse_set_freq_INSERTIONSORT");

OSCdef(\mod_wave_pulseamp_OSC_INSERTIONSORT, {
	arg msg;
	"\\mod_wave_pulseamp_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~insertamp = msg[1];
}, "/wave_pulse_set_amp_INSERTIONSORT");
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
OSCdef(\free_wave_OSC_INSERTIONSORT, {
	"\\free_wave_OSC_INSERTIONSORT".postln;
	// Free it using gate.
	~algowave.set(\gate, 0);
	~insert.set(\gate, 0);
}, "/wave_free_INSERTIONSORT");

// Create address to fire messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\status_wave_OSC_INSERTIONSORT, {
	if(x==0,
		{ Synth(\boot_wave_INSERTIONSORT); x = 1; },
		{}
	);
	~address.sendMsg("/hello");
}, "/hellowave_INSERTIONSORT");

)//--Parentheses end
