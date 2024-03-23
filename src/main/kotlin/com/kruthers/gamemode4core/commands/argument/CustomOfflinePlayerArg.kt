package com.kruthers.gamemode4core.commands.argument

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.bukkit.BukkitCaptionKeys
import cloud.commandframework.captions.CaptionVariable
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import cloud.commandframework.exceptions.parsing.ParserException
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import java.util.*
import java.util.function.BiFunction

class CustomOfflinePlayerArg<C: CommandSender> (
    required: Boolean,
    name: String,
    defaultValue: String,
    suggestionsProvider: BiFunction<CommandContext<C>, String, List<String>>?,
    defaultDescription: ArgumentDescription
) : CommandArgument<C, OfflinePlayer>(
    required,
    name,
    CustomOfflinePlayerParser<C>(),
    defaultValue,
    OfflinePlayer::class.java,
    suggestionsProvider,
    defaultDescription
) {

    companion object {
        fun <C : CommandSender> builder(name: String): Builder<C> {
            return Builder<C>(name)
        }

        fun <C : CommandSender> of(name: String): CommandArgument<C, OfflinePlayer> {
            return builder<C>(name).asRequired().build()
        }

        fun <C : CommandSender> optional(name: String): CommandArgument<C, OfflinePlayer> {
            return builder<C>(name).asOptional().build()
        }

        fun <C : CommandSender> optional(
            name: String,
            defaultPlayer: String
        ): CommandArgument<C, OfflinePlayer> {
            return builder<C>(name).asOptionalWithDefault(defaultPlayer).build()
        }


        class Builder<C : CommandSender> internal constructor(name: String) :
            CommandArgument.Builder<C, OfflinePlayer>(OfflinePlayer::class.java, name) {

            override fun build(): CommandArgument<C, OfflinePlayer> {
                return CustomOfflinePlayerArg(
                    this.isRequired, name, defaultValue,
                    suggestionsProvider, defaultDescription
                )
            }
        }
    }


    private class CustomOfflinePlayerParser<C: CommandSender> : ArgumentParser<C, OfflinePlayer> {
        @Suppress("deprecation")
        override fun parse(
            commandContext: CommandContext<C>,
            inputQueue: Queue<String>
        ): ArgumentParseResult<OfflinePlayer> {
            val input = inputQueue.peek()
                ?: return ArgumentParseResult.failure(
                    NoInputProvidedException(CustomOfflinePlayerParser::class.java, commandContext)
                )
            val player = Bukkit.getOfflinePlayer(input)
            if (!player.hasPlayedBefore())
                return ArgumentParseResult.failure(OfflinePlayerParseException(input, commandContext))

            inputQueue.remove()
            return ArgumentParseResult.success(player)
        }

        override fun suggestions(
            commandContext: CommandContext<C>,
            input: String
        ): List<String> {
            return Bukkit.getOfflinePlayers().mapNotNull { it.name }.filter { it.contains(input, false) }
        }
    }


    private class OfflinePlayerParseException (input: String, context: CommandContext<*>) : ParserException(
        CustomOfflinePlayerParser::class.java,
        context,
        BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_OFFLINEPLAYER,
        CaptionVariable.of("input", input)
    )

}