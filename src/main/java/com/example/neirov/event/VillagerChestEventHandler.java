package com.example.neirov.event;

import com.example.neirov.villager.ChestMemory;
import com.example.neirov.villager.VillagerMemory;
import com.example.neirov.villager.VillagerMemoryProvider;
import com.example.neirov.villager.GoalWatchAndHide;
import com.example.neirov.villager.VillagerTrust;
import com.example.neirov.villager.VillagerTrustProvider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.animal.IronGolem;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = "neirov")
public class VillagerChestEventHandler {
    // Временное хранение памяти о сундуках для каждого игрока
    private static final HashMap<UUID, ChestMemory> chestMemories = new HashMap<>();

    @SubscribeEvent
    public static void onChestOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockEntity blockEntity = level.getBlockEntity(event.getContainer().containerId);
        if (blockEntity instanceof ChestBlockEntity) {
            ChestBlockEntity chest = (ChestBlockEntity) blockEntity;
            chestMemories.put(player.getUUID(), new ChestMemory(chest));
            BlockPos chestPos = chest.getBlockPos();
            // Поиск жителей рядом с сундуком (радиус 8 блоков)
            List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(chestPos).inflate(8));
            for (Villager villager : villagers) {
                // TODO: Проверить видимость игрока для жителя
                // TODO: Реакция жителя (угроза, звать голема и т.д.)
            }
        }
    }

    // Проверка, видит ли житель игрока (простой вариант: расстояние и прямой взгляд)
    private static boolean canVillagerSeePlayer(Villager villager, Player player) {
        double maxDist = 8.0;
        if (villager.distanceTo(player) > maxDist) return false;
        // Можно добавить raytrace для проверки препятствий
        return villager.hasLineOfSight(player);
    }

    private static void assignChestGuarding(List<Villager> villagers, BlockPos chestPos) {
        Random rand = new Random();
        for (Villager villager : villagers) {
            if (rand.nextInt(100) < 15) {
                // Назначаем жителю цель — стоять рядом с сундуком
                villager.getNavigation().moveTo(chestPos.getX() + 0.5, chestPos.getY(), chestPos.getZ() + 0.5, 0.8);
                villager.setCustomNameVisible(true);
                villager.setCustomName(net.minecraft.network.chat.Component.literal("Я охраняю сундук!"));
                // TODO: Можно добавить AI Goal для патрулирования
            }
        }
    }

    private static void villagerReactToTheft(Villager villager, Player player, Level level) {
        // 1. Житель запоминает воришку
        VillagerMemory memory = villager.getCapability(VillagerMemoryProvider.MEMORY).orElse(null);
        if (memory != null) {
            memory.rememberThief(player);
        }
        // 2. Пытается найти ближайшего голема и бежит к нему
        List<IronGolem> golems = level.getEntitiesOfClass(IronGolem.class, villager.getBoundingBox().inflate(32));
        if (!golems.isEmpty()) {
            IronGolem golem = golems.get(0); // ближайший
            villager.getNavigation().moveTo(golem, 1.2);
            villager.setCustomNameVisible(true);
            villager.setCustomName(net.minecraft.network.chat.Component.literal("Голем! Помоги!"));
        } else {
            // 3. Если голема нет — зовёт других жителей (с шансом 34%)
            Random rand = new Random();
            if (rand.nextInt(100) < 34) {
                List<Villager> others = level.getEntitiesOfClass(Villager.class, villager.getBoundingBox().inflate(16));
                for (Villager other : others) {
                    if (other != villager) {
                        other.setCustomNameVisible(true);
                        other.setCustomName(net.minecraft.network.chat.Component.literal("Вор! Берегите сундуки!"));
                        // 15% из них остаются рядом с сундуками
                        assignChestGuarding(List.of(other), villager.blockPosition());
                    }
                }
            }
        }
        // Уникальные реакции профессий
        VillagerProfession prof = villager.getVillagerData().getProfession();
        switch (prof) {
            case FARMER:
                // Фермер зовёт других фермеров и прячет еду
                villager.setCustomName(net.minecraft.network.chat.Component.literal("Фермер зовёт на помощь и прячет еду!"));
                // TODO: Реализовать прятки еды
                break;
            case LIBRARIAN:
                // Библиотекарь жалуется голему и другим
                villager.setCustomName(net.minecraft.network.chat.Component.literal("Библиотекарь жалуется голему!"));
                // TODO: Реализовать жалобу
                break;
            case ARMORER:
            case WEAPONSMITH:
            case TOOLSMITH:
            case BLACKSMITH:
                // Кузнец берёт оружие и атакует
                villager.setCustomName(net.minecraft.network.chat.Component.literal("Кузнец берёт молот и идёт в бой!"));
                villager.setAggressive(true);
                villager.setTarget(player);
                // TODO: Дать оружие
                break;
            case CLERIC:
                // Священник может простить за изумруды
                villager.setCustomName(net.minecraft.network.chat.Component.literal("Священник: 'Пожертвуй изумруды — и я прощу тебя!'"));
                // TODO: Реализовать квест на искупление
                break;
            case CARTOGRAPHER:
                // Картограф предупреждает других
                villager.setCustomName(net.minecraft.network.chat.Component.literal("Картограф отмечает воришку на карте!"));
                // TODO: Реализовать передачу информации
                break;
            default:
                // Обычная реакция
                break;
        }
    }

    @SubscribeEvent
    public static void onChestClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockEntity blockEntity = level.getBlockEntity(event.getContainer().containerId);
        if (blockEntity instanceof ChestBlockEntity) {
            ChestBlockEntity chest = (ChestBlockEntity) blockEntity;
            ChestMemory memory = chestMemories.get(player.getUUID());
            if (memory != null) {
                memory.saveAfter(chest);
                boolean stolen = memory.isStolen();
                BlockPos chestPos = chest.getBlockPos();
                List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(chestPos).inflate(8));
                for (Villager villager : villagers) {
                    if (canVillagerSeePlayer(villager, player)) {
                        if (stolen) {
                            // Житель запоминает воришку и зовёт голема/других
                            villagerReactToTheft(villager, player, level);
                            // Доверие падает за кражу
                            VillagerTrust trust = villager.getCapability(VillagerTrustProvider.TRUST).orElse(null);
                            if (trust != null) {
                                trust.changeTrust(player, -30);
                            }
                        } else {
                            // Житель угрожает игроку
                            villager.getNavigation().moveTo(player, 1.0);
                            villager.setCustomNameVisible(true);
                            villager.setCustomName(net.minecraft.network.chat.Component.literal("Не трогай сундук!"));
                            // Если игрок ушёл и ничего не украл — с шансом 35% житель следит за игроком
                            if (ThreadLocalRandom.current().nextInt(100) < 35) {
                                int seconds = 120 + ThreadLocalRandom.current().nextInt(120); // 2-4 минуты
                                villager.goalSelector.addGoal(1, new GoalWatchAndHide(villager, player.getUUID(), seconds));
                            }
                        }
                    }
                }
                chestMemories.remove(player.getUUID());
            }
        }
    }
} 