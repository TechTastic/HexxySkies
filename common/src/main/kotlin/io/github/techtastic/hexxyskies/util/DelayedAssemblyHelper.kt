package io.github.techtastic.hexxyskies.util

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import org.valkyrienskies.core.util.pollUntilEmpty
import org.valkyrienskies.mod.common.assembly.ShipAssembler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

object DelayedAssemblyHelper {
    private val toAssemble = ConcurrentHashMap<ResourceKey<Level>, ConcurrentLinkedQueue<Set<BlockPos>>>()

    fun addNew(level: ServerLevel, positions: Set<BlockPos>) {
        this.toAssemble.getOrPut(level.dimension(), ::ConcurrentLinkedQueue).add(positions)
    }

    fun onTick(level: ServerLevel) {
        this.toAssemble[level.dimension()]?.pollUntilEmpty {
            ShipAssembler.assembleToShip(level, it)
        }
    }
}