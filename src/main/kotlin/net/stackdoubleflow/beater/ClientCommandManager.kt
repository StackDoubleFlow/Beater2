package net.stackdoubleflow.beater

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandException
import net.minecraft.text.*
import net.minecraft.util.Formatting
import kotlin.math.max
import kotlin.math.min

fun executeCommand(reader: StringReader, command: String) {
    println(command)
    val player = MinecraftClient.getInstance().player!!
    try {
        player.networkHandler.commandDispatcher.execute(reader, FakeCommandSource(player))
    } catch (e: CommandException) {
        sendError(e.textMessage)
    } catch (e: CommandSyntaxException) {
        sendError(Texts.toText(e.rawMessage))
        if (e.input != null && e.cursor >= 0) {
            val cursor = min(e.cursor, e.input.length)
            val text = LiteralText("").formatted(Formatting.GRAY)
                .styled { style: Style ->
                    style.withClickEvent(
                        ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            command
                        )
                    )
                }
            if (cursor > 10) text.append("...")
            text.append(e.input.substring(max(0, cursor - 10), cursor))
            if (cursor < e.input.length) {
                text.append(LiteralText(e.input.substring(cursor)).formatted(Formatting.RED, Formatting.UNDERLINE))
            }
            text.append(TranslatableText("command.context.here").formatted(Formatting.RED, Formatting.ITALIC))
            sendError(text)
        }
    } catch (e: Exception) {
        val error = LiteralText(if (e.message == null) e.javaClass.name else e.message)
        sendError(TranslatableText("command.failed")
            .styled { style: Style ->
                style.withHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        error
                    )
                )
            })
        e.printStackTrace()
    }

}

fun sendFeedback(message: Text) {
    MinecraftClient.getInstance().inGameHud.chatHud.addMessage(message)
}

fun sendError(error: Text) {
    sendFeedback(LiteralText("").append(error).formatted(Formatting.RED))
}