package com.example.neirov.event;

import com.example.neirov.villager.VillagerMemory;
import com.example.neirov.villager.VillagerMemoryProvider;
import com.example.neirov.villager.VillagerRumor;
import com.example.neirov.villager.VillagerRumorProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerForgivenessAndTrialEventHandler {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        Level level = event.world;
        if (level.isClientSide) return;
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(0,0,0,level.getMaxBuildHeight(),level.getMaxBuildHeight(),level.getMaxBuildHeight()));
        Random rand = new Random();
        for (Villager villager : villagers) {
            VillagerMemory memory = villager.getCapability(VillagerMemoryProvider.MEMORY).orElse(null);
            VillagerRumor rumor = villager.getCapability(VillagerRumorProvider.RUMOR).orElse(null);
            if (memory != null && rumor != null) {
                // Случайное прощение: если с момента проступка прошло больше 10 мин, с шансом 10% житель прощает игрока
                long now = System.currentTimeMillis();
                for (var entry : memory.thiefMemory.entrySet()) {
                    if (now - entry.getValue() > 10 * 60 * 1000 && rand.nextInt(100) < 10) {
                        memory.forgiveThiefById(entry.getKey());
                        rumor.blacklist.remove(entry.getKey());
                        // Можно добавить надпись или эффект прощения
                    }
                }
                // Заготовка: если воришка снова замечен — инициировать 'суд' (можно реализовать массовый сбор жителей)
                // TODO: Реализовать визуализацию и механику суда
            }
        }
    }
} 