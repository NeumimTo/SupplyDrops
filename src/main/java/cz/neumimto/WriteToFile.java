package cz.neumimto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by NeumimTo on 30.7.2017.
 */
public class WriteToFile implements Runnable {

	private final long lastTimeRun;
	private final Path path;

	public WriteToFile(long lastTimeRun, Path path) {
		this.lastTimeRun = lastTimeRun;
		this.path = path;
	}

	public void run() {
		try {
			Files.write(path, String.valueOf(lastTimeRun).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
