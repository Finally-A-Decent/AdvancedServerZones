package info.preva1l.advancedserverzones.config;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class AutoReload {
    public void watch(File file, Runnable callback) {
        CompletableFuture.runAsync(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                file.toPath().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path var1 = (Path) event.context();

                        if (var1.toString().endsWith(file.getName())) {
                            callback.run();
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void watch(Path configDirectory, String fileName, Runnable callback) {
        CompletableFuture.runAsync(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                configDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path var1 = (Path) event.context();

                        if (var1.toString().endsWith(fileName)) {
                            callback.run();
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}