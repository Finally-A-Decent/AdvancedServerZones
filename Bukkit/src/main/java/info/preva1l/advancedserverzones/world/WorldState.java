package info.preva1l.advancedserverzones.world;

import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.World;

public record WorldState(
        @Expose String world,
        @Expose long time,

        @Expose boolean weatherState,
        @Expose int weatherDuration,

        @Expose boolean thunderState,
        @Expose int thunderDuration,

        @Expose int clearDuration
) {
    public void apply() {
        World world = Bukkit.getWorld(world());
        if (world == null) return;
        world.setFullTime(world.getTime());

        world.setStorm(weatherState());
        world.setWeatherDuration(weatherDuration());

        world.setThundering(thunderState());
        world.setThunderDuration(thunderDuration());

        world.setClearWeatherDuration(clearDuration());
    }
}
