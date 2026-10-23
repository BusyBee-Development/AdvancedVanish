package net.busybee.advancedvanish.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Runs work on the thread that owns an entity. On Paper that is the main thread; on Folia it is
 * the entity's region thread. When the caller already owns the entity the work runs immediately.
 */
public final class Schedulers {

    private Schedulers() {
    }

    /** Runs {@code task} on the entity's thread. Dropped silently if the entity is gone first. */
    public static void run(Plugin plugin, Entity entity, Runnable task) {
        if (Bukkit.isOwnedByCurrentRegion(entity)) {
            task.run();
        } else {
            entity.getScheduler().run(plugin, scheduled -> task.run(), null);
        }
    }

    /** Runs {@code task} on the entity's thread after {@code delayTicks}. */
    public static void runLater(Plugin plugin, Entity entity, Runnable task, long delayTicks) {
        entity.getScheduler().runDelayed(plugin, scheduled -> task.run(), null, Math.max(1, delayTicks));
    }

    /**
     * Computes a value on the entity's thread. If the entity is removed before the task runs, the
     * future completes with {@code ifRetired}.
     */
    public static <T> CompletableFuture<T> supply(Plugin plugin, Entity entity, Supplier<T> task, T ifRetired) {
        if (Bukkit.isOwnedByCurrentRegion(entity)) {
            try {
                return CompletableFuture.completedFuture(task.get());
            } catch (Throwable t) {
                return CompletableFuture.failedFuture(t);
            }
        }

        CompletableFuture<T> future = new CompletableFuture<>();
        boolean scheduled = entity.getScheduler().run(plugin, s -> {
            try {
                future.complete(task.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }, () -> future.complete(ifRetired)) != null;
        if (!scheduled) future.complete(ifRetired);
        return future;
    }

    /** Runs {@code task} off the server threads. */
    public static void async(Plugin plugin, Runnable task) {
        Bukkit.getAsyncScheduler().runNow(plugin, scheduled -> task.run());
    }
}
