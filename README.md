# algosound
Project for a computermusic seminar about SuperCollider and SonicPi.

This projects goal is to create visualization and sonification of algorithms using Processing and SuperCollider.
A good example is this video on YouTube: https://www.youtube.com/watch?v=kPRA0W1kECg&t=36s
While in the video there are only sorting algorithms, other algorithms like Dijkstra and A* should also be included in this project.

If possible, the sonification should be independent of the algorithm and visual implementation. This will ultimately - as far as I can see - lead to parsing, analyzing and rewriting of the Processing source code to include SuperCollider calls.

First, I am going to make some basic visualization of different algorithms and sonificate them with SuperCollider by using  the network protocol OSC. So to say, I will first do what in the end the "compiling" algorithm should do by itself.

#### Update 29.01.18
After some first builds, research about the topic of parsing and trying the sonification, I came to the conclusion that before parsing can be attempted, a deep understanding of SuperCollider, OSC and sonification of algorithms will be needed to produce results worth mentioning. Therefore, I will focus on implementing a sonification I am fine with. This means the following list of algorithms should be sonificated and visualized while being able to work on any given set:
  - Bubblesort
  - Selectionsort
  - Insertionsort
  - Mergesort
  - Quicksort
  - A*
  - Dijkstra
  
 At this point, all sorting algorithms have been implemented and visualized + a basic sonification of bubblesort.
 
#### Update 09.02.18
All sorting algorithms have been sonificated now.
Sonification consists mainly of a sinewave which is modulated while sorting. The heights of the current accessed elements is mapped to a frequency range. This mapped value is then sent to SuperCollider through OSC, setting the frequency for the sinewave. To prevent sound artifacts due to sudden change of a parameter / to smooth the signal, a Lag UGen is used for the amplitude and frequency. The sinewave is called 'algowave' since the algorithm modulates the (sine)wave. This is the "Generation 1 Sonification". Due to the unharmonic nature of this implementation (which was expected), work on the Gen 2 Sonification has begun. To achieve a more harmonic sound, scales and midi-notes will be used.

This Gen 2 Sonification has been already implemented in bubblesort.

Implementation of the graph algorithms has not started yet because I want to focus more on synths and sonification before starting to implement two algorithms from scratch, including a whole new visualization.
