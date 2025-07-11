package com.example.neirov.event;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import java.util.List;
import java.util.Random;
import com.example.neirov.villager.VillagerTrust;
import com.example.neirov.villager.VillagerTrustProvider;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerDeathEventHandler {
    @SubscribeEvent
    public static void onVillagerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Villager)) return;
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (!(event.getSource().getEntity() instanceof Player)) return;
        Player player = (Player) event.getSource().getEntity();
        // Поиск жителей-свидетелей (радиус 16 блоков)
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(entity.blockPosition()).inflate(16));
        Random rand = new Random();
        for (Villager villager : villagers) {
            if (rand.nextInt(100) < 35) {
                // 35% атакуют игрока
                villager.setCustomNameVisible(true);
                villager.setCustomName(net.minecraft.network.chat.Component.literal("За деревню!"));
                villager.setAggressive(true);
                villager.setTarget(player);
                // TODO: дать меч в руку
            } else {
                // Остальные убегают
                villager.getNavigation().moveTo(villager.blockPosition().offset(rand.nextInt(10)-5, 0, rand.nextInt(10)-5), 1.2);
                villager.setCustomNameVisible(true);
                villager.setCustomName(net.minecraft.network.chat.Component.literal("Бежим!"));
            }
            // Доверие падает за убийство жителя
            VillagerTrust trust = villager.getCapability(VillagerTrustProvider.TRUST).orElse(null);
            if (trust != null) {
                trust.changeTrust(player, -50);
            }
        }
        // Если есть колокол — жители бегут к нему и звонят
        for (BlockPos pos : BlockPos.betweenClosed(entity.blockPosition().offset(-16, -4, -16), entity.blockPosition().offset(16, 4, 16))) {
            if (level.getBlockState(pos).getBlock() == Blocks.BELL) {
                if (level.getBlockEntity(pos) instanceof BellBlockEntity bell) {
                    bell.ring(player, true);
                }
            }
        }
    }
} 