package me.quantiom.advancedvanish.hook.impl

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedServerPing
import me.quantiom.advancedvanish.AdvancedVanish
import me.quantiom.advancedvanish.hook.IHook
import me.quantiom.advancedvanish.util.AdvancedVanishAPI

class ServerListHook : IHook {
    private val packetListener = object : PacketAdapter(
        params(
            AdvancedVanish.instance,
            PacketType.Status.Server.OUT_SERVER_INFO
        ).optionAsync()
    ) {
        override fun onPacketSending(event: PacketEvent?) {
            val ping = event!!.packet.serverPings.read(0) as WrappedServerPing

            // Adjust the online player count
            ping.playersOnline -= AdvancedVanishAPI.vanishedPlayers.size

            // Try to filter vanished players from the sample list
            // In MC 1.21+, this may fail due to NameAndId vs GameProfile conversion issues in ProtocolLib
            try {
                ping.setPlayers(ping.players.filter { AdvancedVanishAPI.vanishedPlayers.find { vUUID -> vUUID == it.uuid } == null })
            } catch (_: IllegalArgumentException) {
                // If we can't filter the player list due to ProtocolLib compatibility issues,
                // at least the player count will be correct (adjusted above)
                // This prevents the error spam while maintaining basic functionality
            }
        }
    }

    override fun getID() = "ServerList"

    override fun onEnable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this.packetListener)
    }

    override fun onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListener(this.packetListener)
    }
}