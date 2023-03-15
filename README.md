# Automated Ranking Function Generator for Program Termination Analysis
[Name will be and MUST be changed later]

### Problem sets
The problem sets can be found in:
 - benchmarking/termination-suite/term-comp/Aprove_09/
 - benchmarking/termination-suite/sv-comp/termination-crafted-lit/terminating/
 - benchmarking/termination-suite/nuTerm-advantage/Java/


### Requirements
general:
- wget
- unzip
- Make
- CMake
- OpenJDK 11 JDK
- python 3.9

python packages:
- torch
- sympy
- pyjnius (NOT jnius)
- matplotlib
- datetime
- pandas
- numpy
- tqdm
- termcolor
- psutil

the environment variable JAVAC needs to be set to a JAVA 11 javac binary. For example with `export JAVAC=/path/javac`

### Updates

- Feb 29: Factored the source code and implementation of anticorr_sumofrelu strategy.
