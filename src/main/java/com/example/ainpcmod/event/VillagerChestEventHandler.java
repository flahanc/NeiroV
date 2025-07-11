package com.example.ainpcmod.event;

import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import java.util.List;

import com.example.ainpcmod.AINPCMod;

@Mod.EventBusSubscriber(modid = AINPCMod.MODID)
public class VillagerChestEventHandler {
    @SubscribeEvent
    public static void onChestOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockEntity blockEntity = level.getBlockEntity(event.getContainer().containerId);
        if (blockEntity != null && blockEntity instanceof ChestBlockEntity) {
            ChestBlockEntity chest = (ChestBlockEntity) blockEntity;
            BlockPos chestPos = chest.getBlockPos();
            // Поиск жителей рядом с сундуком (например, в радиусе 8 блоков)
            List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(chestPos).inflate(8));
            for (Villager villager : villagers) {
                // TODO: Проверить, видит ли житель игрока и сундук
                // TODO: Реакция жителя (угроза, звать голема и т.д.)
            }
        }
    }
} 