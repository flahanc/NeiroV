package com.example.neirov.villager;

import java.util.UUID;

public class VillagerReconciliationQuest {
    public enum QuestType {
        BRING_EMERALDS,
        DEFEND_VILLAGE,
        REPAIR_GOLEM
    }

    private final UUID playerId;
    private final QuestType type;
    private boolean completed = false;
    private int emeraldsRequired = 0;

    public VillagerReconciliationQuest(UUID playerId, QuestType type, int emeraldsRequired) {
        this.playerId = playerId;
        this.type = type;
        this.emeraldsRequired = emeraldsRequired;
    }

    public UUID getPlayerId() { return playerId; }
    public QuestType getType() { return type; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getEmeraldsRequired() { return emeraldsRequired; }
    public void setEmeraldsRequired(int emeraldsRequired) { this.emeraldsRequired = emeraldsRequired; }
} 