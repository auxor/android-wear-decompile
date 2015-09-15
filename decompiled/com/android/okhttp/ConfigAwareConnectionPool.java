package com.android.okhttp;

import libcore.net.event.NetworkEventDispatcher;
import libcore.net.event.NetworkEventListener;

public class ConfigAwareConnectionPool {
    private static final long CONNECTION_POOL_DEFAULT_KEEP_ALIVE_DURATION_MS = 300000;
    private static final long CONNECTION_POOL_KEEP_ALIVE_DURATION_MS;
    private static final int CONNECTION_POOL_MAX_IDLE_CONNECTIONS;
    private static final ConfigAwareConnectionPool instance;
    private ConnectionPool connectionPool;
    private final NetworkEventDispatcher networkEventDispatcher;
    private boolean networkEventListenerRegistered;

    static {
        String keepAliveProperty = System.getProperty("http.keepAlive");
        String keepAliveDurationProperty = System.getProperty("http.keepAliveDuration");
        String maxIdleConnectionsProperty = System.getProperty("http.maxConnections");
        CONNECTION_POOL_KEEP_ALIVE_DURATION_MS = keepAliveDurationProperty != null ? Long.parseLong(keepAliveDurationProperty) : CONNECTION_POOL_DEFAULT_KEEP_ALIVE_DURATION_MS;
        if (keepAliveProperty != null && !Boolean.parseBoolean(keepAliveProperty)) {
            CONNECTION_POOL_MAX_IDLE_CONNECTIONS = 0;
        } else if (maxIdleConnectionsProperty != null) {
            CONNECTION_POOL_MAX_IDLE_CONNECTIONS = Integer.parseInt(maxIdleConnectionsProperty);
        } else {
            CONNECTION_POOL_MAX_IDLE_CONNECTIONS = 5;
        }
        instance = new ConfigAwareConnectionPool();
    }

    protected ConfigAwareConnectionPool(NetworkEventDispatcher networkEventDispatcher) {
        this.networkEventDispatcher = networkEventDispatcher;
    }

    private ConfigAwareConnectionPool() {
        this.networkEventDispatcher = NetworkEventDispatcher.getInstance();
    }

    public static ConfigAwareConnectionPool getInstance() {
        return instance;
    }

    public synchronized ConnectionPool get() {
        if (this.connectionPool == null) {
            if (!this.networkEventListenerRegistered) {
                this.networkEventDispatcher.addListener(new NetworkEventListener() {
                    public void onNetworkConfigurationChanged() {
                        synchronized (ConfigAwareConnectionPool.this) {
                            ConnectionPool oldConnectionPool = ConfigAwareConnectionPool.this.connectionPool;
                            ConfigAwareConnectionPool.this.connectionPool = null;
                            if (oldConnectionPool != null) {
                                oldConnectionPool.enterDrainMode();
                            }
                        }
                    }
                });
                this.networkEventListenerRegistered = true;
            }
            this.connectionPool = new ConnectionPool(CONNECTION_POOL_MAX_IDLE_CONNECTIONS, CONNECTION_POOL_KEEP_ALIVE_DURATION_MS);
        }
        return this.connectionPool;
    }
}
