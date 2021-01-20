ProcMemParser {

	*new { |...args|
		^switch (thisProcess.platform.name)
		{ \linux } { ^ProcMemParserLinux(*args) }
	}

}