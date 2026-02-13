package com.modernexpshare;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModernExpShareMod implements ModInitializer {
    public static final String MOD_ID = "modern_exp_share";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ModernExpShareConfig.loadOrCreate(server);
        });

        ModernExpShareLogic.registerCobblemonHandlers();
        LOGGER.info("Initialized");
    }
}
