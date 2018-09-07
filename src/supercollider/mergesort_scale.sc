FreqScope.new
Stethoscope.new
s.queryAllNodes

Synth(\scale_midisine_MERGESORT);
Synth(\scale_default_midifade_MERGESORT);

(//--Parentheses begin

/**
 * Futuristic booting sound.
 */
SynthDef(\scale_boot_MERGESORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

// Sinewave osc playing midi-notes.
SynthDef(\scale_midisine_MERGESORT, {
	arg midi=69, amp=0.1, atk=0.005, rel=0.30, pan=0;
	var sig, env;
	env = EnvGen.kr(Env([0,1,0],[atk, rel]),doneAction:2);
	amp = amp * midi.clip(50,120).linexp(50,120,2,0.01);
	sig = SinOsc.ar(midi.midicps);
	sig = sig * env;
	Out.ar(0, Pan2.ar(sig, pan, amp));
}).add;

// Modified default-synth ("fade+midi edition").
SynthDef(\scale_default_midifade_MERGESORT, {
	arg midi=69, amp=0.5, pan=0, att=0.005, sustain=0.2, releaseTime=0.1, panlag=0.01;
	var z;
	z = LPF.ar(
			Mix.new(VarSaw.ar(midi.midicps + [0, Rand(-0.4,0.0), Rand(0.0,0.4)], 0, 0.3, 0.3)),
			XLine.kr(Rand(4000,5000), Rand(2500,3200), 1)
	) * Linen.kr(Line.kr(1,-0.01, att+sustain+releaseTime), att, sustain, releaseTime, 2);
	Out.ar(0, Pan2.ar(z, Lag.kr(pan,panlag), amp));
}).add;

// Define listener for boot sound.
OSCdef(\scale_boot_OSC_MERGESORT, {
	"playing boot sound.".postln;
	// Play boot sound
	Synth(\scale_boot_MERGESORT);
}, "/scale_boot_MERGESORT");

/**
 * Define listener for setting up of scale.
 * Setup depends on given minimal frequency and max frequency.
 */
OSCdef(\scale_start_OSC_MERGESORT, {
	arg msg;
	var min_freq, max_freq, min_midi, max_midi;

	// Generate scale
	~scales = nil;
	min_freq = msg[1];
	max_freq = msg[2];
	// Set closest even integer as minimal midi note.
	min_midi = min_freq.cpsmidi.round(2);
	// Calculate amount of scales with 12 steps per octave.
	d = ((max_freq.cpsmidi.round - min_midi)/12).round;
	(d+1).do{
		|i|
		~scales = ~scales ++ (Scale.minor.degrees+(min_midi+(12*i)));
	};
	"scales=".post;~scales.postln;

	/**
	 * Generate a random sequence of duration times.
	 * TODO:
	 * There is a lot potential in this being very useful when
	 * accessible during runtime through UI.
	 */
	/*~durations = Array.fill(12,{
		Array.fill(6,{ arg i; (i*0.25) + 0.25;}).choose
	}); // Array.fill inception
	"durations=".post;~durations.postln;*/
}, "/scale_start_MERGESORT");

OSCdef(\scale_set_maxfreq_OSC_MERGESORT, {
	arg msg;
	"\\scale_set_maxfreq_OSC_MERGESORT - arguments: [".post;msg[1].post;"]".postln;
	~initscale.value(msg: [msg[0], ~minfreq, msg[1]]);
}, "/scale_set_maxfreq_MERGESORT");

OSCdef(\scale_set_minfreq_OSC_MERGESORT, {
	arg msg;
	"\\scale_set_minfreq_OSC_MERGESORT - arguments: [".post;msg[1].post;"]".postln;
	~initscale.value(msg: [msg[0], msg[1], ~maxfreq]);
}, "/scale_set_minfreq_MERGESORT");

~amp = 0.1;
OSCdef(\scale_set_amp_OSC_MERGESORT, {
	arg msg;
	"\\scale_set_amp_OSC_MERGESORT - arguments: [";msg[1].post;"]".postln;
	~amp = msg[1];
}, "/scale_set_amp_MERGESORT");

// Define listener for playing a midi note.
OSCdef(\scale_set_OSC_MERGESORT, {
	arg msg;
	var midi;
	i = ~scales.find([msg[1].cpsmidi.round]);
	if( i.isNil,
		{ midi = (msg[1].cpsmidi.round)-1 },
		{ midi = ~scales.at(i); },
	);
	"playing midi-note ".post;midi.postln;
	"pan=".post;msg[2].postln;
	Synth(\scale_midisine_MERGESORT, [\midi, midi, \rel, rrand(0.1,1.75), \pan, msg[2], \amp, ~amp]);
}, "/scale_set_MERGESORT");

// Create address to fire messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\scale_status_OSC_MERGESORT, {
	if(x==0,
		{ Synth(\scale_boot_MERGESORT); x = 1; },
		{}
	);
	~address.sendMsg("/hello");
}, "/scale_hello_MERGESORT");

)//--Parentheses end
