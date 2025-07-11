package com.example.neirov.event;

import com.example.neirov.villager.VillagerMemory;
import com.example.neirov.villager.VillagerMemoryProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerThiefReturnEventHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;
        // Проверяем, не запомнил ли кто-то этого игрока как воришку
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(player.blockPosition()).inflate(12));
        for (Villager villager : villagers) {
            VillagerMemory memory = villager.getCapability(VillagerMemoryProvider.MEMORY).orElse(null);
            if (memory != null && memory.isThief(player)) {
                // Поиск ближайшего сундука (радиус 8 блоков)
                List<ChestBlockEntity> chests = level.getEntitiesOfClass(ChestBlockEntity.class, villager.getBoundingBox().inflate(8));
                boolean returned = false;
                for (ChestBlockEntity chest : chests) {
                    if (memory.isItemsReturned(player, chest)) {
                        returned = true;
                        break;
                    }
                }
                if (returned) {
                    memory.forgiveThief(player);
                    villager.setCustomNameVisible(true);
                    villager.setCustomName(net.minecraft.network.chat.Component.literal("Спасибо, что вернул вещи!"));
                    // TODO: убрать угрозы, вернуть обычное поведение
                } else {
                    // Житель требует вернуть вещи
                    villager.setCustomNameVisible(true);
                    villager.setCustomName(net.minecraft.network.chat.Component.literal("Верни украденное!"));
                    // TODO: если игрок не реагирует — вызвать голема (или других жителей)
                }
            }
        }
    }
} 