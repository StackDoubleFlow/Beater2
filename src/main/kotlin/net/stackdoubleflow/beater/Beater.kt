package net.stackdoubleflow.beater

import net.fabricmc.api.ModInitializer

object Beater: ModInitializer {
    private const val MOD_ID = "Beater"
    override fun onInitialize() {
        println("Example mod has been initialized.")
    }
}