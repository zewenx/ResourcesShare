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
	
	static LogUtils instance;
	private Logger mLogger;
	private ConsoleHandler consoleHandler;
	private FileHandler fileHandler;
	
	private LogUtils() {
		consoleHandler = new ConsoleHandler();
		try {
			File file = new File("server.log");
			if (!file.exists()) {
				file.createNewFile();
			}
			
			fileHandler = new FileHandler("server.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mLogger = Logger.getLogger("log");
		mLogger.addHandler(fileHandler);
	}
	
	public static LogUtils initLogger(){
		if(instance == null){
			instance = new LogUtils();
		}
		return instance;
	}
	
	public void log(String log, boolean debug){
		if (!debug) {
			mLogger.removeHandler(consoleHandler);
		}else {
//			mLogger.addHandler(consoleHandler);
		}
		
		mLogger.info(log);
	}
}
