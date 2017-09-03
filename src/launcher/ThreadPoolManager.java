package launcher;

import javafx.util.Pair;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Hack
 * Date: 02.05.2017 2:14
 */
public class ThreadPoolManager {
    private static final ThreadPoolManager instance = new ThreadPoolManager();
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(getThreadNumber());
    private ScheduledThreadPoolExecutor scheduledExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(getThreadNumber());
    private Map<String, Future<?>> dependencies = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<Pair<String, Future<?>>> depends = new ConcurrentLinkedQueue<>();

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    public void execute(Runnable r) {
        executor.execute(r);
    }

    public Future<?> submit(Runnable r) {
        return executor.submit(r);
    }

    public void shutdown() {
        executor.shutdownNow();
        scheduledExecutor.shutdownNow();
    }

    public ScheduledFuture<?> schedule(Runnable r, long millisDelay) {
        return scheduledExecutor.schedule(r, millisDelay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long period) {
        return scheduledExecutor.scheduleAtFixedRate(r, initial, period, TimeUnit.MILLISECONDS);
    }

    public void dependentExecute(Runnable r, String myNameAsDependency, String ... myDependencies) {
        for (String dependName : myDependencies) {
            Future<?> future = getDependency(dependName);
            while (future != null && !future.isCancelled() && !future.isDone())
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
        }
        if (myNameAsDependency != null && !myNameAsDependency.isEmpty())
            depends.add(new Pair<>(myNameAsDependency, executor.submit(r)));
        else
            executor.execute(r);
    }

    private int getThreadNumber() {
        return Math.max(1, Runtime.getRuntime().availableProcessors());
    }

    private Future<?> getDependency(String key) {
        return depends.stream()
                .filter(pair -> pair.getKey().equals(key) && !pair.getValue().isCancelled() && !pair.getValue().isDone())
                .findAny().orElse(null).getValue();
    }

    public void runDaemon(Runnable r, long period) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        scheduleAtFixedRate(thread, period, period);
    }

    public int activeCount() {
        return executor.getActiveCount() + scheduledExecutor.getActiveCount();
    }
}
