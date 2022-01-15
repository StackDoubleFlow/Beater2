package net.stackdoubleflow.beater

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.server.command.ServerCommandSource
import java.util.stream.Collectors

class FakeCommandSource(player: ClientPlayerEntity) : ServerCommandSource(
    player,
    player.pos,
    player.rotationClient,
    null,
    0,
    player.entityName,
    player.name,
    null,
    player
) {
    override fun getPlayerNames(): Collection<String>? {
        return MinecraftClient.getInstance().networkHandler?.playerList
            ?.stream()?.map { e: PlayerListEntry ->
                e.profile.name
            }?.collect(Collectors.toList())
    }
}