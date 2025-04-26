package info.preva1l.advancedserverzones.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@UtilityClass
public class Logger {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("AdvancedServerZones");

    public void info(@NotNull String message) {
        logger.log(Level.INFO, message);
    }

    public void warn(@NotNull String message) {
        logger.log(Level.WARNING, message);
    }

    public void warn(@NotNull String message, @NotNull Exception e) {
        logger.log(Level.WARNING, message, e);
    }

    public void severe(@NotNull String message) {
        logger.severe(message);
    }

    public void severe(@NotNull String message, @NotNull Exception e) {
        logger.log(Level.SEVERE, message, e);
    }

    public void debug(@NotNull String message) {
        logger.log(Level.FINE, message);
    }
}
