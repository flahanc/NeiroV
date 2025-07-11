package com.example.neirov.villager;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class VillagerRumorProvider {
    public static final Capability<VillagerRumor> RUMOR = null;
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(VillagerRumor.class);
    }
} 