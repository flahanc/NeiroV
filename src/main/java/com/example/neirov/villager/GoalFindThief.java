package com.example.neirov.villager;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.EnumSet;
import java.util.UUID;

public class GoalFindThief extends Goal {
    private final Villager villager;
    private final UUID thiefId;
    private final long endTime;
    private Player targetPlayer;

    public GoalFindThief(Villager villager, UUID thiefId, int seconds) {
        this.villager = villager;
        this.thiefId = thiefId;
        this.endTime = System.currentTimeMillis() + seconds * 1000L;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (System.currentTimeMillis() > endTime) return false;
        Level level = villager.level();
        for (Player player : level.players()) {
            if (player.getUUID().equals(thiefId) && player.isAlive()) {
                targetPlayer = player;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (targetPlayer != null) {
            villager.getNavigation().moveTo(targetPlayer, 1.0);
        }
    }

    @Override
    public void tick() {
        if (targetPlayer != null) {
            villager.getNavigation().moveTo(targetPlayer, 1.0);
        }
    }
} 