package info.preva1l.advancedserverzones.network;

import info.preva1l.advancedserverzones.config.Config;
import info.preva1l.advancedserverzones.util.GsonCodec;
import info.preva1l.trashcan.flavor.annotations.Close;
import info.preva1l.trashcan.flavor.annotations.Configure;
import info.preva1l.trashcan.flavor.annotations.Service;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.SingleServerConfig;

@Service(priority = 10)
public final class RedisBroker extends Broker {
    public static final RedisBroker instance = new RedisBroker();

    @Getter private static RedissonClient redisson;
    private static String CHANNEL = "NONE";
    private RTopic topic;

    @Configure
    @Override
    public void connect() {
        redisson = initReddison();

        topic = getRedisson().getTopic(CHANNEL);
        topic.addListenerAsync(Message.class, (charSequence, message) -> handle(message));
        Broker.instance = this;
    }

    @Override
    protected void send(@NotNull Message message) {
        topic.publishAsync(message);
    }

    @Close
    @Override
    public void destroy() {
        if (getRedisson() != null) getRedisson().shutdown();
    }

    @NotNull
    private RedissonClient initReddison() {
        Config.Redis conf = Config.i().getRedis();
        final String username = conf.getUsername();
        final String password = conf.getPassword();
        final String host = conf.getHost();
        final int port = conf.getPort();
        CHANNEL = conf.getChannel();

        org.redisson.config.Config config = new org.redisson.config.Config()
                .setCodec(new GsonCodec(gson));
        SingleServerConfig ssc = config.useSingleServer()
                .setAddress("redis://%s:%s".formatted(host, port))
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(10)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(10);
        if (!username.isEmpty()) ssc.setUsername(username);
        if (!password.isEmpty()) ssc.setPassword(password);

        return Redisson.create(config);
    }
}
