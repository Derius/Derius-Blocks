package dk.muj.derius.blocks.integration.derius;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Material;

import com.massivecraft.massivecore.ps.PS;

import dk.muj.derius.api.DeriusAPI;
import dk.muj.derius.api.mixin.BlockMixin;
import dk.muj.derius.blocks.DeriusBlocks;
import dk.muj.derius.blocks.entity.MChunk;
import dk.muj.derius.blocks.entity.MChunkColl;

public class BlockMixinBlocks implements BlockMixin
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static BlockMixinBlocks i = new BlockMixinBlocks();
	public static BlockMixinBlocks get() { return i; }
	public static void inject()
	{
		// Load values from previous mixin.
		DeriusBlocks.LISTEN_FOR.addAll(DeriusAPI.getBlocksTypesToListenFor());
		// Inject this.
		DeriusAPI.setBlockMixin(get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	// Placed
	@Override
	public boolean isBlockPlacedByPlayer(PS ps)
	{
		MChunk chunk = MChunkColl.get().get(ps, false);
		if (chunk == null) return false;
		
		return chunk.getBlocks().contains(ps.getBlockCoords());
	}
	
	// Listening
	public Set<Material> getBlocksTypesToListenFor() { return DeriusBlocks.LISTEN_FOR; }
	public void setBlocksTypesToListenFor(Collection<Material> blocks) { DeriusBlocks.LISTEN_FOR.clear(); DeriusBlocks.LISTEN_FOR.addAll(blocks); } 
	public void addBlockTypesToListenFor(Collection<Material> blocks) { DeriusBlocks.LISTEN_FOR.addAll(blocks); }
	
	public boolean isListenedFor(Material material)
	{
		if (material == null) throw new IllegalArgumentException("material was null");
		return this.getBlocksTypesToListenFor().contains(material);
	}

}
