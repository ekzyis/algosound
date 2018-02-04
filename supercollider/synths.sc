/**
 * Futuristic booting sound.
 */
(
SynthDef(\boot, {
	var ampEnv,freqEnv,src;
	ampEnv = EnvGen.ar(Env([0.01,1,1,0.01], [0.4,0.6,0.2], curve:\exp), doneAction:2);
	freqEnv = EnvGen.ar(Env([0.1,1,2.71828], [1,0.5], curve:\exp));
	src = SinOsc.ar(freqEnv*[2400,2200,2100,1200,1000], mul:0.1)*ampEnv;
	Out.ar(0, Pan2.ar(Mix(src),0));
}).add;
)
x = Synth(\boot);

/**
 * Algowave which will be modified by individual element accesses while sorting.
 * used @ bubblesort, insertionsort
 */
(
SynthDef(\algowave, {
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
)
x = Synth(\algowave);
x.free

/**
 * This synth represents the value of the current element to insert.
 * used @ insertionsort
 */
(
SynthDef(\insert, {
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
)
x = Synth(\insert);
x.free

/**
 * This synth reminds me a lot about loose connection. Maybe I can need this later for something?
 */
(
SynthDef(\looseconnection, {
	arg freq=880, density=10, att=0.1, decay=0.5, amp=0.1;
	var sig;
	sig = FreeVerb.ar(
		Decay2.ar(
			Dust.ar(density), att, decay, mul:SinOsc.ar(freq, mul:amp)
	));
	Out.ar(0, Mix(sig));
}).add;
)
x = Synth(\looseconnection)
x.set(\freq, 440)
x.set(\density, 20)
x.set(\decay, 0.2)
x.free

/**
 * Starting of something.
 */
(
SynthDef(\engine, {
	var sig,env;
	env = EnvGen.ar(Env([1,1,0.6],[3,2]));
	sig = Mix(Decay2.ar(Impulse.ar(XLine.ar(1,50,5)), 0.1, 0.2, mul:Resonz.ar(WhiteNoise.ar, [440*env,660*env,880*env], bwr:0.1)));
	Out.ar(0,Mix(sig));
}).add;
)
x = Synth(\engine)
x.free
