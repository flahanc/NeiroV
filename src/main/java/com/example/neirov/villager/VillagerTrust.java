package com.example.neirov.villager;

import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.UUID;

public class VillagerTrust {
    // Уровень доверия к каждому игроку (от -100 до 100)
    private final HashMap<UUID, Integer> trustMap = new HashMap<>();

    public int getTrust(Player player) {
        return trustMap.getOrDefault(player.getUUID(), 0);
    }

    public void changeTrust(Player player, int delta) {
        int trust = getTrust(player) + delta;
        if (trust > 100) trust = 100;
        if (trust < -100) trust = -100;
        trustMap.put(player.getUUID(), trust);
    }

    public void setTrust(Player player, int value) {
        trustMap.put(player.getUUID(), Math.max(-100, Math.min(100, value)));
    }

    public boolean isTrusted(Player player) {
        return getTrust(player) > 0;
    }

    public boolean isEnemy(Player player) {
        return getTrust(player) < -30;
    }
} 