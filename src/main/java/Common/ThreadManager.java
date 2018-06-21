package Common;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Lizb on 2017/5/15.
 *
 */
public class ThreadManager {
	
    private static Executor sExecutor = Executors.newSingleThreadExecutor();
    
    
    public static void runOnSubThread(Runnable runnable) {
        sExecutor.execute(runnable);
    }

    public static void runOnFixedThreadPool(Runnable runnable){
        Executors.newFixedThreadPool(5).execute(runnable);
    }

}
