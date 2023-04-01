all: TraceAgentSO GenAndTraceJar CalculateLoopHeadsJar JavaChecker bm_suite


bm_suite:
	make -C benchmarking/termination-suite/sv-comp/termination-crafted-lit
	make -C benchmarking/termination-suite/term-comp/Aprove_09
	make -C benchmarking/termination-suite/nuTerm-advantage/Java
	make -C benchmarking/termination-suite/loopInvSuite


deps: TraceAgentSO GenAndTraceJar CalculateLoopHeadsJar JavaChecker
	git submodule update --init --recursive

z3: #submodules
	wget -P libs https://github.com/Z3Prover/z3/archive/refs/tags/z3-4.11.0.zip
	unzip -o libs/z3-4.11.0.zip -d libs
	mkdir -p ${PWD}/libs/z3-z3-4.11.0/build
	cd ${PWD}/libs/z3-z3-4.11.0/build && \
	cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=Release \
				-DZ3_BUILD_JAVA_BINDINGS=True \
				-DZ3_INSTALL_JAVA_BINDINGS=True \
				-DCMAKE_INSTALL_PREFIX=${PWD}/libs/ .. && \
	make install
	patch -d deps/javachecker/ < libs/javachecker.patch

TraceAgentSO: #submodules
	cd ${PWD}/deps/traceAgent/ && cmake .
	make -C ${PWD}/deps/traceAgent
	mv ${PWD}/deps/traceAgent/libJavaMemTraceAgent.so ${PWD}/libs/libJavaMemTraceAgent.so

GenAndTraceJar:
	${PWD}/deps/GenAndTrace/gradlew fatJar -p ${PWD}/deps/GenAndTrace
	mv ${PWD}/deps/GenAndTrace/build/libs/GenAndTrace-1.0-SNAPSHOT.jar ${PWD}/libs/GenAndTrace.jar 

CalculateLoopHeadsJar:
	${PWD}/deps/CalculateLoopHeads/gradlew fatJar -p ${PWD}/deps/CalculateLoopHeads
	mv ${PWD}/deps/CalculateLoopHeads/build/libs/CalculateLoopHeads-1.0-SNAPSHOT.jar ${PWD}/libs/CalculateLoopHeads.jar 

JavaChecker: #submodules
	#git --git-dir ${PWD}/deps/javachecker/.git  checkout objects
	${PWD}/deps/javachecker/gradlew uberJar -p ${PWD}/deps/javachecker
	mv ${PWD}/deps/javachecker/build/libs/javachecker-uber.jar ${PWD}/libs

run_experim:
	make -C benchmarking run_experim
