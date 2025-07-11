package com.example.neirov.villager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VillagerRumor {
    // Слухи о воришках и врагах (UUID игроков)
    private final Set<UUID> blacklist = new HashSet<>();

    public void addToBlacklist(UUID playerId) {
        blacklist.add(playerId);
    }

    public boolean isBlacklisted(UUID playerId) {
        return blacklist.contains(playerId);
    }

    // Передача слуха другому жителю
    public void shareWith(VillagerRumor other) {
        other.blacklist.addAll(this.blacklist);
    }
} 