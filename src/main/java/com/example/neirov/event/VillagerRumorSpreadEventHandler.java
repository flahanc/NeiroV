package com.example.neirov.event;

import com.example.neirov.villager.VillagerRumor;
import com.example.neirov.villager.VillagerRumorProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerRumorSpreadEventHandler {
    @SubscribeEvent
    public static void onVillagerTick(TickEvent.WorldTickEvent event) {
        Level level = event.world;
        if (level.isClientSide) return;
        // Для каждого жителя ищем других рядом и обмениваемся слухами
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(0,0,0,level.getMaxBuildHeight(),level.getMaxBuildHeight(),level.getMaxBuildHeight()));
        for (Villager villager : villagers) {
            List<Villager> nearby = level.getEntitiesOfClass(Villager.class, villager.getBoundingBox().inflate(4));
            VillagerRumor myRumor = villager.getCapability(VillagerRumorProvider.RUMOR).orElse(null);
            for (Villager other : nearby) {
                if (other != villager) {
                    VillagerRumor otherRumor = other.getCapability(VillagerRumorProvider.RUMOR).orElse(null);
                    if (myRumor != null && otherRumor != null) {
                        myRumor.shareWith(otherRumor);
                        otherRumor.shareWith(myRumor);
                    }
                }
            }
        }
    }
} 