package com.example.neirov.villager;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.EnumSet;
import java.util.UUID;
import java.util.Random;

public class GoalWatchAndHide extends Goal {
    private final Villager villager;
    private final UUID playerId;
    private final long endTime;
    private Player targetPlayer;
    private final Random rand = new Random();

    public GoalWatchAndHide(Villager villager, UUID playerId, int seconds) {
        this.villager = villager;
        this.playerId = playerId;
        this.endTime = System.currentTimeMillis() + seconds * 1000L;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (System.currentTimeMillis() > endTime) return false;
        Level level = villager.level();
        for (Player player : level.players()) {
            if (player.getUUID().equals(playerId) && player.isAlive()) {
                targetPlayer = player;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        // Житель держится на расстоянии 8-12 блоков
        if (targetPlayer != null) {
            double dx = targetPlayer.getX() - villager.getX();
            double dz = targetPlayer.getZ() - villager.getZ();
            double dist = Math.sqrt(dx*dx + dz*dz);
            double desiredDist = 8 + rand.nextInt(5); // 8-12 блоков
            double ratio = desiredDist / dist;
            double tx = villager.getX() + dx * ratio;
            double tz = villager.getZ() + dz * ratio;
            villager.getNavigation().moveTo(tx, villager.getY(), tz, 0.8);
        }
    }

    @Override
    public void tick() {
        if (targetPlayer != null) {
            // Если игрок смотрит на жителя — житель прячется (уходит в случайную точку)
            if (isPlayerLookingAtVillager(targetPlayer, villager)) {
                double tx = villager.getX() + rand.nextInt(8) - 4;
                double tz = villager.getZ() + rand.nextInt(8) - 4;
                villager.getNavigation().moveTo(tx, villager.getY(), tz, 1.0);
            }
        }
    }

    private boolean isPlayerLookingAtVillager(Player player, Villager villager) {
        double dx = villager.getX() - player.getX();
        double dz = villager.getZ() - player.getZ();
        double dy = villager.getEyeY() - player.getEyeY();
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        double dot = (player.getLookAngle().x * dx + player.getLookAngle().y * dy + player.getLookAngle().z * dz) / dist;
        return dot > 0.95 && dist < 16; // если игрок смотрит почти прямо на жителя
    }
} 