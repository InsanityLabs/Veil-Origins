package com.veilorigins.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.veilorigins.VeilOrigins;
import com.veilorigins.command.OriginCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerVeilOriginsCommands(Commands.CommandSelection selection, CommandBuildContext context, CallbackInfo info) {
        VeilOrigins.LOGGER.info("Registering Veil Origins commands...");
        
        // Ensure origins are registered
        com.veilorigins.registry.ModOrigins.register();
        VeilOrigins.LOGGER.info("Origins registered: {}", com.veilorigins.api.VeilOriginsAPI.getAllOrigins().size());
        
        Commands self = (Commands) (Object) this;
        CommandDispatcher<CommandSourceStack> dispatcher = self.getDispatcher();
        
        OriginCommand.register(dispatcher);
    }
}
