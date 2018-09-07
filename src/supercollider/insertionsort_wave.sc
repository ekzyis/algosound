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
SynthDef(\wave_boot_INSERTIONSORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

/**
 * Synth which will be modified by individual element accesses while sorting.
 */
SynthDef(\wave_algowave_INSERTIONSORT, {
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
SynthDef(\wave_insert_INSERTIONSORT, {
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
OSCdef(\wave_boot_OSC_INSERTIONSORT, {
	"\\wave_boot_OSC_INSERTIONSORT".postln;
	Synth(\boot);
}, "/wave_boot_INSERTIONSORT");

// Define listener for start of algowave- and insert-synth.
OSCdef(\wave_start_OSC_INSERTIONSORT, {
	"\\wave_start_OSC_INSERTIONSORT".postln;
	~algowave = Synth(\wave_algowave_INSERTIONSORT);
	~insert = Synth.after(~algowave,\wave_insert_INSERTIONSORT, [\amp, 0]);
}, "/wave_start_INSERTIONSORT");

// Define listener for pausing of synths.
OSCdef(\wave_pause_OSC_INSERTIONSORT, {
	"\\wave_pause_OSC_INSERTIONSORT".postln;
	~algowave.set(\amptotal, 0);
	~insert.set(\amp, 0);
}, "/wave_pause_INSERTIONSORT");

// Define listener for resuming of synths.
OSCdef(\wave_resume_OSC_INSERTIONSORT, {
	"\\wave_resume_OSC_INSERTIONSORT".postln;
	~algowave.set(\amptotal, ~amp);
	~insert.set(\amp, ~insertamp);
}, "/wave_resume_INSERTIONSORT");

// Define listener for modifying.
OSCdef(\wave_set_OSC_INSERTIONSORT, {
	arg msg;
	"\\wave_mod_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~algowave.set(\amptotal, ~amp);
	~insert.set(\amp, ~insertamp);
	~algowave.set(\freq, msg[1]);
	~insert.set(\freq, msg[2]);
}, "/wave_set_INSERTIONSORT");

// Realtime modulating of synths
OSCdef(\wave_set_freqlag_OSC_INSERTIONSORT, {
	arg msg;
	"\\wave_mod_freqlag_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~algowave.set(\freqlag, msg[1]);
}, "/wave_set_freqlag_INSERTIONSORT");

~amp = 1;
~insertamp = 0.2;
OSCdef(\wave_set_amp_OSC_INSERTIONSORT, {
	arg msg;
	"\\wave_mod_amp_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~amp = msg[1];
}, "/wave_set_amp_INSERTIONSORT");

OSCdef(\wave_set_amplag_OSC_INSERTIONSORT, {
	arg msg;
	"\\wave_mod_amplag_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~algowave.set(\amplag, msg[1]);
}, "/wave_set_amplag_INSERTIONSORT");

OSCdef(\wave_set_pulsefreq_OSC_INSERTIONSORT, {
	arg msg;
	"\\wave_mod_pulsefreq_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	~insert.set(\pulsefreq, msg[1]);
}, "/wave_pulse_set_freq_INSERTIONSORT");

OSCdef(\wave_set_pulseamp_OSC_INSERTIONSORT, {
	arg msg;
	"\\wave_mod_pulseamp_OSC_INSERTIONSORT - arguments: [".post;msg[1].post;"]".postln;
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
OSCdef(\wave_free_OSC_INSERTIONSORT, {
	"\\wave_free_OSC_INSERTIONSORT".postln;
	// Free it using gate.
	~algowave.set(\gate, 0);
	~insert.set(\gate, 0);
}, "/wave_free_INSERTIONSORT");

// Create address to fire messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\wave_status_OSC_INSERTIONSORT, {
	if(x==0,
		{ Synth(\wave_boot_INSERTIONSORT); x = 1; },
		{}
	);
	~address.sendMsg("/hello");
}, "/wave_hello_INSERTIONSORT");

)//--Parentheses end
