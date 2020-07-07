NasalDemons : MultiOutUGen {
	*ar {|numChannels, addr, size=1, loop=1, rate=1, post=0|
		^this.multiNew(*(['audio', numChannels] ++ this.hexAddrToArgs(addr) ++ [size, loop, rate, post]));
	}

    init { arg argNumChannels ... theInputs;
		inputs = theInputs;
		^this.initOutputs(argNumChannels, rate);
	}
	argNamesInputsOffset { ^2 }

	*hexAddrToArgs{|hexAddrString|
		^hexAddrString.padLeft(16,"0").clump(4).collect(_.hexToInt)
	}

	*hexAddrsToSize{|addrLo, addrHi|
		^[addrLo,addrHi].collect{|a| a.injectr(0){|t,c,n| 16**n *c.digit + t} }
		.differentiate[1]
	}

	// Get Memory Addresses

	*getMemoryAddresses{
		^switch(thisProcess.platform.name)
		{\linux}{this.prGetMemAddrsLinux}
		{\osx}{this.prGetMemAddrsOSX}
	}

	*getHeapAddresses{
		^switch(thisProcess.platform.name)
		{\linux}{this.prGetHeapAddrsLinux}
		{\osx}{this.prGetHeapAddrsOSX}
	}

	*getStackAddresses{
		^switch(thisProcess.platform.name)
		{\linux}{this.prGetStackAddrsLinux}
		{\osx}{this.prGetStackAddrsOSX}
	}

	*procMapsToAddrList{|stdout|
		^stdout.split($\n).collect(_.split($-)).rotate(1).drop(1)
	}

	// Platform: LINUX

	*prGetMemAddrsLinux{
		^this procMapsToAddrList: "cat /proc/%/maps | grep '[^ ] r[w-][x-][p-] 00000000' | awk -F' ' '{print $1}'".format(Server.default.pid).unixCmdGetStdOut;
	}
	*prGetHeapAddrsLinux{
		^this procMapsToAddrList: "cat /proc/%/maps | grep 'heap' | awk -F' ' '{print $1}'".format(Server.default.pid).unixCmdGetStdOut
	}
	*prGetStackAddrsLinux{
		^this procMapsToAddrList: "cat /proc/%/maps | grep 'stack' | awk -F' ' '{print $1}'".format(Server.default.pid).unixCmdGetStdOut
	}

	// Platform: OSX

	*prGetMemAddrsOSX{
		^this procMapsToAddrList: "vmmap % | grep -o '[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}'".format(Server.default.pid).unixCmdGetStdOut;
	}
	*prGetHeapAddrsOSX{
		^this procMapsToAddrList: "vmmap % | grep \"^MALLOC\" |grep -o \"[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}\"".format(Server.default.pid).unixCmdGetStdOut
	}
	*prGetStackAddrsOSX{
		^this procMapsToAddrList: "vmmap % | grep -i \"^stack\" |grep -o \"[0-9a-fA-F]\\{16\\}-[0-9a-fA-F]\\{16\\}\"".format(Server.default.pid).unixCmdGetStdOut
	}



	checkInputs {
		/* TODO */
		^this.checkValidInputs;
	}
}
