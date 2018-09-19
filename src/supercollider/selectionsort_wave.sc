FreqScope.new
Stethoscope.new
s.queryAllNodes

// Test synths after creating
x = Synth(\wave_boot_SELECTIONSORT);
y = Synth(\wave_algowave_SELECTIONSORT)
y.set(\gate, 0)
y.set(\freq, 700);
y.set(\freqlag, 1)
y.free
z = Synth(\wave_minimum_SELECTIONSORT);

(//--Parentheses begin

// Create address to fire messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

/**
 * Futuristic booting sound.
 */
SynthDef(\wave_boot_SELECTIONSORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

/**
 * Synth which will be modified by individual element accesses while sorting.
 */
SynthDef(\wave_algowave_SELECTIONSORT, {
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
SynthDef(\wave_minimum_SELECTIONSORT, {
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
OSCdef(\wave_boot_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_boot_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_boot_SELECTIONSORT");
		},
		{
			Synth(\wave_boot_SELECTIONSORT);
		}
	);
}, "/wave_boot_SELECTIONSORT");

// Define listener for start of algowave-synth.
OSCdef(\wave_start_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_start_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_start_SELECTIONSORT");
		},
		{
			~algowave = Synth(\wave_algowave_SELECTIONSORT);
		}
	);
}, "/wave_start_SELECTIONSORT");

// Define listener for pausing of algowave-synth.
OSCdef(\wave_pause_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_pause_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_pause_SELECTIONSORT");
		},
		{
			~algowave.set(\amptotal, 0);
		}
	);
}, "/wave_pause_SELECTIONSORT");

// Define listener for resuming of algowave-synth.
OSCdef(\wave_resume_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_resume_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_resume_SELECTIONSORT");
		},
		{
			~algowave.set(\amptotal, ~amp);
		}
	);
}, "/wave_resume_SELECTIONSORT");

// Define listener for modifying.
OSCdef(\wave_set_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_set_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_SELECTIONSORT");
		},
		{
			~algowave.set(\amptotal, ~amp);
			~algowave.set(\freq, msg[1]);
		}
	);
}, "/wave_set_SELECTIONSORT");

OSCdef(\wave_min_set_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_min_set_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_min_set_SELECTIONSORT");
		},
		{
			Synth(\wave_minimum_SELECTIONSORT, [\amp, ~minamp, \freq, msg[1]]);
		}
	);
}, "/wave_min_set_SELECTIONSORT");

// Realtime modulating of synths
OSCdef(\wave_set_freqlag_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_set_freqlag_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_freqlag_SELECTIONSORT");
		},
		{
			~algowave.set(\freqlag, msg[1]);
		}
	);
}, "/wave_set_freqlag_SELECTIONSORT");

~amp = 1;
OSCdef(\wave_set_amp_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_set_amp_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_amp_SELECTIONSORT");
		},
		{
			~amp = msg[1];
		}
	);
}, "/wave_set_amp_SELECTIONSORT");

OSCdef(\wave_set_amplag_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_set_amplag_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_amplag_SELECTIONSORT");
		},
		{
			~algowave.set(\amplag, msg[1]);
		}
	);
}, "/wave_set_amplag_SELECTIONSORT");

~minamp = 0.2;
OSCdef(\wave_min_set_amp_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_min_set_amp_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_min_set_amp_SELECTIONSORT");
		},
		{
			~minamp = msg[1];
		}
	);
}, "/wave_min_set_amp_SELECTIONSORT");

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
OSCdef(\wave_free_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_free_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_free_SELECTIONSORT");
		},
		{
			// Free it using gate.
			~algowave.set(\gate, 0);
		}
	);
}, "/wave_free_SELECTIONSORT");

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\wave_status_OSC_SELECTIONSORT, {
	arg msg;
	"\\wave_status_OSC_SELECTIONSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_hello_SELECTIONSORT");
		},
		{
			if(x==0,
				{ Synth(\wave_boot_SELECTIONSORT); x = 1; },
				{}
			);
		}
	);
}, "/wave_hello_SELECTIONSORT");

)//--Parentheses end
