package com.example.neirov.villager;

import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class VillagerTrustProvider {
    public static final Capability<VillagerTrust> TRUST = null;
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(VillagerTrust.class);
    }
} 