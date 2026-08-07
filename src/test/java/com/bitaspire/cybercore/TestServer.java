package com.bitaspire.cybercore;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Installs a stub {@link Server} so Takion can build a library instance headless. Without it the
 * static initializers that reach {@link Bukkit#getLogger()} fail and nothing can be tested.
 */
final class TestServer {

    private static final Logger LOGGER = Logger.getLogger("CyberCoreTest");

    private static boolean installed;

    private TestServer() {}

    static synchronized void install() {
        if (installed) return;
        installed = true;

        if (Bukkit.getServer() != null) return;

        Bukkit.setServer((Server) Proxy.newProxyInstance(
                TestServer.class.getClassLoader(),
                new Class<?>[] {Server.class},
                new StubHandler()
        ));
    }

    private static Object pluginManager() {
        return Proxy.newProxyInstance(
                TestServer.class.getClassLoader(),
                new Class<?>[] {org.bukkit.plugin.PluginManager.class},
                (proxy, method, args) -> "getPlugins".equals(method.getName()) ?
                        new org.bukkit.plugin.Plugin[0] :
                        new StubHandler().invoke(proxy, method, args)
        );
    }

    private static final class StubHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            switch (method.getName()) {
                case "getLogger":
                    return LOGGER;
                case "getName":
                    return "CyberCoreTest";
                case "getVersion":
                case "getBukkitVersion":
                    return "1.16.5-R0.1-SNAPSHOT";
                case "isPrimaryThread":
                    return true;
                case "getOnlinePlayers":
                    return Collections.emptyList();
                case "getPluginManager":
                    return pluginManager();
                case "toString":
                    return "TestServer";
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "equals":
                    return proxy == args[0];
                default:
                    return defaultValue(method.getReturnType());
            }
        }

        private Object defaultValue(Class<?> type) {
            if (!type.isPrimitive()) return null;
            if (type == boolean.class) return false;
            if (type == void.class) return null;
            if (type == long.class) return 0L;
            if (type == double.class) return 0D;
            if (type == float.class) return 0F;
            return 0;
        }
    }
}
