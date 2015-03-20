package dk.muj.derius.blocks.entity;

import com.massivecraft.massivecore.store.Entity;

public class MConf extends Entity<MConf>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	protected static transient MConf i;
	public static MConf get() { return i; }
	
	// -------------------------------------------- //
	// CONFIG
	// -------------------------------------------- //
	
	public int daysWithNoActivityBeforeAChunkIsConsideredOld = 20;
	
	public boolean doExtraChunkCleanup = false;
}
