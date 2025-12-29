package com.veilorigins.registry;

import com.veilorigins.VeilOrigins;
import com.veilorigins.item.BloodBottleItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registry for all Veil Origins items.
 */
public class ModItems {
    
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VeilOrigins.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VeilOrigins.MOD_ID);
    
    // Blood Bottle - empty, can be filled by vampires draining blood
    public static final DeferredItem<BloodBottleItem> BLOOD_BOTTLE_EMPTY = ITEMS.registerItem(
            "blood_bottle_empty",
            props -> new BloodBottleItem(props, 0),
            () -> new Item.Properties().stacksTo(16));
    
    // Blood Bottle - half full
    public static final DeferredItem<BloodBottleItem> BLOOD_BOTTLE_HALF = ITEMS.registerItem(
            "blood_bottle_half",
            props -> new BloodBottleItem(props, 50),
            () -> new Item.Properties().stacksTo(16));
    
    // Blood Bottle - full
    public static final DeferredItem<BloodBottleItem> BLOOD_BOTTLE_FULL = ITEMS.registerItem(
            "blood_bottle_full",
            props -> new BloodBottleItem(props, 100),
            () -> new Item.Properties().stacksTo(16));
    
    // Creative Tab for Veil Origins items
    public static final Supplier<CreativeModeTab> VEIL_ORIGINS_TAB = CREATIVE_MODE_TABS.register("veil_origins_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.veil_origins"))
                    .icon(() -> new ItemStack(BLOOD_BOTTLE_FULL.get()))
                    .displayItems((params, output) -> {
                        output.accept(BLOOD_BOTTLE_EMPTY.get());
                        output.accept(BLOOD_BOTTLE_HALF.get());
                        output.accept(BLOOD_BOTTLE_FULL.get());
                    })
                    .build());
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        VeilOrigins.LOGGER.info("Veil Origins: Registered items and creative tab");
    }
}
