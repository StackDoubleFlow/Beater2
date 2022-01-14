package net.stackdoubleflow.beater

import net.fabricmc.api.ClientModInitializer

object Beater: ClientModInitializer {
    private const val MOD_ID = "Beater"
    override fun onInitializeClient() {
        println("Example mod has been initialized.")
    }
}