package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
	ExecutorService fixedThreadPool;
	int count = 0;
	int cores = 5;

	private static ThreadPoolManager mPoolManager;

	private ThreadPoolManager() {
		fixedThreadPool = Executors.newFixedThreadPool(cores);
	}

	public static ThreadPoolManager init() {
		if (mPoolManager == null) {
			mPoolManager = new ThreadPoolManager();
		}
		return mPoolManager;
	}

	public void submitThread(Runnable runnable) {
		fixedThreadPool.execute(runnable);
	}

	public void setThreadNumber(int count) {
		cores = count;
	}
}
