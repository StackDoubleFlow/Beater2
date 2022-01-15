package net.stackdoubleflow.beater

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Vec3d

object Beater : ClientModInitializer {
    private const val MOD_ID = "Beater"
    private val tracks = listOf(Track("overgrown", Vec3d(154.5, 86.0, -149.5)))

    private var runningRace: RunningRace? = null;
    private lateinit var client: MinecraftClient;

    fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("race").then(CommandManager.literal("start").executes { ctx ->
            if (runningRace != null) {
                ctx.source.sendFeedback(LiteralText("There is already a race running."), false)
                return@executes 1
            }
            if (client.player?.vehicle?.type != EntityType.BOAT) {
                ctx.source.sendFeedback(LiteralText("You are not in a boat."), false)
                return@executes 1
            }
            val track = tracks[0];

            runningRace = RunningRace(client, tracks[0])
            ctx.source.sendFeedback(LiteralText("Started race on track \"${track.name}\""), false)
            1
        }))
        dispatcher.register(CommandManager.literal("race").then(CommandManager.literal("cancel").executes { ctx ->
            stopRace()
            ctx.source.sendFeedback(LiteralText("Cancelled race"), false)
            1
        }))
    }

    fun stopRace() {
        runningRace = null
    }

    override fun onInitializeClient() {
        client = MinecraftClient.getInstance()

        ClientTickEvents.END_CLIENT_TICK.register {
            runningRace?.tick()
        }

        HudRenderCallback.EVENT.register { matrixStack, _ ->
            runningRace?.renderOverlay(matrixStack)
        }

        println("Beater has been initialized.")
    }
}