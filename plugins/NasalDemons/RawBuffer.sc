RawBuffer {
	*new { |server, path, numChannels = 1, action|
		if (File.exists(path)) {
			var signal = File.readAllSignal(path);
			^Buffer.loadCollection(server, signal, numChannels, action)
		} {
			"RawBuffer: file '%' doesn't exists".format(path).warn;
		}
	}
}