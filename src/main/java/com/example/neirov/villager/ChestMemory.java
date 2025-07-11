package com.example.neirov.villager;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import java.util.ArrayList;
import java.util.List;

public class ChestMemory {
    private final List<ItemStack> before;
    private final List<ItemStack> after;

    public ChestMemory(ChestBlockEntity chest) {
        this.before = new ArrayList<>();
        for (int i = 0; i < chest.getContainerSize(); i++) {
            before.add(chest.getItem(i).copy());
        }
        this.after = null;
    }

    public void saveAfter(ChestBlockEntity chest) {
        if (after != null) return;
        for (int i = 0; i < chest.getContainerSize(); i++) {
            after.add(chest.getItem(i).copy());
        }
    }

    public boolean isStolen() {
        if (after == null) return false;
        for (int i = 0; i < before.size(); i++) {
            if (!ItemStack.isSameItemSameTags(before.get(i), after.get(i)) && before.get(i).getCount() > after.get(i).getCount()) {
                return true;
            }
        }
        return false;
    }
} 