package com.example.neirov.event;

import com.example.neirov.villager.VillagerTrust;
import com.example.neirov.villager.VillagerTrustProvider;
import com.example.neirov.villager.VillagerRumor;
import com.example.neirov.villager.VillagerRumorProvider;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerTradeTrustEventHandler {
    @SubscribeEvent
    public static void onVillagerInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Villager)) return;
        Player player = event.getEntity();
        Villager villager = (Villager) event.getTarget();
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        VillagerTrust trust = villager.getCapability(VillagerTrustProvider.TRUST).orElse(null);
        VillagerRumor rumor = villager.getCapability(VillagerRumorProvider.RUMOR).orElse(null);
        if (rumor != null && rumor.isBlacklisted(player.getUUID())) {
            // Житель отказывает в торговле и может проявлять агрессию
            if (!player.level().isClientSide) {
                player.displayClientMessage(Component.literal("Житель шепчет: 'Это вор!'"), true);
            }
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            // TODO: Можно добавить агрессию или вызвать голема
            return;
        }
        if (trust != null) {
            int trustLevel = trust.getTrust(player);
            if (trustLevel < -30) {
                // Житель отказывается торговать
                if (!player.level().isClientSide) {
                    player.displayClientMessage(Component.literal("Житель не хочет с вами иметь дело!"), true);
                }
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            } else if (trustLevel > 50) {
                // TODO: Можно дать скидку или подарок
                if (!player.level().isClientSide) {
                    player.displayClientMessage(Component.literal("Житель рад вас видеть!"), true);
                }
            }
        }
    }
} 