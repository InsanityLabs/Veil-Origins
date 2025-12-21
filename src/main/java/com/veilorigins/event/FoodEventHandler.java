package com.veilorigins.event;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.UnicodeFontHandler;
import com.veilorigins.api.VeilOriginsAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

/**
 * Handles food-related events for origins with special dietary preferences.
 * - Werewolf/Wolfling: Can eat rotten flesh without hunger effect, bonus from
 * raw meat
 * - Vampire/Vampling: Bonus from raw meat (blood preference)
 */
@EventBusSubscriber(modid = VeilOrigins.MOD_ID)
public class FoodEventHandler {

    /**
     * Called when an entity finishes using an item (like eating food).
     * Provides bonuses for eating preferred foods.
     */
    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (player.level().isClientSide())
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        String originPath = origin.getId().getPath();
        ItemStack item = event.getItem();

        // Handle Werewolf and Wolfling food preferences
        if (originPath.equals("werewolf") || originPath.equals("wolfling")) {
            handleWolfkinFood(player, item, originPath.equals("werewolf"));
        }

        // Handle Vampire and Vampling food preferences
        if (originPath.equals("vampire") || originPath.equals("vampling")) {
            handleVampireFood(player, item, originPath.equals("vampire"));
        }
    }

    /**
     * Handles food effects for werewolves and wolflings.
     */
    private static void handleWolfkinFood(Player player, ItemStack item, boolean isFullWerewolf) {
        // Raw meat gives extra saturation and regeneration
        if (isRawMeat(item)) {
            // Bonus regeneration from raw meat
            int regenLevel = isFullWerewolf ? 1 : 0; // Regeneration II for werewolf, I for wolfling
            int duration = isFullWerewolf ? 100 : 60; // 5 or 3 seconds

            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, regenLevel, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20, 0, false, false));

            String star = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_STAR, "*");
            player.displayClientMessage(
                    Component.literal(star + " The raw meat invigorates you!").withStyle(ChatFormatting.GOLD),
                    true);
        }

        // Rotten flesh - we'll handle the hunger removal in the effect application
        // event
        if (item.is(Items.ROTTEN_FLESH)) {
            // Also give slight healing as they actually benefit from it
            player.heal(isFullWerewolf ? 2.0f : 1.0f);
            String star = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_STAR, "*");
            player.displayClientMessage(
                    Component.literal(star + " The rotten flesh satisfies your feral hunger!")
                            .withStyle(ChatFormatting.GOLD),
                    true);
        }
    }

    /**
     * Handles food effects for vampires and vamplings.
     */
    private static void handleVampireFood(Player player, ItemStack item, boolean isFullVampire) {
        // Raw meat gives regeneration (blood still in the meat)
        if (isRawMeat(item)) {
            int regenLevel = isFullVampire ? 1 : 0;
            int duration = isFullVampire ? 80 : 40;

            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, regenLevel, false, true));

            String star = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_STAR, "*");
            player.displayClientMessage(
                    Component.literal(star + " The blood in the meat sustains you!").withStyle(ChatFormatting.DARK_RED),
                    true);

            // Vampires also get slight strength boost from the blood
            if (isFullVampire) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, true));
            }
        }

        // Cooked meat provides less benefit (blood is gone)
        if (isCookedMeat(item)) {
            player.displayClientMessage(
                    Component.literal("The cooked meat provides little nourishment...").withStyle(ChatFormatting.GRAY),
                    true);
            // Apply slight weakness as cooked food doesn't satisfy vampires
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false));
        }
    }

    /**
     * Cancel hunger effect from rotten flesh for werewolves and wolflings.
     */
    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (player.level().isClientSide())
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        String originPath = origin.getId().getPath();

        // Werewolves and Wolflings are immune to hunger from food
        if (originPath.equals("werewolf") || originPath.equals("wolfling")) {
            if (event.getEffectInstance().getEffect() == MobEffects.HUNGER) {
                // Cancel the hunger effect
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                VeilOrigins.LOGGER.debug("Cancelled hunger effect for {}", player.getName().getString());
            }
        }
    }

    /**
     * Checks if the item is raw meat.
     */
    private static boolean isRawMeat(ItemStack item) {
        return item.is(Items.BEEF) ||
                item.is(Items.PORKCHOP) ||
                item.is(Items.CHICKEN) ||
                item.is(Items.MUTTON) ||
                item.is(Items.RABBIT) ||
                item.is(Items.COD) ||
                item.is(Items.SALMON) ||
                item.is(Items.TROPICAL_FISH);
    }

    /**
     * Checks if the item is cooked meat.
     */
    private static boolean isCookedMeat(ItemStack item) {
        return item.is(Items.COOKED_BEEF) ||
                item.is(Items.COOKED_PORKCHOP) ||
                item.is(Items.COOKED_CHICKEN) ||
                item.is(Items.COOKED_MUTTON) ||
                item.is(Items.COOKED_RABBIT) ||
                item.is(Items.COOKED_COD) ||
                item.is(Items.COOKED_SALMON);
    }
}
