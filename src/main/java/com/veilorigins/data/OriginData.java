package com.veilorigins.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.veilorigins.VeilOrigins;

import java.util.function.Supplier;

public class OriginData {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VeilOrigins.MOD_ID);

    public static final Supplier<AttachmentType<PlayerOriginData>> PLAYER_ORIGIN = ATTACHMENT_TYPES.register(
        "player_origin", () -> AttachmentType.builder(PlayerOriginData::new).serialize(new PlayerOriginData.Codec()).build()
    );

    public static class PlayerOriginData {
        private ResourceLocation originId;
        private int originLevel = 1;
        private int originXP = 0;
        private int prestigeLevel = 0;
        private float resourceBar = 100.0f;

        public PlayerOriginData() {}
        
        public static class Codec implements net.neoforged.neoforge.attachment.IAttachmentSerializer<CompoundTag, PlayerOriginData> {
            @Override
            public PlayerOriginData read(net.neoforged.neoforge.attachment.IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                PlayerOriginData data = new PlayerOriginData();
                if (tag.contains("originId")) {
                    data.originId = ResourceLocation.parse(tag.getString("originId"));
                }
                data.originLevel = tag.getInt("originLevel");
                data.originXP = tag.getInt("originXP");
                data.prestigeLevel = tag.getInt("prestigeLevel");
                data.resourceBar = tag.getFloat("resourceBar");
                return data;
            }

            @Override
            public CompoundTag write(PlayerOriginData data, HolderLookup.Provider provider) {
                CompoundTag tag = new CompoundTag();
                if (data.originId != null) {
                    tag.putString("originId", data.originId.toString());
                }
                tag.putInt("originLevel", data.originLevel);
                tag.putInt("originXP", data.originXP);
                tag.putInt("prestigeLevel", data.prestigeLevel);
                tag.putFloat("resourceBar", data.resourceBar);
                return tag;
            }
        }

        public ResourceLocation getOriginId() { return originId; }
        public void setOriginId(ResourceLocation id) { this.originId = id; }
        
        public int getOriginLevel() { return originLevel; }
        public void setOriginLevel(int level) { this.originLevel = level; }
        
        public int getOriginXP() { return originXP; }
        public void setOriginXP(int xp) { this.originXP = xp; }
        
        public int getPrestigeLevel() { return prestigeLevel; }
        public void setPrestigeLevel(int level) { this.prestigeLevel = level; }
        
        public float getResourceBar() { return resourceBar; }
        public void setResourceBar(float value) { this.resourceBar = Math.max(0, Math.min(100, value)); }

        public void addXP(int amount) {
            this.originXP += amount;
        }

        public void addResource(float amount) {
            setResourceBar(resourceBar + amount);
        }

        public void consumeResource(float amount) {
            setResourceBar(resourceBar - amount);
        }
    }
}
