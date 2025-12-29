package com.veilorigins.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.veilorigins.VeilOrigins;

import java.util.function.Supplier;

public class OriginData {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VeilOrigins.MOD_ID);

    public static final Supplier<AttachmentType<PlayerOriginData>> PLAYER_ORIGIN = ATTACHMENT_TYPES.register(
        "player_origin", () -> AttachmentType.builder(PlayerOriginData::new)
            .serialize(new IAttachmentSerializer<PlayerOriginData>() {
                @Override
                public PlayerOriginData read(IAttachmentHolder holder, ValueInput input) {
                    PlayerOriginData data = new PlayerOriginData();
                    String originIdStr = input.getStringOr("originId", "");
                    if (originIdStr != null && !originIdStr.isEmpty()) {
                        data.originId = ResourceLocation.parse(originIdStr);
                    }
                    data.originLevel = input.getIntOr("originLevel", 1);
                    data.originXP = input.getIntOr("originXP", 0);
                    data.prestigeLevel = input.getIntOr("prestigeLevel", 0);
                    data.resourceBar = input.getFloatOr("resourceBar", 100.0f);
                    return data;
                }

                @Override
                public boolean write(PlayerOriginData data, ValueOutput output) {
                    if (data.originId != null) {
                        output.putString("originId", data.originId.toString());
                    } else {
                        output.putString("originId", "");
                    }
                    output.putInt("originLevel", data.originLevel);
                    output.putInt("originXP", data.originXP);
                    output.putInt("prestigeLevel", data.prestigeLevel);
                    output.putFloat("resourceBar", data.resourceBar);
                    return true;
                }
            })
            .build()
    );

    public static PlayerOriginData get(net.minecraft.world.entity.player.Player player) {
        return player.getData(PLAYER_ORIGIN);
    }

    public static class PlayerOriginData {
        private ResourceLocation originId;
        private int originLevel = 1;
        private int originXP = 0;
        private int prestigeLevel = 0;
        private float resourceBar = 100.0f;

        public PlayerOriginData() {}

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
