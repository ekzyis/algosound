FreqScope.new
Stethoscope.new
s.queryAllNodes

// Test synths after creating
x = Synth(\boot);
y = Synth(\algowave)
y.set(\gate, 0)
y.set(\freq, 700);
y.set(\freqlag, 1)
y.free
z = Synth(\minimum);

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

/**
 * Algowave which will be modified by individual element accesses while sorting.
 */
SynthDef(\algowave, {
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
SynthDef(\minimum, {
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
OSCdef(\bootListener, {
	"playing boot sound.".postln;
	// Play boot sound
	Synth(\boot);
}, "/boot");

// Define listener for start of sinewave.
OSCdef(\sortListener, {
	"creating synths.".postln;
	~algowave = Synth(\algowave);
}, "/wave_start");

// Define listener for pausing of sinewave.
OSCdef(\pauseListener, {
	"pausing sound.".postln;
	~algowave.set(\amptotal, 0);
}, "/wave_pause");

// Define listener for resuming of sinewave.
OSCdef(\resumeListener, {
	"resuming sound.".postln;
	~algowave.set(\amptotal, 0.3);
}, "/wave_resume");

// Define listener for modifying.
OSCdef(\modListener, {
	arg msg;
	~algowave.set(\amptotal, 0.3);
	~algowave.set(\freq, msg[1]);
}, "/wave_set");

OSCdef(\modListener2, {
	arg msg;
	Synth(\minimum, [\freq, msg[1]]);
}, "/min_set");

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
OSCdef(\freeListener, {
	"freeing synths.".postln;
	// Free it using gate.
	~algowave.set(\gate, 0);
}, "/wave_free");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/status");

)//--Parentheses end
