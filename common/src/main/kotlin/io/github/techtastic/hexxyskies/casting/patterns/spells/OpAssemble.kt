package io.github.techtastic.hexxyskies.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import io.github.techtastic.hexxyskies.util.DelayedAssemblyHelper
import io.github.techtastic.hexxyskies.util.OperatorUtils.getListOfVecs
import net.minecraft.core.BlockPos
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

object OpAssemble : SpellAction {
    override val argc: Int
        get() = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val positions = args.getListOfVecs(0, argc).map {
            env.assertVecInRange(it)
            BlockPos.containing(it)
        }.filterNot { env.world.getBlockState(it).`is`(ValkyrienSkiesMod.ASSEMBLE_BLACKLIST) }.toSet()

        if (positions.isEmpty())
            throw MishapInvalidIota.ofType(args[0], 0, "list.vec.empty")

        return SpellAction.Result(
            Spell(positions),
            MediaConstants.SHARD_UNIT * positions.size,
            positions.map { ParticleSpray.cloud(it.center, 1.2) }
        )
    }

    private data class Spell(val positions: Set<BlockPos>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            DelayedAssemblyHelper.addNew(env.world, positions)
        }
    }
}