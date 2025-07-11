package com.example.neirov.event;

import com.example.neirov.villager.VillagerTrust;
import com.example.neirov.villager.VillagerTrustProvider;
import com.example.neirov.villager.VillagerRumor;
import com.example.neirov.villager.VillagerRumorProvider;
import com.example.neirov.villager.VillagerReconciliationQuest;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerReconciliationQuestEventHandler {
    // Храним квесты на примирение по игроку
    private static final HashMap<UUID, VillagerReconciliationQuest> quests = new HashMap<>();

    @SubscribeEvent
    public static void onVillagerInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Villager)) return;
        Player player = event.getEntity();
        Villager villager = (Villager) event.getTarget();
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (villager.getVillagerData().getProfession() != VillagerProfession.CLERIC) return;
        VillagerTrust trust = villager.getCapability(VillagerTrustProvider.TRUST).orElse(null);
        VillagerRumor rumor = villager.getCapability(VillagerRumorProvider.RUMOR).orElse(null);
        if ((trust != null && trust.getTrust(player) < -30) || (rumor != null && rumor.isBlacklisted(player.getUUID()))) {
            VillagerReconciliationQuest quest = quests.get(player.getUUID());
            if (quest == null) {
                // Выдаём квест: принести 10 изумрудов
                quest = new VillagerReconciliationQuest(player.getUUID(), VillagerReconciliationQuest.QuestType.BRING_EMERALDS, 10);
                quests.put(player.getUUID(), quest);
                if (!player.level().isClientSide) {
                    player.displayClientMessage(Component.literal("Священник: Принеси 10 изумрудов, и мы простим тебя!"), false);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            } else if (!quest.isCompleted()) {
                // Проверяем, есть ли у игрока нужное количество изумрудов
                int emeralds = 0;
                for (ItemStack stack : player.getInventory().items) {
                    if (stack.getItem() == Items.EMERALD) {
                        emeralds += stack.getCount();
                    }
                }
                if (emeralds >= quest.getEmeraldsRequired()) {
                    // Забираем изумруды
                    int toRemove = quest.getEmeraldsRequired();
                    for (ItemStack stack : player.getInventory().items) {
                        if (stack.getItem() == Items.EMERALD) {
                            int remove = Math.min(stack.getCount(), toRemove);
                            stack.shrink(remove);
                            toRemove -= remove;
                            if (toRemove <= 0) break;
                        }
                    }
                    quest.setCompleted(true);
                    if (trust != null) trust.setTrust(player, 10);
                    if (rumor != null) rumor.shareWith(new VillagerRumor()); // Очищаем слухи (можно реализовать иначе)
                    if (!player.level().isClientSide) {
                        player.displayClientMessage(Component.literal("Священник: Ты прощён! Жители снова будут с тобой общаться."), false);
                    }
                } else {
                    if (!player.level().isClientSide) {
                        player.displayClientMessage(Component.literal("Священник: У тебя недостаточно изумрудов!"), false);
                    }
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
} 