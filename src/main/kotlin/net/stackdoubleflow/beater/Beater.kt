package net.stackdoubleflow.beater

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

object Beater : ClientModInitializer {
    val tracks = mapOf(
        "overgrown" to Track(
            Vec3d(-248.5, 65.0, 106.5),
            listOf(
                Line(Vec2f(-2f, 165f), Vec2f(-12f, 165f)),
                Line(Vec2f(-175f, 170f), Vec2f(-175f, 160f))
            ),
            Line(Vec2f(-252f, 106f), Vec2f(-244f, 106f))
        )
    )

    private var runningRace: RunningRace? = null
    private lateinit var client: MinecraftClient

    fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {

        dispatcher.register(
            CommandManager.literal("race")
                .then(
                    CommandManager.literal("start")
                        .then(CommandManager.argument("track_name", TrackArgumentType()).executes { ctx ->
                            if (runningRace != null) {
                                sendError(LiteralText("There is already a race running"))
                                return@executes 1
                            }

                            val trackName = TrackArgumentType.getTrack(ctx, "track_name")
                            val track = tracks[trackName]
                            if (track == null) {
                                sendError(LiteralText("Could not find track"))
                                return@executes 1
                            }

                            if (track.startPos.distanceTo(client.player!!.pos) > 5) {
                                sendError(LiteralText("Please move closer to the track starting position"))
                            }

                            if (client.player?.vehicle?.type != EntityType.BOAT) {
                                sendError(LiteralText("You are not in a boat"))
                                return@executes 1
                            }

                            runningRace = RunningRace(client, track)
                            ctx.source.sendFeedback(LiteralText("Started race on track \"${trackName}\""), false)
                            1
                        })
                )


        )
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