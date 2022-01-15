package net.stackdoubleflow.beater

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import kotlin.math.absoluteValue

class RunningRace(private val client: MinecraftClient, private val track: Track) {
    private val startTime = System.nanoTime() + 3_000_000_000

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

        val client = MinecraftClient.getInstance()
        val renderer = client.textRenderer
        val window = client.window;

        val screenWidth = window.scaledWidth
        val textWidth = renderer.getWidth(text)
        val x = screenWidth / 2 - textWidth / 2;

        val screenHeight = window.scaledHeight
        val y = screenHeight * 0.20f;

        renderer.draw(matrixStack, text, x.toFloat(), y, 0xFFFFFF)
    }


    fun tick() {
        client.player?.movementSpeed = 0f
        client.player?.vehicle?.setPos(154.5, 86.0, -149.5)

    }
}