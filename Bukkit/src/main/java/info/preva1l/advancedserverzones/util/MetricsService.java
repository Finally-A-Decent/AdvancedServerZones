package info.preva1l.advancedserverzones.util;

import info.preva1l.advancedserverzones.AdvancedServerZones;
import info.preva1l.trashcan.flavor.annotations.Close;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;

/**
 * Created on 26/04/2025
 *
 * @author Preva1l
 */
@Service
public class MetricsService {
    public static final MetricsService instance = new MetricsService();
    private static final int METRICS_ID = 23558;

    private Metrics metrics;

    @Configure
    public void configure() {
        metrics = new Metrics(AdvancedServerZones.instance, METRICS_ID);
    }

    @Close
    public void close() {
        if (metrics != null) {
            metrics.shutdown();
        }
    }
}
