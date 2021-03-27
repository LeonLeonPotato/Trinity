package me.leon.trinity.main;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class RPCHandler {
    final private static DiscordRPC lib = DiscordRPC.INSTANCE;
    final private static DiscordRichPresence presence = new DiscordRichPresence();

    public static void start() {
        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> Trinity.LOGGER.info("RPC Started!");
        lib.Discord_Initialize("825313668543152128", handlers, true, "");

        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "Trinity v" + Trinity.VERSION;
        presence.largeImageKey = "trinity";
        presence.state = "gamin'";
        presence.largeImageText = "Trinity v" + Trinity.VERSION;

        lib.Discord_UpdatePresence(presence);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();

                presence.details = "Trinity v" + Trinity.VERSION;
                presence.state = "gamin'";
                presence.largeImageKey = "trinity";
                presence.largeImageText = "Trinity v" + Trinity.VERSION;

                lib.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }

    public static void shutdown() {
        lib.Discord_Shutdown();
    }
}
