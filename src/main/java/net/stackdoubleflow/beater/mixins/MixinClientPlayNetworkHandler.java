package net.stackdoubleflow.beater.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.stackdoubleflow.beater.Beater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    void onInit(MinecraftClient mc,
                Screen screen,
                ClientConnection connection,
                GameProfile profile,
                CallbackInfo ci
    ) {
        Beater.INSTANCE.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }

    @Inject(method = "onCommandTree", at = @At("TAIL"))
    public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        Beater.INSTANCE.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }
}