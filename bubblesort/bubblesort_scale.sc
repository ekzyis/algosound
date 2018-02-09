FreqScope.new
Stethoscope.new
s.queryAllNodes

(//--Parentheses begin

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

// SinOsc playing midi-notes.
SynthDef(\midisine, {
	arg midi=69, amp=0.1, atk=0.005, rel=0.3;
	var sig, env;
	env = EnvGen.kr(Env([0,1,0],[atk, rel]),doneAction:2);
	amp = amp * midi.clip(50,120).linexp(50,120,5,0.01);
	sig = SinOsc.ar(midi.midicps) * amp;
	sig = sig * env;
	Out.ar(0, Mix(sig)!2);
}).add;

// Define listener for boot sound.
OSCdef(\bootListener, {
	"playing boot sound.".postln;
	// Play boot sound
	Synth(\boot);
}, "/boot");

/**
 * Define listener for setting up of scale.
 * Setup depends on given minimal frequency and max frequency.
 */
OSCdef(\startListener, {
	arg msg;
	var min_freq, max_freq, min_midi, max_midi;
	~scales = nil;
	min_freq = msg[1];
	max_freq = msg[2];
	// Set closest even integer as minimal midi note.
	min_midi = min_freq.cpsmidi.round(2);
	// Calculate amount of scales with 12 steps per octave.
	d = ((max_freq.cpsmidi.round - min_midi)/12).round;
	(d+1).do{
		|i|
		~scales = ~scales ++ (Scale.major.degrees+(min_midi+(12*i)));
	};
	"scales=".post;~scales.postln;
}, "/midi_start");

// Define listener for playing a midi note.
OSCdef(\midiListener, {
	arg msg;
	var midi;
	i = ~scales.find([msg[1].cpsmidi.round]);
	if( i.isNil,
		{ midi = (msg[1].cpsmidi.round)-1 },
		{ midi = ~scales.at(i); },
	);
	"playing midi-note ".post;midi.postln;
	Synth(\midisine, [\midi, midi]);
}, "/midi_play");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/status");

)//--Parentheses end
