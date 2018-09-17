FreqScope.new
Stethoscope.new
s.queryAllNodes

// Test synths after creating
x = Synth(\wave_boot_QUICKSORT);
y = Synth(\wave_algowave_QUICKSORT)
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
SynthDef(\wave_boot_QUICKSORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

/**
 * Synth which will be modified by individual element accesses while sorting.
 */
SynthDef(\wave_algowave_QUICKSORT, {
	arg freq=440, freqlag=0.1, amptotal=0.6, amp=0.2, amplag=0.5, gate=1;
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
OSCdef(\wave_boot_OSC_QUICKSORT, {
	arg msg;
	"\\wave_boot_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_boot_QUICKSORT");
		},
		{
			Synth(\wave_boot_QUICKSORT);
		}
	);
}, "/wave_boot_QUICKSORT");

// Define listener for start of synths.
OSCdef(\wave_start_OSC_QUICKSORT, {
	arg msg;
	"\\wave_start_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_start_QUICKSORT");
		},
		{
			~algowave1 = Synth(\wave_algowave_QUICKSORT);
			~algowave2 = Synth(\wave_algowave_QUICKSORT);
			~algowave3 = Synth(\wave_algowave_QUICKSORT);
		}
	);
}, "/wave_start_QUICKSORT");

// Define listener for pausing of synths.
OSCdef(\wave_pause_OSC_QUICKSORT, {
	arg msg;
	"\\wave_pause_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_pause_QUICKSORT");
		},
		{
			~algowave1.set(\amptotal, 0);
			~algowave2.set(\amptotal, 0);
			~algowave3.set(\amptotal, 0);
		}
	);
}, "/wave_pause_QUICKSORT");

// Define listener for resuming of synths.
OSCdef(\wave_resume_OSC_QUICKSORT, {
	arg msg;
	"\\wave_resume_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_resume_QUICKSORT");
		},
		{
			~algowave1.set(\amptotal, ~amp);
			~algowave2.set(\amptotal, ~amp);
			~algowave3.set(\amptotal, ~amp);
		}
	);
}, "/wave_resume_QUICKSORT");

// Define listeners for modifying.
OSCdef(\wave_set1_OSC_QUICKSORT, {
	arg msg;
	"\\wave_set1_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set1_QUICKSORT");
		},
		{
			~algowave1.set(\freq, msg[1]);
			~algowave1.set(\amptotal, ~amp);
		}
	);
}, "/wave_set1_QUICKSORT");
OSCdef(\wave_set2_OSC_QUICKSORT, {
	arg msg;
	"\\wave_set2_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set2_QUICKSORT");
		},
		{
			~algowave2.set(\freq, msg[1]);
			~algowave2.set(\amptotal, ~amp);
		}
	);
}, "/wave_set2_QUICKSORT");
OSCdef(\wave_set3_OSC_QUICKSORT, {
	arg msg;
	"\\wave_set3_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set3_QUICKSORT");
		},
		{
			~algowave3.set(\freq, msg[1]);
			~algowave3.set(\amptotal, ~amp);
		}
	);
}, "/wave_set3_QUICKSORT");

// Realtime modulating of synths
OSCdef(\wave_set_freqlag_OSC_QUICKSORT, {
	arg msg;
	"\\wave_set_freqlag_OSC_QUICKSORTT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_freqlag_QUICKSORT");
		},
		{
			~algowave.set(\freqlag, msg[1]);
		}
	);
}, "/wave_set_freqlag_QUICKSORT");

~amp = 1;
OSCdef(\wave_set_amp_OSC_QUICKSORT, {
	arg msg;
	"\\wave_set_amp_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_amp_QUICKSORT");
		},
		{
			~amp = msg[1];
		}
	);
}, "/wave_set_amp_QUICKSORT");

OSCdef(\wave_set_amplag_OSC_QUICKSORT, {
	arg msg;
	"\\wave_set_amplag_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_set_amplag_QUICKSORT");
		},
		{
			~algowave.set(\amplag, msg[1]);
		}
	);
}, "/wave_set_amplag_QUICKSORT");
/**
 * Define listener for freeing of synths.
 * KNOWN ISSUES: After freeing, another free-attempt will
 * cause a
 *  FAILURE IN SERVER /n_free Node XXXX not found
 * error.
 * Solution: Check if synth is already freed.
 * STATUS: Did not find a function like this :(
 * Tried with SYNTH.isNil but this leads to other possible
 * more severe bugs like orphaned synths.
 */
OSCdef(\wave_free_OSC_QUICKSORT, {
	arg msg;
	"\\wave_free_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_free_QUICKSORT");
		},
		{
			// Free it using gate.
			~algowave1.set(\gate, 0);
			~algowave2.set(\gate, 0);
			~algowave3.set(\gate, 0);
		}
	);
}, "/wave_free_QUICKSORT");

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\wave_status_OSC_QUICKSORT, {
	arg msg;
	"\\wave_status_OSC_QUICKSORT - arguments: [".post;msg[1].post;"]".postln;
	if(msg[1]=='status',
		{
			~address.sendMsg("/wave_hello_QUICKSORT");
		},
		{
			if(x==0,
				{ Synth(\wave_boot_QUICKSORT); x = 1; },
				{}
			);
		}
	);
}, "/wave_hello_QUICKSORT");

)//--Parentheses end
