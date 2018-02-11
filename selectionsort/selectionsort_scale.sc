FreqScope.new
Stethoscope.new
s.queryAllNodes

Synth(\midisine_scale_SELECTIONSORT);
Synth(\default_midifade_scale_SELECTIONSORT);

(//--Parentheses begin

/**
 * Futuristic booting sound.
 */
SynthDef(\boot_scale_SELECTIONSORT, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.kr(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.kr(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;

// Sinewave osc playing midi-notes.
SynthDef(\midisine_scale_SELECTIONSORT, {
	arg midi=69, amp=0.1, atk=0.005, rel=0.3;
	var sig, env;
	env = EnvGen.kr(Env([0,1,0],[atk, rel]),doneAction:2);
	amp = amp * midi.clip(50,120).linexp(50,120,2,0.01);
	sig = SinOsc.ar(midi.midicps) * amp;
	sig = sig * env;
	Out.ar(0, Mix(sig)!2);
}).add;

// Modified default-synth ("fade+midi edition").
SynthDef(\default_midifade_scale_SELECTIONSORT, {
	arg midi=69, amp=0.5, pan=0, att=0.005, sustain=0.2, releaseTime=0.1;
	var z;
	z = LPF.ar(
			Mix.new(VarSaw.ar(midi.midicps + [0, Rand(-0.4,0.0), Rand(0.0,0.4)], 0, 0.3, 0.3)),
			XLine.kr(Rand(4000,5000), Rand(2500,3200), 1)
	) * Linen.kr(Line.kr(1,-0.01, att+sustain+releaseTime), att, sustain, releaseTime, 2);
	Out.ar(0, Pan2.ar(z, pan, amp));
}).add;

// Define listener for boot sound.
OSCdef(\boot_scale_OSC_SELECTIONSORT, {
	"playing boot sound.".postln;
	// Play boot sound
	Synth(\boot_scale_SELECTIONSORT);
}, "/boot_scale_SELECTIONSORT");

/**
 * Define listener for setting up of scale.
 * Setup depends on given minimal frequency and max frequency.
 */
OSCdef(\start_scale_OSC_SELECTIONSORT, {
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
}, "/scale_start_SELECTIONSORT");

// Define listener for playing a midi note.
OSCdef(\midiplay_scale_OSC_SELECTIONSORT, {
	arg msg;
	var midi;
	i = ~scales.find([msg[1].cpsmidi.round]);
	if( i.isNil,
		{ midi = (msg[1].cpsmidi.round)-1 },
		{ midi = ~scales.at(i); },
	);
	"playing midi-note ".post;midi.postln;
	"pan=".post;msg[2].postln;
	Synth(\midisine_scale_SELECTIONSORT, [\midi, midi, \rel, rrand(0.1,1.75)]);
}, "/scale_play_SELECTIONSORT");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

x = 0;
// Define listener for checking if sc3-server is running.
OSCdef(\status_scale_OSC_SELECTIONSORT, {
	if(x==0,
		{ Synth(\boot_scale_SELECTIONSORT); x = 1; },
		{}
	);
	~address.sendMsg("/hello");
}, "/helloscale_SELECTIONSORT");

)//--Parentheses end
