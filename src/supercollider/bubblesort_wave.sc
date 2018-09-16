FreqScope.new
Stethoscope.new
s.queryAllNodes

// Test synths after creating
x = Synth(\wave_boot_BUBBLESORT);
y = Synth(\wave_algowave_BUBBLESORT)
y.set(\gate, 0)
y.set(\freq, 700);
y.set(\freqlag, 1)
y.free

(//--Parentheses begin

// Create address to fire messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

/**
 * Futuristic booting sound.
 */
SynthDef(\wave_boot_BUBBLESORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

/**
 * Synth which will be modified by individual element accesses while sorting.
 */
SynthDef(\wave_algowave_BUBBLESORT, {
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
OSCdef(\wave_boot_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_boot_OSC_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_boot_BUBBLESORT");
		},
		{
			Synth(\wave_boot_BUBBLESORT);
		}
	);
}, "/wave_boot_BUBBLESORT");

// Define listener for start of algowave-synth.
OSCdef(\wave_start_BUBBLESORT, {
	arg msg;
	"\\wave_start_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_start_BUBBLESORT");
		},
		{
			~algowave = Synth(\wave_algowave_BUBBLESORT);
		}
	);
}, "/wave_start_BUBBLESORT");

// Define listener for pausing of algowave-synth.
OSCdef(\wave_pause_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_pause_OSC_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_pause_BUBBLESORT");
		},
		{
			~algowave.set(\amptotal, 0);
		}
	);
}, "/wave_pause_BUBBLESORT");

// Define listener for resuming of algowave-synth.
OSCdef(\wave_resume_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_resume_OSC_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_resume_BUBBLESORT");
		},
		{
			~algowave.set(\amptotal, ~amp);
		}
	);
}, "/wave_resume_BUBBLESORT");

// Define listener for modifying.
OSCdef(\wave_set_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_set_OSC_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_BUBBLESORT");
		},
		{
			~algowave.set(\freq, msg[1]);
			~algowave.set(\amptotal, ~amp);
		}
	);
}, "/wave_set_BUBBLESORT");

// Realtime modulating of synths
OSCdef(\wave_set_freqlag_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_set_freqlag_OSC_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=="status",
		{
			~address.sendMsg("/wave_set_freqlag_BUBBLESORT");
		},
		{
			~algowave.set(\freqlag, msg[1]);
		}
	);
}, "/wave_set_freqlag_BUBBLESORT");

~amp = 1;
OSCdef(\wave_set_amp_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_set_amp_OSC_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_amp_BUBBLESORT");
		},
		{
			~amp = msg[1];
		}
	);
}, "/wave_set_amp_BUBBLESORT");

OSCdef(\wave_set_amplag_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_set_amplag_OSC_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_amplag_BUBBLESORT");
		},
		{
			~algowave.set(\amplag, msg[1]);
		}
	);
}, "/wave_set_amplag_BUBBLESORT");
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
OSCdef(\wave_free_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_free_OSC_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_free_BUBBLESORT");
		},
		{ // Free it using gate.
			~algowave.set(\gate, 0);
		}
	);
}, "/wave_free_BUBBLESORT");

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\wave_status_OSC_BUBBLESORT, {
	arg msg;
	"\\wave_hello_BUBBLESORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_hello_BUBBLESORT");
		},
		{
			if(x==0,
				{ Synth(\wave_boot_BUBBLESORT); x = 1; },
				{}
			);
		}
	);
}, "/wave_hello_BUBBLESORT");

)//--Parentheses end
