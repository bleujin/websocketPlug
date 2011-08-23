package net.ion.websocket.common.plugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

public class TestThreadExecutor extends TestCase {

	public void testRun() throws Exception {

		int maxLoadThreads = 5;
		final ExecutorService te = Executors.newFixedThreadPool(maxLoadThreads) ; 
		// final ThreadPoolExecutor te = new ThreadPoolExecutor(maxLoadThreads, maxLoadThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());

		new Thread(){
			public void run(){
				try {
					Thread.sleep(5000) ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Debug.line("SHUT DOWN....") ;
				te.shutdownNow() ;
				Debug.line("Shut down................. end ") ;;
			}
		}.start() ;
		
		
		for (int i = 0; i < 20; i++) {
			te.submit(new MyRun(te)) ;
		}
		
		// te.execute(new MyRun(te));
		
		te.awaitTermination(10000, TimeUnit.HOURS) ;
	}
	
	
	
	
	public void testPC() throws Exception {
		
		
	}

}

class MyRun implements Runnable {

	ExecutorService te ;
	public MyRun(ExecutorService te) {
		this.te = te ;
	}

	public void run() {
		try {
			int i = 0 ;
			while (i < 10000) {
				System.out.print("[" +Thread.currentThread().getId() + ":" + te.isShutdown()  + "]");
				if ((i++ % 30) == 0) System.out.println() ;
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
