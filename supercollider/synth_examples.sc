// Show frequency analyzer.
FreqScope.new
// Show stethoscope.
Stethoscope.new(s)
// Show local server's node tree.
s.queryAllNodes

// Groups for specific source and effect synths.
~sourceGrp = Group.new;
~fxGrp = Group.after(~sourceGrp);

/*
 * Frequency and amplitude modulation example.
 * (Modified) Code sample from:
 * http://composerprogrammer.com/teaching/supercollider/sctutorial/2.5%20More%20Synthesis%20Examples.html
 */
// Chorus effect
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
		[0.2,0.5,1.5],
		// Loop from level index 2+1=3 (end index) to level index 0.
		/*
		 * This means, that the synth will loop from beginning to end
		 * until freed or gate is set to something smaller than or equal to 0.
		 * Using the gate will free the synth after a full loop has finished
		 * hence this will cause no abrupt interrupting of the sound.
		 */
		releaseNode:2,
		loopNode:0);
	var source = SinOsc.ar(550,mul:0.1*EnvGen.ar(env, gate, doneAction:2)).dup;
	Out.ar(0,source);
}).add
)
x = Synth(\env_example)
x.set(\gate, 0)

/**
 * Effect example with reverb.
 * https://www.youtube.com/watch?v=VGs_lMw2hQg&index=8&list=PLPYzvS8A_rTaNDweXe6PX4CXSGq4iEWYC
 */
~reverbBus = Bus.audio(s, 1);
(
SynthDef(\reverb_example, {
	arg in, out=0;
	var sig;
	sig = In.ar(in, 2);
	sig = FreeVerb.ar(sig);
	Out.ar(out, sig);
}).add;
)
(
SynthDef(\blip_example, {
	arg out;
	var freq, trig, sig;
	freq = LFNoise0.kr(3).exprange(300,1200).round(300);
	sig = SinOsc.ar(freq) * 0.25;
	trig = Dust.kr(2);
	sig = sig * EnvGen.kr(Env.perc(0.01, 0.2), trig);
	sig = Pan2.ar(sig, LFNoise1.kr(10));
	Out.ar(out, sig);
}).add;
)
x = Synth.before(y,\blip_example, [\out, ~reverbBus], ~sourceGrp);
x.free
y = Synth(\reverb_example, [\in, ~reverbBus], ~fxGrp);
y.free


/**
 * Example of using patterns with Pdef.
 * (Modified) Code sample from:
 * https://www.youtube.com/watch?v=nB_bVJ1c1Rg&index=11&list=PLPYzvS8A_rTaNDweXe6PX4CXSGq4iEWYC
 */
(
SynthDef(\sine, {
	arg freq=440, atk=0.005, rel=0.3, amp=1, pan=0;
	var sig, env;
	sig = SinOsc.ar(freq);
	env = EnvGen.kr(Env([0,1,0],[atk,rel]),doneAction:2);
	sig = Pan2.ar(sig, pan, amp);
	sig = sig * env;
	Out.ar(0, sig);
}).add;
)
(
Pdef(
	\sinepat,
	Pbind(
		\instrument, \sine,
		// Time between synths. Linear distribution between 0.05 and 0.5.
		\dur, Pwhite(0.05, 0.5, inf),
		// Only works when pitch-argument is called freq in synth. See hierarchy in Pbind-documentation.
		\midinote, Pseq([35],inf),
		\harmonic, Pexprand(1, 80, inf).round,
		\atk, Pwhite(0.01,1.0, inf),
		\rel, Pwhite(1.0, 2.0, inf),
		\amp, Pkey(\harmonic).reciprocal * 0.3,
		\pan, Pwhite(-0.8, 0.8, inf),
	);
).play;
)
