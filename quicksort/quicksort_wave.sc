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
 * Synth which will be modified by individual element accesses while sorting.
 */
SynthDef(\algowave, {
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
OSCdef(\bootListener, {
	"playing boot sound.".postln;
	Synth(\boot);
}, "/boot");

// Define listener for start of synths.
OSCdef(\sortListener, {
	"creating multiple algowaves".postln;
	~algowave1 = Synth(\algowave);
	~algowave2 = Synth(\algowave);
	~algowave3 = Synth(\algowave);
}, "/wave_start");

// Define listener for pausing of synths.
OSCdef(\pauseListener, {
	"pausing synths.".postln;
	~algowave1.set(\amptotal, 0);
	~algowave2.set(\amptotal, 0);
	~algowave3.set(\amptotal, 0);
}, "/wave_pause");

// Define listener for resuming of synths.
OSCdef(\resumeListener, {
	"resuming synths.".postln;
	~algowave1.set(\amptotal, 0.6);
	~algowave2.set(\amptotal, 0.6);
	~algowave3.set(\amptotal, 0.6);
}, "/wave_resume");

// Define listeners for modifying.
OSCdef(\modListener1, {
	arg msg;
	~algowave1.set(\freq, msg[1]);
}, "/wave_set1");
OSCdef(\modListener2, {
	arg msg;
	~algowave2.set(\freq, msg[1]);
}, "/wave_set2");
OSCdef(\modListener3, {
	arg msg;
	~algowave3.set(\freq, msg[1]);
}, "/wave_set3");
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
OSCdef(\freeListener, {
	"freeing synths.".postln;
	// Free it using gate.
	~algowave1.set(\gate, 0);
	~algowave2.set(\gate, 0);
	~algowave3.set(\gate, 0);
}, "/wave_free");

// Create address to send messages to Processing client
~address = NetAddr.new("127.0.0.1", 12000);

// Define listener for checking if sc3-server is running.
OSCdef(\statuslistener, {
	~address.sendMsg("/hello");
}, "/hellowave");

)//--Parentheses end
