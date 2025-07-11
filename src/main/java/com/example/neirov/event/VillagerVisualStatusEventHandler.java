package com.example.neirov.event;

import com.example.neirov.villager.VillagerRumor;
import com.example.neirov.villager.VillagerRumorProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import java.util.List;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerVisualStatusEventHandler {
    @SubscribeEvent
    public static void onVillagerTick(TickEvent.WorldTickEvent event) {
        Level level = event.world;
        if (level.isClientSide) return;
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(0,0,0,level.getMaxBuildHeight(),level.getMaxBuildHeight(),level.getMaxBuildHeight()));
        for (Villager villager : villagers) {
            VillagerRumor rumor = villager.getCapability(VillagerRumorProvider.RUMOR).orElse(null);
            if (rumor == null) continue;
            List<Player> players = level.getEntitiesOfClass(Player.class, villager.getBoundingBox().inflate(6));
            for (Player player : players) {
                if (rumor.isBlacklisted(player.getUUID())) {
                    // Житель показывает надпись и отходит
                    villager.setCustomNameVisible(true);
                    villager.setCustomName(net.minecraft.network.chat.Component.literal("Житель показывает на вас пальцем и шепчет другим!"));
                    villager.getNavigation().moveTo(villager.getX() + 3, villager.getY(), villager.getZ() + 3, 1.0);
                    // Закрывает ближайшую дверь, если есть
                    for (BlockPos pos : BlockPos.betweenClosed(villager.blockPosition().offset(-2, -1, -2), villager.blockPosition().offset(2, 2, 2))) {
                        if (level.getBlockState(pos).getBlock() == Blocks.OAK_DOOR && level.getBlockState(pos).getValue(net.minecraft.world.level.block.DoorBlock.OPEN)) {
                            level.setBlock(pos, level.getBlockState(pos).setValue(net.minecraft.world.level.block.DoorBlock.OPEN, false), 2);
                        }
                    }
                }
            }
        }
    }
} 