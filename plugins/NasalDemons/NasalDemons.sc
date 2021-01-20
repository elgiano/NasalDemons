NasalDemons : MultiOutUGen {
	*ar {|numChannels, block, size=1, loop=1, rate=1, post=0|
		block = block.asUGenInput;
		if(block.rank > 1) {
			block = block.collect{|b| b[b.size - 1] = b.last * size }.flop
		} {
			block[block.size - 1] = block.last * size;
		};
		^this.multiNew(*['audio', numChannels] ++ block ++ [loop, rate, post]);
	}

	init { arg argNumChannels ... theInputs;
		inputs = theInputs;
		^this.initOutputs(argNumChannels, rate);
	}
	argNamesInputsOffset { ^2 }

	// Get Memory Addresses

	*getReadableBlocks {
		^ProcMemParser(Server.default.pid).getReadable
		.collect(NasalDemonsMemBlock.fromProcMem(_));
	}

	*getSpecialNonFileBlocks {
		^ProcMemParser(Server.default.pid).getSpecialNonFiles
		.collect(NasalDemonsMemBlock.fromProcMem(_));
	}

	*getNonFileBlocks {
		^ProcMemParser(Server.default.pid).getReadableNonFiles
		.collect(NasalDemonsMemBlock.fromProcMem(_));
	}

	*getHeapBlocks {
		^ProcMemParser(Server.default.pid).getHeap
		.collect(NasalDemonsMemBlock.fromProcMem(_));
	}

	*getStackBlocks {
		^ProcMemParser(Server.default.pid).getStack
		.collect(NasalDemonsMemBlock.fromProcMem(_));
	}

	/**procMapsToBlocks{|lines|
	^lines.collect(_.split($-)).collect(NasalDemonsMemBlock(*_))
	}*/

	/* Platform: OSX

	*prGetMemBlocksOSX{
	^this procMapsToBlocks: "vmmap % | grep \"r[w-][x-]/r[w-][x-]\" | grep -o '[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}'".format(Server.default.pid).unixCmdGetStdOutLines;
	}
	*prGetHeapBlocksOSX{
	^this procMapsToBlocks: "vmmap % | grep \"^MALLOC\" | grep \"r[w-][x-]/r[w-][x-]\" | grep -o \"[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}\"".format(Server.default.pid).unixCmdGetStdOutLines
	}
	*prGetStackBlocksOSX{
	^this procMapsToBlocks: "vmmap % | grep \"^Stack\" | grep \"r[w-][x-]/r[w-][x-]\" | grep -o \"[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}\"".format(Server.default.pid).unixCmdGetStdOutLines
	} */

	checkInputs {
		/* TODO */
		^this.checkValidInputs;
	}
}

NasalDemonsMemBlock {
	var <>addrLo, <>addrHi;
	var <info;

	*new { |addrLo, addrHi, info|
		^super.newCopyArgs(addrLo, addrHi, info)
	}

	*fromProcMem { |procMemInfo|
		var lo, hi;
		# lo, hi = procMemInfo[\address].split($-);
		^this.new(lo, hi, procMemInfo)
	}

	// WARNING: use only for control placeholders
	*newClear { ^this.new("0","0") }

	asControlInput {
		^this.addrLoForUGen ++ this.bytes
	}
	asArray { ^this.asControlInput }

	asUGenInput { ^this.asControlInput }

	addrLoForUGen{
		^addrLo.padLeft(16,"0").clump(4).collect(this.class.hexToInt(_))
	}

	bytes{
		^[addrLo,addrHi].collect(this.class.hexToFloat(_)).differentiate[1].asInteger
	}

	asString {
		^"a %( %, %%)".format(
			this.class, addrLo, addrHi,
			if (info.notNil) { ", " ++ (info[\path] ? "no path") } { " " }
		)
	}
	debug {
		"a %[%-%](% bytes)%".format(
			this.class, addrLo, addrHi, this.bytes,
			if (info.notNil) { ": " ++ (info[\path] ? "no path") } { "" }
		).postln
	}

	*hexToInt {|hexString|
		^hexString.inject (0) { |result, char|
			if (char.digit.notNil) {
				(result << 4) | char.digit;
			} {
				"Invalid hex digit found at % in %".format(char, hexString).warn;
				result << 4;
			};
		};
	}

	*hexToFloat { |hexString|
		^hexString.injectr (0.0) { |result, char, n|
			if (char.digit.notNil) {
				result = 16 ** n * char.digit + result;
			} {
				"Invalid hex digit found at % in %".format(char, hexString).warn;
			};
		};
	}


}
