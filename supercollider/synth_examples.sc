/*
* @Author: ekzyis
* @Date:   30-01-2018 00:28:20
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:06:41
*/
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
x.free

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
x.free

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

/**
 * Bubble sound example.
 * Code sample from:
 * http://doc.sccode.org/Classes/DiskOut.html
 */
(
SynthDef(\bubbles, { |out|
    var f, zout;
    f = LFSaw.kr(0.4, 0, 24, LFSaw.kr([8,7.23], 0, 3, 80)).midicps; // glissando function
    zout = CombN.ar(SinOsc.ar(f, 0, 0.04), 0.2, 0.2, 4); // echoing sine wave
    Out.ar(out, zout)
}).add;
)
x = Synth(\bubbles)
x.free

/**
 * Example of destructive interference and importance of Order of Execution.
 * This only works, when both signals are in one synth
 * thus processed at the same time, not in a order of execution.
 */
(
SynthDef(\doublePhase, { |phase=0|
	var sig;
	sig = SinOsc.ar(440, [0,phase], 0.1);
	Out.ar(0, Mix(sig));
}).add;
)
x = Synth(\doublePhase);
// Sounds stops.
x.set(\phase, 1pi)
// Sound "starts" again but more quiet than with no phase difference.
x.set(\phase, 0.9pi)
(
SynthDef(\singlePhase, { |phase=0,freq=440|
	var sig;
	sig = SinOsc.ar(440, phase, 0.1);
	Out.ar(0, sig);
}).add;
)
x = Synth(\singlePhase);
y = Synth(\singlePhase);
/**
 * Depending on the exact time the synths were created (= their initial phase offset),
 * the interference of these two SinOsc will not be fully destructive when setting on oscilator's
 * to 0.5pi (=only adding to the initial phase offset).
 * When the initial phase offset was more close to pi (=half phase), the signal could even be amplified.
 */
s.queryAllNodes

/**
 * Default-synth from the SCClassLibrary.
 */
(
SynthDef(\default, {
		arg out=0, freq=440, amp=0.1, pan=0, gate=1;
		var z;
		z = LPF.ar(
			Mix.new(VarSaw.ar(freq + [0, Rand(-0.4,0.0), Rand(0.0,0.4)], 0, 0.3, 0.3)),
			XLine.kr(Rand(4000,5000), Rand(2500,3200), 1)
		) * Linen.kr(gate, 0.01, 0.7, 0.3, 2);
		OffsetOut.ar(out, Pan2.ar(z, pan, amp));
}).add;
)
x = Synth(\default)
x.free

/**
 * Modified default-synth ("fade edition").
 */
(
SynthDef(\default_fade, {
	arg freq=440, amp=0.1, pan=0, att=0.01, sustain=0.7, releaseTime=0.3;
	var z;
	z = LPF.ar(
			Mix.new(VarSaw.ar(freq + [0, Rand(-0.4,0.0), Rand(0.0,0.4)], 0, 0.3, 0.3)),
			XLine.kr(Rand(4000,5000), Rand(2500,3200), 1)
	) * Linen.kr(Line.kr(1,-0.01, 1), att, sustain, releaseTime, 2);
	Out.ar(0, Pan2.ar(z, pan, amp));
}).add;
)
Synth(\default_fade)
