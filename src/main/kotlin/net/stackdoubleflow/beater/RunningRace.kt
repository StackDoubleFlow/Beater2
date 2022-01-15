package net.stackdoubleflow.beater

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.LiteralText
import kotlin.math.absoluteValue

private const val DING_1 = -3_000_000_000
private const val DING_2 = -2_000_000_000
private const val DING_3 = -1_000_000_000

private const val PRE_PITCH = 1F // Note Block note 12
private const val GO_PITCH = 1.587401F // Note Block note 20

class RunningRace(private val client: MinecraftClient, private val track: Track) {
    private val startTime = System.nanoTime() + 3_000_000_000
    private val player = client.player!!
    private val boat = player.vehicle
    private var lastTickTime = 0L

    private fun timeElapsedStr(timeElapsed: Long): String {
        val neg = timeElapsed < 0

        val elapsedNanos = timeElapsed.absoluteValue
        val elapsedMillis = elapsedNanos / 1000000
        val elapsedSeconds = elapsedMillis / 1000
        val elapsedMinutes = elapsedSeconds / 60

        val secondsPart = elapsedSeconds - (elapsedMinutes * 60)
        val millisPart = elapsedMillis - (elapsedSeconds * 1000)

        val builder = StringBuilder()
        if (neg) builder.append('-')
        builder.append(elapsedMinutes.toString().padStart(2, '0'))
            .append(':')
            .append(secondsPart.toString().padStart(2, '0'))
            .append('.')
            .append(millisPart.toString().padStart(3, '0'))

        return builder.toString()
    }

    fun renderOverlay(matrixStack: MatrixStack) {
        val timeElapsed = System.nanoTime() - startTime
        val text = LiteralText(timeElapsedStr(timeElapsed))

        val renderer = client.textRenderer
        val window = client.window

        val screenWidth = window.scaledWidth
        val textWidth = renderer.getWidth(text)
        val x = screenWidth / 2 - textWidth / 2

        val screenHeight = window.scaledHeight
        val y = screenHeight * 0.20f

        renderer.draw(matrixStack, text, x.toFloat(), y, 0xFFFFFF)
    }


    fun tick() {
        if (player.vehicle != boat) {
            sendError(LiteralText("Player is no longer in boat; race cancelled"))
            Beater.stopRace()
            return
        }

        val now = System.nanoTime()
        val timeElapsed = now - startTime
        val lastTimeElapsed = lastTickTime - startTime
        if (timeElapsed < 0) {
            player.vehicle?.setPosition(track.startPos)
            if (DING_1 in (lastTimeElapsed + 1) until timeElapsed) {
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 3.0f, PRE_PITCH)
            } else if (DING_2 in (lastTimeElapsed + 1) until timeElapsed) {
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 3.0f, PRE_PITCH)
            } else if (DING_3 in (lastTimeElapsed + 1) until timeElapsed) {
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 3.0f, PRE_PITCH)
            }
        }
        if (timeElapsed >= 0 && lastTimeElapsed < 0) {
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 3.0f, GO_PITCH)
        }

        lastTickTime = now
    }
}