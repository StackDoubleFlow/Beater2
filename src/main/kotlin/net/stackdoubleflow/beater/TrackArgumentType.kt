package net.stackdoubleflow.beater

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import java.util.concurrent.CompletableFuture

class TrackArgumentType : ArgumentType<String> {
    override fun parse(reader: StringReader): String = reader.readUnquotedString()

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        CommandSource.suggestMatching(Beater.tracks.keys, builder)
        return builder.buildFuture()
    }

    companion object {
        fun getTrack(context: CommandContext<*>, name: String?): String? {
            return context.getArgument(name, String::class.java)
        }
    }
}