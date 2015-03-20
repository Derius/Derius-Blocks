package dk.muj.derius.blocks;

import org.bukkit.block.Block;

import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;

public class MChunkColl extends Coll<MChunk>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MChunkColl i = new MChunkColl();
	public static MChunkColl get() { return i; }
	private MChunkColl()
	{
		super(Const.COLLECTION_CHUNKS, MChunk.class, MStore.getDb(), DeriusBlocks.get());
		this.setCreative(false);
	}
	
	// -------------------------------------------- //
	// STACK TRACEABILITY
	// -------------------------------------------- //
	
	@Override
	public void onTick()
	{
		super.onTick();
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public MChunk createNewInstance()
	{
		// This could be faster and has a lower chance
		// of errors, than the default reflection.
		// Because this logging is generally performance heavy, we need to optimise when possible.
		return new MChunk();
	}

	@Override
	public String fixId(Object oid)
	{
		if (oid instanceof String) return (String) oid;
		if (oid instanceof PS) return DeriusBlocks.getChunkId((PS) oid);
		if (oid instanceof MChunk) return ((MChunk) oid).getId();
		
		return null;
	}
	
	// -------------------------------------------- //
	// PLACED
	// -------------------------------------------- //
	
	public static void setBlockPlacedByPlayer(Block block, boolean isPlaced)
	{	
		final PS ps = PS.valueOf(block);
		
		if (isPlaced)
		{
			// We will always like to remove, but not always add.
			if ( ! DeriusBlocks.LISTEN_FOR.contains(block.getType())) return;
			// When adding we absolutely need a MChunk.
			MChunk mchunk = MChunk.get(ps, true);
			mchunk.addBlock(ps);
		}
		else
		{
			// When removing we don't always need a MChunk.
			MChunk mchunk = MChunk.get(ps, false);
			if (mchunk == null) return;
			mchunk.removeBlock(ps);
		}
		
		return;
	}

}
