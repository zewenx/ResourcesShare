package server;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogUtils {

	static LogUtils instanceForServer;
	static LogUtils instanceForClient;
	private Logger mLogger;
	private FileHandler fileHandler;

	private LogUtils(String tag) {
		try {
			File file = new File(tag);
			if (!file.exists()) {
				file.createNewFile();
			}

			fileHandler = new FileHandler(tag, true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mLogger = Logger.getLogger("log");
		for (Handler handler : mLogger.getHandlers()) {
			mLogger.removeHandler(handler);
		}
		mLogger.addHandler(fileHandler);
	}

	public static LogUtils initLogger(String tag) {
		if (tag.contains("Server")) {
			if (instanceForServer == null) {
				instanceForServer = new LogUtils(tag);
			}
			return instanceForServer;
		}else {
			if (instanceForClient == null) {
				instanceForClient = new LogUtils(tag);
			}
			return instanceForClient;
		}
	}

	public void log(String log, boolean debug) {
		mLogger.setUseParentHandlers(debug);
		mLogger.info(log);
	}
}