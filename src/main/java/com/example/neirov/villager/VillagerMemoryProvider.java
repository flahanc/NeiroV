package com.example.neirov.villager;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class VillagerMemoryProvider {
    public static final Capability<VillagerMemory> MEMORY = null;
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(VillagerMemory.class);
    }
} 