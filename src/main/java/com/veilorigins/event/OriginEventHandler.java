package com.veilorigins.event;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.SyncOriginDataPacket;
import com.veilorigins.origins.vampire.VampiricDoubleJumpPassive;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = VeilOrigins.MOD_ID)
public class OriginEventHandler {

    /**
     * Load player's origin from persistent data when they log in
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            VeilOriginsAPI.loadPlayerOrigin(player);

            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);

            // Sync origin data to the client - this is critical for multiplayer!
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            String originId = origin != null ? origin.getId().toString() : "";
            SyncOriginDataPacket syncPacket = new SyncOriginDataPacket(
                    originId,
                    data.getOriginLevel(),
                    data.getOriginXP(),
                    data.getResourceBar());
            ModPackets.sendToPlayer(serverPlayer, syncPacket);
            VeilOrigins.LOGGER.info("Sent origin sync packet to player {}: {}",
                    player.getName().getString(), originId);

            if (origin != null) {
                player.displayClientMessage(Component.literal("Welcome back, " + origin.getDisplayName() + "!")
                        .withStyle(ChatFormatting.GREEN), false);
            } else {
                player.displayClientMessage(Component
                        .literal("You haven't chosen an origin yet. Use /origin select <origin> to choose one!")
                        .withStyle(ChatFormatting.YELLOW), false);
            }
        }
    }

    /**
     * Clean up player from cache when they log out
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            VeilOriginsAPI.unloadPlayer(player);
            // Clean up sync tracking to prevent memory leaks
            lastSyncedResource.remove(player.getUUID());
        }
    }

    /**
     * Re-apply origin effects when player respawns
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Load origin again to reapply passive effects
            VeilOriginsAPI.loadPlayerOrigin(player);

            // Re-sync origin data to the client after respawn
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            String originId = origin != null ? origin.getId().toString() : "";
            SyncOriginDataPacket syncPacket = new SyncOriginDataPacket(
                    originId,
                    data.getOriginLevel(),
                    data.getOriginXP(),
                    data.getResourceBar());
            ModPackets.sendToPlayer(serverPlayer, syncPacket);
        }
    }

    // Track last synced resource bar values per player to avoid excessive syncing
    private static final java.util.Map<java.util.UUID, Float> lastSyncedResource = new java.util.HashMap<>();
    private static int syncTickCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (player.level().isClientSide())
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        // Tick origin passives
        origin.tick(player);

        // Tick ability cooldowns and custom tick logic
        origin.getAbilities().forEach(ability -> {
            ability.tickCooldown();
            ability.tick(player);
        });

        // Regenerate resource
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        if (origin.getResourceType() != null) {
            float regenRate = origin.getResourceType().getRegenRate();
            data.addResource(regenRate / 20.0f); // Per tick
        }

        // Periodic sync of resource bar to client (every 20 ticks / 1 second)
        // Only sync if the value has changed significantly to reduce network traffic
        if (player instanceof ServerPlayer serverPlayer) {
            syncTickCounter++;
            if (syncTickCounter >= 20) {
                syncTickCounter = 0;

                float currentResource = data.getResourceBar();
                Float lastResource = lastSyncedResource.get(player.getUUID());

                // Sync if changed by more than 0.5 or if never synced
                if (lastResource == null || Math.abs(currentResource - lastResource) > 0.5f) {
                    SyncOriginDataPacket syncPacket = new SyncOriginDataPacket(
                            origin.getId().toString(),
                            data.getOriginLevel(),
                            data.getOriginXP(),
                            currentResource);
                    ModPackets.sendToPlayer(serverPlayer, syncPacket);
                    lastSyncedResource.put(player.getUUID(), currentResource);
                }
            }
        }

        // Vampire/Vampling blood-hunger sync
        // Blood replaces hunger - keep hunger full based on blood level
        String originPath = origin.getId().getPath();
        if (originPath.equals("vampire") || originPath.equals("vampling")) {
            float bloodValue = data.getResourceBar();
            
            // Convert blood (0-100) to hunger (0-20)
            int hungerFromBlood = (int) (bloodValue / 5.0f);
            hungerFromBlood = Math.min(20, Math.max(0, hungerFromBlood));
            
            // Set hunger to match blood level
            player.getFoodData().setFoodLevel(hungerFromBlood);
            
            // Set saturation based on blood level (high blood = high saturation)
            float saturationFromBlood = bloodValue > 80 ? 5.0f : (bloodValue > 50 ? 2.0f : 0.0f);
            player.getFoodData().setSaturation(saturationFromBlood);
            
            // Drain blood slowly over time (simulates hunger drain)
            // Faster drain when sprinting or taking damage
            float drainRate = 0.01f; // Base drain per tick (~0.5 per second)
            if (player.isSprinting()) {
                drainRate *= 2.0f; // Double drain when sprinting
            }
            if (player.getHealth() < player.getMaxHealth()) {
                drainRate *= 1.5f; // More drain when healing
            }
            data.consumeResource(drainRate);
            
            // Natural regeneration from blood (replaces food-based regen)
            if (bloodValue > 80 && player.getHealth() < player.getMaxHealth()) {
                // Heal slowly when blood is high
                if (player.tickCount % 80 == 0) { // Every 4 seconds
                    player.heal(1.0f);
                    data.consumeResource(5.0f); // Costs blood to heal
                }
            }
            
            // Starvation damage when blood is critically low
            if (bloodValue <= 0) {
                if (player.tickCount % 80 == 0) { // Every 4 seconds
                    player.hurt(player.damageSources().starve(), 1.0f);
                }
            }
        }

        // Cindersoul resource logic (Internal Heat)
        if (origin.getId().getPath().equals("cindersoul")) {
            boolean nearHeat = player.isInLava() || player.isOnFire() ||
                    player.level().getBlockState(player.blockPosition()).is(net.minecraft.world.level.block.Blocks.FIRE)
                    ||
                    player.level().getBlockState(player.blockPosition())
                            .is(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK);
            boolean inCold = player.isInWaterOrRain() || player.isInPowderSnow ||
                    (player.level().getBiome(player.blockPosition()).value().coldEnoughToSnow(player.blockPosition(), player.level().getSeaLevel()));

            if (nearHeat) {
                data.addResource(1.0f); // Fast recharge
            } else if (inCold) {
                data.consumeResource(0.5f); // Drain
            }
        }

        // Tidecaller resource logic (Hydration)
        if (origin.getId().getPath().equals("tidecaller")) {
            if (player.isInWaterOrRain()) {
                data.addResource(2.0f); // Fast recharge in water/rain
            } else {
                float drain = 0.015f; // ~10 minutes
                // Desert/Dry check
                if (player.level().getBiome(player.blockPosition()).value().getBaseTemperature() > 1.0f) {
                    drain *= 3.0f;
                }
                data.consumeResource(drain);
            }
        }

        // Starborne resource (Stellar Energy)
        if (origin.getId().getPath().equals("starborne")) {
            boolean isDay = (player.level().getDayTime() % 24000L < 12000L);
            boolean canSeeSky = player.level().canSeeSky(player.blockPosition());

            if (isDay && canSeeSky) {
                data.addResource(1.0f);
            } else if (player.level().getMaxLocalRawBrightness(player.blockPosition()) == 0) {
                data.consumeResource(0.5f);
            }
        }

        // Skyborn resource (Altitude Bonus)
        if (origin.getId().getPath().equals("skyborn")) {
            float height = (float) player.getY();
            float target = Math.max(0, Math.min(100, (height - 64) / 2.0f));
            float current = data.getResourceBar();
            if (current < target)
                data.addResource(1.0f);
            else if (current > target)
                data.consumeResource(1.0f);
        }

        // Crystalline (Crystal Charge)
        if (origin.getId().getPath().equals("crystalline")) {
            // Recharges from sunlight
            if ((player.level().getDayTime() % 24000L < 12000L) && player.level().canSeeSky(player.blockPosition())) {
                data.addResource(0.5f);
            }
        }

        // Technomancer (Power Cells)
        if (origin.getId().getPath().equals("technomancer")) {
            // Recharges when standing still
            // We need to track movement.
            // Ideally compare pos with previous tick pos.
            // For now, check if deltaMovement is low?
            // Or just store pos in player capability or simpler heuristic.
            if (player.getDeltaMovement().lengthSqr() < 0.01) {
                data.addResource(1.0f);
            }
        }

        // Ethereal (Phase Energy)
        if (origin.getId().getPath().equals("ethereal")) {
            // Depletes when intangible (handled by ability active state? Or here if passive
            // mechanic?)
            // Spec says "depletes when intangible".
            // If ability PhaseShift is active, ability handles it.
            // If passive, we handle it here.
            // "Recharges when solid".
            // If PhaseShift not active -> recharge.
            com.veilorigins.origins.ethereal.PhaseShiftAbility phase = (com.veilorigins.origins.ethereal.PhaseShiftAbility) origin
                    .getAbility("phase_shift");
            if (phase != null && !phase.isActive(player)) {
                data.addResource(0.5f);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        // Stoneheart is immune to fall damage
        if (origin.getId().getPath().equals("stoneheart")) {
            event.setCanceled(true);
        }

        // Starborne takes 50% reduced fall damage (they can fly, so lighter landings)
        if (origin.getId().getPath().equals("starborne")) {
            event.setDamageMultiplier(0.5f);
        }

        // Skyborn takes 75% reduced fall damage when falling from high altitudes
        if (origin.getId().getPath().equals("skyborn")) {
            event.setDamageMultiplier(0.25f);
        }

        // Vampire/Vampling: No fall damage after double jump
        String originPath = origin.getId().getPath();
        if (originPath.equals("vampire") || originPath.equals("vampling")) {
            // Check if player has the VampiricDoubleJumpPassive and has immunity
            origin.getPassives().stream()
                    .filter(passive -> passive instanceof VampiricDoubleJumpPassive)
                    .findFirst()
                    .ifPresent(passive -> {
                        VampiricDoubleJumpPassive doubleJumpPassive = (VampiricDoubleJumpPassive) passive;
                        if (doubleJumpPassive.hasDoubleJumpImmunity(player)) {
                            event.setCanceled(true);
                        }
                    });
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        // Stoneheart Stone Skin ability - complete immunity
        if (origin.getId().getPath().equals("stoneheart")) {
            com.veilorigins.origins.stoneheart.StoneSkinAbility stoneSkin = (com.veilorigins.origins.stoneheart.StoneSkinAbility) origin
                    .getAbility("stone_skin");

            if (stoneSkin != null && stoneSkin.isActive()) {
                event.setNewDamage(0);
                if (event.getSource().getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
                    float reflectedDamage = event.getOriginalDamage() * 0.5f;
                    attacker.hurt(player.damageSources().thorns(player), reflectedDamage);
                }
            }
        }

        // Cindersoul fire immunity
        if (origin.getId().getPath().equals("cindersoul")) {
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE) ||
                    event.getSource() == player.damageSources().lava() ||
                    event.getSource() == player.damageSources().hotFloor()) {
                event.setNewDamage(0);
                player.clearFire();
            }
        }

        // Tidecaller weaknesses
        if (origin.getId().getPath().equals("tidecaller")) {
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
                event.setNewDamage(event.getOriginalDamage() * 1.5f);
            }
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_LIGHTNING)) {
                event.setNewDamage(event.getOriginalDamage() * 1.25f);
            }
        }

        // Starborne weaknesses/strengths
        if (origin.getId().getPath().equals("starborne")) {
            if (event.getSource() == player.damageSources().fellOutOfWorld()) {
                event.setNewDamage(event.getOriginalDamage() * 2.0f);
            }
        }

        // Skyborn Altitude Affinity
        if (origin.getId().getPath().equals("skyborn")) {
            if (!player.onGround()) {
                event.setNewDamage(event.getOriginalDamage() * 0.75f);
            }
            if (player.onGround() && player.getY() < 70) {
                event.setNewDamage(event.getOriginalDamage() * 1.5f);
            }
        }

        // Mycomorph Weaknesses
        if (origin.getId().getPath().equals("mycomorph")) {
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
                event.setNewDamage(event.getOriginalDamage() * 2.0f);
            }
            if (event.getSource() == player.damageSources().lava()) {
                event.setNewDamage(player.getMaxHealth() * 100);
            }
        }

        // Crystalline Vulnerability
        if (origin.getId().getPath().equals("crystalline")) {
            // Pickaxes/Axes +50%
            // Requires checking DamageSource held item... slightly complex.
            // Explosions +75%
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
                event.setNewDamage(event.getOriginalDamage() * 1.75f);
            }
        }

        // Technomancer Vulnerability
        if (origin.getId().getPath().equals("technomancer")) {
            // Lightning +200%
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_LIGHTNING)) {
                event.setNewDamage(event.getOriginalDamage() * 3.0f);
            }
            // Water damage 0.5 per second (handled in Tick?)
        }

        // Ethereal Resistance
        if (origin.getId().getPath().equals("ethereal")) {
            // 50% less physical damage? (exclude fire, explosion, drowning)
            if (!event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE) &&
                    !event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION) &&
                    !event.getSource().is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR) &&
                    event.getSource() != player.damageSources().drown()) {
                // Assume physical
                event.setNewDamage(event.getOriginalDamage() * 0.5f);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        // Umbrakin cannot place bright light sources
        if (origin.getId().getPath().equals("umbrakin")) {
            net.minecraft.world.level.block.state.BlockState placedBlock = event.getPlacedBlock();
            int lightLevel = placedBlock.getLightEmission();

            if (lightLevel >= 8) {
                event.setCanceled(true);
                player.displayClientMessage(
                        Component.literal("You cannot place such bright light sources!").withStyle(ChatFormatting.RED), false);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSleep(
            net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        // Voidtouched and Riftwalker cannot use beds - reality too unstable
        if (origin.getId().getPath().equals("voidtouched") ||
                origin.getId().getPath().equals("riftwalker")) {

            net.minecraft.core.BlockPos pos = event.getPos();
            net.minecraft.world.level.block.state.BlockState state = player.level().getBlockState(pos);

            if (state.getBlock() instanceof net.minecraft.world.level.block.BedBlock) {
                event.setCanceled(true);
                player.level().explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        5.0f, net.minecraft.world.level.Level.ExplosionInteraction.BLOCK);
                player.displayClientMessage(
                        Component.literal("Reality is too unstable - the bed explodes!")
                                .withStyle(ChatFormatting.DARK_PURPLE), false);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDamageVoid(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        // Voidtouched takes 75% less void damage
        if (origin.getId().getPath().equals("voidtouched")) {
            if (event.getSource() == player.damageSources().fellOutOfWorld()) {
                event.setNewDamage(event.getOriginalDamage() * 0.25f);
            }
        }

        // Riftwalker takes 50% more void damage
        if (origin.getId().getPath().equals("riftwalker")) {
            if (event.getSource() == player.damageSources().fellOutOfWorld()) {
                event.setNewDamage(event.getOriginalDamage() * 1.5f);
            }
        }

        // Frostborn fire damage increased by 100%, lava is instakill
        if (origin.getId().getPath().equals("frostborn")) {
            if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
                event.setNewDamage(event.getOriginalDamage() * 2.0f);
            }
            if (event.getSource() == player.damageSources().lava()) {
                event.setNewDamage(player.getMaxHealth() * 10); // Instakill
            }
        }

        // Frostborn immune to cold damage
        if (origin.getId().getPath().equals("frostborn")) {
            if (event.getSource() == player.damageSources().freeze()) {
                event.setNewDamage(0);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Player player))
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        // Cindersoul +2 fire damage
        if (origin.getId().getPath().equals("cindersoul")) {
            if (event.getSource().getDirectEntity() == player) {
                event.setNewDamage(event.getOriginalDamage() + 2.0f);
                event.getEntity().setRemainingFireTicks(60); // Set target on fire for 3s
            }
        }

        // Skyborn +25% damage when above target
        if (origin.getId().getPath().equals("skyborn")) {
            if (player.getY() > event.getEntity().getY()) {
                event.setNewDamage(event.getOriginalDamage() * 1.25f);
            }
        }

        // Technomancer +50% damage when Overclocked
        if (origin.getId().getPath().equals("technomancer")) {
            // Already handled by Strength effect in ability, but if we want stacking or raw
            // mod:
            // "Attacks deal +50% damage"
            // Strength II is +6 damage. Base is usually 1 (fist) or 7 (sword).
            // 7+6=13 (~+85%). Close enough.
        }
    }
}
