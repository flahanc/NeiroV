package com.example.neirov.villager;

import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class VillagerMemory {
    // Запоминаем воришек и время последней кражи
    private final HashMap<UUID, Long> thiefMemory = new HashMap<>();

    // Для каждого воришки храним список украденных предметов
    private final HashMap<UUID, List<ItemStack>> stolenItems = new HashMap<>();

    public void rememberThief(Player player) {
        thiefMemory.put(player.getUUID(), System.currentTimeMillis());
    }

    public boolean isThief(Player player) {
        return thiefMemory.containsKey(player.getUUID());
    }

    public long getLastTheftTime(Player player) {
        return thiefMemory.getOrDefault(player.getUUID(), 0L);
    }

    public void rememberStolenItems(Player player, List<ItemStack> items) {
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : items) {
            copy.add(stack.copy());
        }
        stolenItems.put(player.getUUID(), copy);
    }

    public boolean isItemsReturned(Player player, ChestBlockEntity chest) {
        List<ItemStack> stolen = stolenItems.get(player.getUUID());
        if (stolen == null) return false;
        List<ItemStack> chestItems = new ArrayList<>();
        for (int i = 0; i < chest.getContainerSize(); i++) {
            chestItems.add(chest.getItem(i));
        }
        // Проверяем, что все украденные предметы снова есть в сундуке (по количеству)
        for (ItemStack stolenStack : stolen) {
            boolean found = false;
            for (ItemStack chestStack : chestItems) {
                if (ItemStack.isSameItemSameTags(stolenStack, chestStack) && chestStack.getCount() >= stolenStack.getCount()) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public void forgiveThief(Player player) {
        thiefMemory.remove(player.getUUID());
        stolenItems.remove(player.getUUID());
    }
} 