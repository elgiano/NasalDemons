NasalDemons : MultiOutUGen {
	*ar {|numChannels, block, size=1, loop=1, rate=1, post=0|
		var addr = block.asArray.collect(_.addrLoForUGen).flop.collect(_.unbubble);
		var blockSize = (block.asArray.collect(_.bytes) * size).unbubble;
		^this.multiNew(*(['audio', numChannels] ++ addr ++ [ blockSize, loop, rate, post]));
	}

  init { arg argNumChannels ... theInputs;
		inputs = theInputs;
		^this.initOutputs(argNumChannels, rate);
	}
	argNamesInputsOffset { ^2 }

	// Get Memory Addresses

	*getMemoryBlocks{
		^switch(thisProcess.platform.name)
		{\linux}{this.prGetMemBlocksLinux}
		{\osx}{this.prGetMemBlocksOSX}
	}

	*getHeapBlocks{
		^switch(thisProcess.platform.name)
		{\linux}{this.prGetHeapBlocksLinux}
		{\osx}{this.prGetHeapBlocksOSX}
	}

	*getStackBlocks{
		^switch(thisProcess.platform.name)
		{\linux}{this.prGetStackBlocksLinux}
		{\osx}{this.prGetStackBlocksOSX}
	}

	*procMapsToBlocks{|stdout|
		^stdout.split($\n).collect(_.split($-)).rotate(1).drop(1).collect(NasalDemonsMemBlock(*_))
	}

	// Platform: LINUX

	*prGetMemBlocksLinux{
		^this procMapsToBlocks: "cat /proc/%/maps | grep '[^ ] r[w-][x-][p-] 00000000' | awk -F' ' '{print $1}'".format(Server.default.pid).unixCmdGetStdOut;
	}
	*prGetHeapBlocksLinux{
		^this procMapsToBlocks: "cat /proc/%/maps | grep 'heap' | awk -F' ' '{print $1}'".format(Server.default.pid).unixCmdGetStdOut
	}
	*prGetStackBlocksLinux{
		^this procMapsToBlocks: "cat /proc/%/maps | grep 'stack' | awk -F' ' '{print $1}'".format(Server.default.pid).unixCmdGetStdOut
	}

	// Platform: OSX

	*prGetMemBlocksOSX{
		^this procMapsToBlocks: "vmmap % | grep -o '[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}'".format(Server.default.pid).unixCmdGetStdOut;
	}
	*prGetHeapBlocksOSX{
		^this procMapsToBlocks: "vmmap % | grep \"^MALLOC\" |grep -o \"[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}\"".format(Server.default.pid).unixCmdGetStdOut
	}
	*prGetStackBlocksOSX{
		^this procMapsToBlocks: "vmmap % | grep -i \"^stack\" |grep -o \"[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}\"".format(Server.default.pid).unixCmdGetStdOut
	}

	checkInputs {
		/* TODO */
		^this.checkValidInputs;
	}
}

NasalDemonsMemBlock {
	var <>addrLo, <>addrHi;

	*new {|addrLo, addrHi|
		^super.newCopyArgs(addrLo,addrHi)
	}

	*hexToInt {|hexString|
		^ hexString.inject(0){ |result, char|
  			if(char.digit.notNil){
        		(result << 4) | char.digit;
    		} {
        		"Invalid hex digit found at % in %".format(char, hexString).warn;
        		result << 4;
    		};
		};
	}

	addrLoForUGen{
		^ addrLo.padLeft(16,"0").clump(4).collect(this.class.hexToInt(_))
	}

	bytes{
		^ [addrLo,addrHi].collect(this.class.hexToInt(_)).differentiate[1]
	}

	asString {
		^ "a %( %, % )".format(this.class, addrLo, addrHi)
	}
	debug {
		"a %[%-%](% bytes)".format(this.class, addrLo, addrHi, this.bytes).postln
	}

}
