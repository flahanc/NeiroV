package com.example.neirov.event;

import com.example.neirov.villager.VillagerRumor;
import com.example.neirov.villager.VillagerRumorProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerTrialEventHandler {
    private static final BlockPos SQUARE_POS = new BlockPos(0, 64, 0); // Площадь деревни (пример)
    private static boolean trialActive = false;
    private static UUID accusedPlayer = null;
    private static long trialEndTime = 0;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        Level level = event.world;
        if (level.isClientSide) return;
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(0,0,0,level.getMaxBuildHeight(),level.getMaxBuildHeight(),level.getMaxBuildHeight()));
        List<Player> players = level.players();
        // Если суд не активен, ищем воришку для суда
        if (!trialActive) {
            for (Player player : players) {
                for (Villager villager : villagers) {
                    VillagerRumor rumor = villager.getCapability(VillagerRumorProvider.RUMOR).orElse(null);
                    if (rumor != null && rumor.isBlacklisted(player.getUUID())) {
                        if (new Random().nextInt(1000) == 0) { // Редко инициировать суд
                            trialActive = true;
                            accusedPlayer = player.getUUID();
                            trialEndTime = System.currentTimeMillis() + 20000; // 20 секунд на суд
                            // Жители идут на площадь
                            for (Villager v : villagers) {
                                v.getNavigation().moveTo(SQUARE_POS.getX() + 0.5, SQUARE_POS.getY(), SQUARE_POS.getZ() + 0.5, 1.0);
                                v.setCustomNameVisible(true);
                                v.setCustomName(net.minecraft.network.chat.Component.literal("Суд над воришкой!"));
                            }
                            break;
                        }
                    }
                }
                if (trialActive) break;
            }
        } else {
            // Суд активен: жители обсуждают проступок
            if (System.currentTimeMillis() < trialEndTime) {
                for (Villager v : villagers) {
                    v.setCustomNameVisible(true);
                    v.setCustomName(net.minecraft.network.chat.Component.literal("Обсуждение: что делать с воришкой?"));
                }
            } else {
                // Приговор: временное изгнание (пример)
                for (Player player : players) {
                    if (player.getUUID().equals(accusedPlayer)) {
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal("Вас изгнали из деревни на 5 минут!"), false);
                        // TODO: Реализовать запрет на вход (например, телепортировать или накладывать эффект)
                    }
                }
                for (Villager v : villagers) {
                    v.setCustomNameVisible(false);
                }
                trialActive = false;
                accusedPlayer = null;
            }
        }
    }
} 