package dk.muj.derius.blocks.entity;

import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;

import dk.muj.derius.blocks.Const;
import dk.muj.derius.blocks.DeriusBlocks;

public class MConfColl extends Coll<MConf>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MConfColl i = new MConfColl();
	public static MConfColl get() { return i; }
	private MConfColl()
	{
		super(Const.COLLECTION_MCONF, MConf.class, MStore.getDb(), DeriusBlocks.get());
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
	
	// This initializes the saving task of the class.
	@Override
	public void init()
	{
		super.init();
		MConf.i = this.get("BlocksSettings", true);
	}
	
}
