// Show frequency analyzer.
FreqScope.new
// Show stethoscope.
Stethoscope.new
// Show local server's node tree.
s.queryAllNodes

/*
 * Frequency and amplitude modulation example.
 * (Modified) Code sample from:
 * http://composerprogrammer.com/teaching/supercollider/sctutorial/2.5%20More%20Synthesis%20Examples.html
 */
Chorus effect
x = {Mix(Saw.ar([440,443,437],mul:0.1))}.play;
x.free
(
SynthDef(\AMFM_example, {
	// Resonant filter.
	var source = Resonz.ar(
		// Arguments: input, freq, bandwith ratio
		// Frequency modulated sawtooth wave with chorusing.
		Saw.ar([440,443,437] + SinOsc.ar(100,mul:100)),
		// Varying filter bandwith over time.
		XLine.kr(10000,10,10),
		// Vary filter bandwith ratio over time.
		Line.kr(1,0.05,10),
		// Amplitude modulation
		mul: LFSaw.kr(Line.kr(3,17,3),mul:0.5,add:0.5)*Line.kr(1,0,10,doneAction:2)
	);
	source = Mix(source).dup;
	Out.ar(0,source);
}).add;
)
x = Synth(\AMFM_example)

/*
 * Looping envelope example.
 * (Modified) Code sample from:
 * http://composerprogrammer.com/teaching/supercollider/sctutorial/3.1%20Envelopes.html
 */
(
SynthDef(\env_example, {
	arg gate=1;
	var env = Env(
		[0.0,0.0,1.0,0.0],
		[0.5,1.0,2.0],
		// Loop from level index 2+1=3 (end index) to level index 0.
		/*
		 * This means, that the synth will loop from beginning to end
		 * until freed or gate is set to something smaller than or equal to 0.
		 * Using the gate will free the synth after a full loop has finished
		 * hence this will cause no abrupt interrupting of the sound.
		 */
		releaseNode:2,
		loopNode:0);
	var source = EnvGen.ar(env, gate, doneAction:2)*SinOsc.ar(550,mul:0.1).dup;
	Out.ar(0,source);
}).add
)
x = Synth(\env_example)
x.set(\gate, 0)

