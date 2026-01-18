package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.client.gui.AbilityBarScreen;
import com.veilorigins.client.gui.HudConfigScreen;
import com.veilorigins.client.gui.OriginCardCarouselScreen;
import com.veilorigins.client.gui.SkillTreeScreen;
import com.veilorigins.data.OriginData;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.ActivateAbilityPacket;
import com.veilorigins.network.packet.DoubleJumpPacket;
import com.veilorigins.network.packet.LegendaryAbilityPacket;
import com.veilorigins.origins.vampire.VampiricDoubleJumpPassive;
import com.veilorigins.progression.ProgressionSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = VeilOrigins.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler {

    // Double jump state tracking
    private static boolean wasOnGround = true;
    private static boolean wasJumpPressed = false;
    private static boolean hasDoubleJumped = false;
    private static boolean wasSprintingOnGround = false;
    private static boolean jumpedWhileHoldingJump = false; // Track if player left ground while holding jump
    private static int jumpBufferTicks = 0; // Buffer to prevent accidental double jumps
    
    // Legendary ability state tracking (R + Attack combo)
    private static boolean isAbility1Held = false;
    private static boolean wasAttackPressed = false;
    private static boolean ability1JustPressed = false;
    private static int ability1HoldTimer = 0;
    private static final int HOLD_THRESHOLD = 5; // 5 ticks (0.25 seconds) to distinguish tap vs hold

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null)
            return;

        // Tick down cooldowns client-side for smooth HUD display
        ClientOriginData.tickCooldowns();

        // Handle auto-open origin selection on first join
        ClientEventHandler.tickOriginSelectDelay();

        // Always process double jump detection, even with screens open
        processDoubleJumpInput(mc, player);
        
        // Always process possession movement sync (WASD controls for possessed mob)
        processPossessionMovement(mc, player);

        // Don't process keybinds if a screen is open (except for closing radial menu)
        if (mc.screen != null)
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);

        // Check radial menu key - opens appropriate menu based on origin status
        if (KeyBindings.RADIAL_MENU.consumeClick()) {
            openRadialMenu(mc, origin);
            return;
        }

        // Check HUD config key - opens HUD configuration screen
        if (KeyBindings.HUD_CONFIG.consumeClick()) {
            mc.setScreen(new HudConfigScreen(null));
            VeilOrigins.LOGGER.debug("Opening HUD configuration screen");
            return;
        }

        // Check Skill Tree key - opens skill tree screen
        if (KeyBindings.SKILL_TREE.consumeClick()) {
            Origin currentOrigin = VeilOriginsAPI.getPlayerOrigin(player);
            if (currentOrigin != null) {
                mc.setScreen(new SkillTreeScreen(null));
                VeilOrigins.LOGGER.debug("Opening Skill Tree screen");
            } else {
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                        "§eYou need to select an origin first! Press §b[G]§e to choose."),
                    true);
            }
            return;
        }

        // Origin-specific keybinds only work if player has an origin
        if (origin == null) {
            // If player presses ability keys without an origin, prompt them to select one
            if (KeyBindings.ABILITY_1.consumeClick() || KeyBindings.ABILITY_2.consumeClick()) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                "§eYou haven't selected an origin yet! Press §b[G]§e to open the origin selection menu."),
                        true);
            }
            return;
        }

        // Process legendary ability combo (R held + Attack)
        processLegendaryAbilityCombo(mc, player, origin);

        // Check ability 1 key - fires on release if it was a quick tap (not held for combo)
        // The processLegendaryAbilityCombo handles the hold+attack case
        
        // Check ability 2 key
        if (KeyBindings.ABILITY_2.consumeClick()) {
            if (origin.getAbilities().size() > 1) {
                ModPackets.sendToServer(new ActivateAbilityPacket(1));
                VeilOrigins.LOGGER.debug("Sent ability 2 activation packet");
            }
        }

        // Check resource info key
        if (KeyBindings.RESOURCE_INFO.consumeClick()) {
            displayOriginInfo(player, origin);
        }
    }
    
    /**
     * Process ability 1 and legendary ability combo:
     * - Quick tap R = activate ability 1
     * - Hold R + Attack = activate legendary ability
     */
    private static void processLegendaryAbilityCombo(Minecraft mc, Player player, Origin origin) {
        Options options = mc.options;
        boolean isAbility1Down = KeyBindings.ABILITY_1.isDown();
        boolean isAttackDown = options.keyAttack.isDown();
        
        // Detect when R is first pressed
        if (isAbility1Down && !isAbility1Held) {
            isAbility1Held = true;
            ability1JustPressed = true;
            ability1HoldTimer = 0;
        }
        
        // Track how long R has been held
        if (isAbility1Held && isAbility1Down) {
            ability1HoldTimer++;
        }
        
        // Check for attack while R is held (legendary combo)
        if (isAbility1Held && isAbility1Down && isAttackDown && !wasAttackPressed) {
            // Check if player is level 50
            OriginData.PlayerOriginData data = OriginData.get(player);
            if (data.getOriginLevel() >= ProgressionSystem.MAX_LEVEL) {
                // Send legendary ability packet
                ModPackets.sendToServer(new LegendaryAbilityPacket());
                VeilOrigins.LOGGER.debug("Sent legendary ability activation packet");
                
                // Mark that we used the combo so we don't also fire ability 1 on release
                ability1JustPressed = false;
            } else {
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                        "§cLegendary ability requires Level 50! (Current: " + data.getOriginLevel() + ")"),
                    true);
            }
        }
        
        // When R is released
        if (!isAbility1Down && isAbility1Held) {
            // If it was a quick tap (not used for combo), fire ability 1
            if (ability1JustPressed && ability1HoldTimer < HOLD_THRESHOLD) {
                if (origin.getAbilities().size() > 0) {
                    ModPackets.sendToServer(new ActivateAbilityPacket(0));
                    VeilOrigins.LOGGER.debug("Sent ability 1 activation packet (quick tap)");
                }
            }
            
            // Reset state
            isAbility1Held = false;
            ability1JustPressed = false;
            ability1HoldTimer = 0;
        }
        
        wasAttackPressed = isAttackDown;
    }

    /**
     * Processes double jump input detection for vampiric abilities.
     * This runs every tick to detect jump key presses while in the air.
     */
    private static void processDoubleJumpInput(Minecraft mc, Player player) {
        Options options = mc.options;
        boolean isOnGround = player.onGround();
        boolean isJumpPressed = options.keyJump.isDown();

        // Decrease buffer
        if (jumpBufferTicks > 0) {
            jumpBufferTicks--;
        }

        // Track sprinting state when on ground
        if (isOnGround && player.isSprinting()) {
            wasSprintingOnGround = true;
        }

        // Detect if player left ground while holding jump - they need to release first
        if (wasOnGround && !isOnGround && isJumpPressed) {
            jumpedWhileHoldingJump = true;
        }

        // Reset double jump state when landing
        if (isOnGround && !wasOnGround) {
            hasDoubleJumped = false;
            jumpedWhileHoldingJump = false;
            jumpBufferTicks = 5; // Short buffer after landing to prevent immediate double jump
        }

        // Reset sprint tracking when on ground and not sprinting
        if (isOnGround && !player.isSprinting()) {
            wasSprintingOnGround = false;
        }

        // If player was holding jump when they left ground, they must release first
        if (jumpedWhileHoldingJump && !isJumpPressed) {
            jumpedWhileHoldingJump = false; // They released, now they can press again to double jump
        }

        // Detect double jump: player is in air, presses jump (not holding from ground
        // jump),
        // hasn't already double jumped
        if (!isOnGround && isJumpPressed && !wasJumpPressed && !hasDoubleJumped
                && !jumpedWhileHoldingJump && jumpBufferTicks <= 0) {
            // Check if player was sprinting or is currently sprinting
            if (wasSprintingOnGround || player.isSprinting()) {
                // Check if player has an origin with VampiricDoubleJumpPassive
                Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
                if (origin != null && hasVampiricDoubleJump(origin)) {
                    // Send double jump packet to server
                    ModPackets.sendToServer(new DoubleJumpPacket());
                    hasDoubleJumped = true;
                    VeilOrigins.LOGGER.debug("Sent double jump packet for player");
                }
            }
        }

        // Update state tracking
        wasOnGround = isOnGround;
        wasJumpPressed = isJumpPressed;
    }

    /**
     * Checks if the origin has the VampiricDoubleJumpPassive.
     */
    private static boolean hasVampiricDoubleJump(Origin origin) {
        return origin.getPassives().stream()
                .anyMatch(passive -> passive instanceof VampiricDoubleJumpPassive);
    }

    /**
     * Sends player movement input to server when possessing a mob.
     * No longer needed - player moves normally and mob follows on server.
     */
    private static void processPossessionMovement(Minecraft mc, Player player) {
        // Movement is now handled automatically - mob follows player position on server
    }

    /**
     * Opens the appropriate menu based on origin status.
     * If player has no origin, opens the card carousel for origin selection.
     * If player has an origin, opens the ability bar for ability selection.
     */
    private static void openRadialMenu(Minecraft mc, Origin origin) {
        if (origin == null) {
            // Use the card carousel for origin selection
            mc.setScreen(new OriginCardCarouselScreen());
            VeilOrigins.LOGGER.debug("Opening card carousel for origin selection");
        } else {
            // Use the new ability bar for ability selection
            mc.setScreen(new AbilityBarScreen());
            VeilOrigins.LOGGER.debug("Opening ability bar for origin: {}", origin.getId());
        }
    }

    /**
     * Displays detailed origin information to the player.
     */
    private static void displayOriginInfo(Player player, Origin origin) {
        StringBuilder info = new StringBuilder();
        info.append(String.format("§d=== %s ===§r\n", origin.getDisplayName()));
        info.append(String.format("§7%s§r\n", origin.getDescription()));
        info.append(String.format("§eAbilities: §f%d | §ePassives: §f%d\n",
                origin.getAbilities().size(),
                origin.getPassives().size()));

        if (origin.getResourceType() != null) {
            info.append(String.format("§bResource: §f%s§r",
                    origin.getResourceType().getName()));
        }

        // Display main info in chat
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                        String.format("§6Origin: §e%s §7| Abilities: %d | Press §b[G]§7 for menu",
                                origin.getDisplayName(),
                                origin.getAbilities().size())),
                false);
    }
}
