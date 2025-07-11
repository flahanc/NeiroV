package com.example.neirov;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NeiroVMod.MODID)
public class NeiroVMod {
    public static final String MODID = "neirov";

    public NeiroVMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Регистрация событий и AI для умных жителей
    }
} 