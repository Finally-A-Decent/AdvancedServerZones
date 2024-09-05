package info.preva1l.advancedserverzones.util;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Easy creation of Bukkit Tasks
 *
 * @author Preva1l
 */
@UtilityClass
public class TaskManager {
    /**
     * Synchronous Tasks
     */
    @UtilityClass
    public class Sync {
        /**
         * Run a synchronous task once. Helpful when needing to run some sync code in an async loop
         *
         * @param plugin   The current plugin typeof JavaPlugin. (Not Commons)
         * @param runnable The runnable, lambda supported yeh
         */
        public void run(JavaPlugin plugin, Runnable runnable) {
            plugin.getServer().getScheduler().runTask(plugin, runnable);
        }
    }

    /**
     * Asynchronous tasks
     */
    @UtilityClass
    public class Async {
        /**
         * Run an asynchronous task once. Helpful when needing to run some sync code in an async loop
         *
         * @param plugin   The current plugin typeof JavaPlugin. (Not Commons)
         * @param runnable The runnable, lambda supported yeh
         */
        public void run(JavaPlugin plugin, Runnable runnable) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
        }

        /**
         * Run an asynchronous task forever with a delay between runs.
         *
         * @param plugin   The current plugin typeof JavaPlugin. (Not Commons)
         * @param runnable The runnable, lambda supported yeh
         * @param interval Time between each run
         */
        public void runTask(JavaPlugin plugin, Runnable runnable, long interval) {
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0L, interval);
        }
    }
}