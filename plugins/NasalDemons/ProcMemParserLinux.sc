ProcMemParserLinux {

	var <entries;

	*new { |pid|
		^super.new.init(pid)
	}

	init { |pid|
		entries = [];
		this.read(pid)
	}

	read { |pid=(Server.default.pid)|
		var lines = "cat /proc/%/maps".format(pid).unixCmdGetStdOutLines;
		var keys = #[\address, \perms, \offset, \dev, \inode, \path];
		entries = lines.collect {|line|
			var values = line.findRegexp("[^ ]+").flop[1];
			[keys, values].lace.asDict;
		}
	}

	getReadable {
		^entries select: { |entry| entry[\perms][0] == $r }
	}

	getReadableFiles {
		^this.getReadable select: { |entry| entry[\inode] != "0" }
	}

	getReadableNonFiles {
		^this.getReadable select: { |entry| entry[\inode] == "0" }
	}

	getSpecialNonFiles {
		^this.getReadableNonFiles select: { |entry| entry[\path].notNil }
	}

	getHeap {
		^this.getReadableNonFiles select: {|info| info[\path] == "[heap]"}
	}

	getStack {
		^this.getReadableNonFiles select: {|info| info[\path] == "[stack]"}
	}

}