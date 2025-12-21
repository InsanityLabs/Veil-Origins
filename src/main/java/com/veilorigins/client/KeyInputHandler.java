package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.client.gui.HudConfigScreen;
import com.veilorigins.client.gui.RadialMenuScreen;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.ActivateAbilityPacket;
import com.veilorigins.network.packet.DoubleJumpPacket;
import com.veilorigins.origins.vampire.VampiricDoubleJumpPassive;
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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null)
            return;

        // Handle auto-open origin selection on first join
        ClientEventHandler.tickOriginSelectDelay();

        // Always process double jump detection, even with screens open
        processDoubleJumpInput(mc, player);

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

        // Check ability 1 key
        if (KeyBindings.ABILITY_1.consumeClick()) {
            if (origin.getAbilities().size() > 0) {
                ModPackets.sendToServer(new ActivateAbilityPacket(0));
                VeilOrigins.LOGGER.debug("Sent ability 1 activation packet");
            }
        }

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
     * Opens the radial menu with the appropriate mode.
     * If player has no origin, opens origin selection.
     * If player has an origin, opens ability selection.
     */
    private static void openRadialMenu(Minecraft mc, Origin origin) {
        RadialMenuScreen.MenuMode mode;

        if (origin == null) {
            mode = RadialMenuScreen.MenuMode.ORIGIN_SELECT;
            VeilOrigins.LOGGER.debug("Opening radial menu in ORIGIN_SELECT mode");
        } else {
            mode = RadialMenuScreen.MenuMode.ABILITY_SELECT;
            VeilOrigins.LOGGER.debug("Opening radial menu in ABILITY_SELECT mode for origin: {}", origin.getId());
        }

        mc.setScreen(new RadialMenuScreen(mode));
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
