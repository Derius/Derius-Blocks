package dk.muj.derius.blocks.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;

import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.xlib.gson.annotations.SerializedName;

import dk.muj.derius.blocks.DeriusBlocks;

/**
 * This class contains information about
 * blocks placed by players in a single chunk.
 */
public class MChunk extends Entity<MChunk>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static MChunk get(Object oid)
	{
		return MChunkColl.get().get(oid);
	}
	
	public static MChunk get(Object oid, boolean creative)
	{
		return MChunkColl.get().get(oid, creative);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public MChunk load(MChunk that)
	{
		if (that == null || that == this) return that;
		this.setBlocks(that.getBlocks());
		this.setLastActive(that.getLastActive());
		
		return this;
	}
	
	@Override
	public boolean isDefault()
	{
		return this.getBlocks() == null || this.getBlocks().isEmpty();
	}
	
	@Override
	public void changed()
	{
		this.activeNow();
		super.changed();
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	@SerializedName("b")
	private Set<PS> blocks = new HashSet<>();
	public Set<PS> getBlocks() { return this.blocks; }
	public void setBlocks(Set<PS> blocks) { this.blocks = blocks; this.changed();}
	
	@SerializedName("l")
	private long lastActive = System.currentTimeMillis();
	public long getLastActive() { return this.lastActive; }
	public void setLastActive(long lastActive) { this.lastActive = lastActive; this.changed(); }
	private void activeNow() { this.lastActive = System.currentTimeMillis(); }
	
	private transient String world = null;
	public String getWorld()
	{
		if (this.world == null)
		{
			String id = this.getId();
			// 3 is the length of "w: "
			int start = id.indexOf("w: ") + 3;
			int end = id.indexOf(',');
			world = id.substring(start, end);
		}
		
		return this.world;
	}
	
	// -------------------------------------------- //
	// BLOCK GETTERS & SETTERS
	// -------------------------------------------- //
	
	public boolean addBlock(final PS ps)
	{
		final PS block = ps.getBlockCoords(true);
		if ( ! DeriusBlocks.getChunkId(ps).equals(this.getId())) return false;
		
		boolean ret = this.getBlocks().add(block);
		if (ret) this.changed();
	
		return ret;
	}
	
	public boolean removeBlock(final PS ps)
	{
		final PS block = ps.getBlockCoords(true);
		if ( ! DeriusBlocks.getChunkId(ps).equals(this.getId())) return false;
		
		boolean ret = this.getBlocks().remove(block);
		if (ret) this.changed();
		
		return ret;
	}
	
	// -------------------------------------------- //
	// CONVENIENCE
	// -------------------------------------------- //
	
	private boolean isNotNull(Object obj) { return obj != null; }
	
	/**
	 * This method cleans up and removes
	 * values which should not be present here.
	 * Hopefully this doen't do anything,
	 * but our system can't be perfect.
	 */
	public synchronized void clean()
	{
		Set<PS> result =  this.getBlocks().stream()
				
				// Extra super duper fancy null check.
				.filter(this::isNotNull)
				.map(ps -> ps.withWorld(this.getWorld()))
				// They must be inside this chunk.
				.filter(ps -> DeriusBlocks.getChunkId(ps).equals(this.getId()))
				
				// Turn to bukkit block for doing some extra checking.
				// If that fails (AKA: It didn't have the proper values) we certainly don't want this value.
				.map( ps -> { try{ return ps.asBukkitBlock(); } catch(IllegalStateException ex){ return null; } })
				
				// If the above failed null was returned.
				.filter(this::isNotNull)
				
				// If the block is air, we don't want it.
				.filter(b -> b.getType() != null && b.getType() != Material.AIR)
				
				// We don't want the below commented out behaviour. Something might 
				// just be temporarily disabled. But we still want to keep it tracked as 'placed'.
				//.filter(b -> DeriusBlocks.LISTEN_FOR.contains(b.getType()))
				
				// We still want it as a PS.
				.map(PS::valueOf)
				.collect(Collectors.toSet());
		this.setBlocks(result);
	}
	
	public boolean tryDetach()
	{
		if ( ! (this.isDefault() || this.isOld()) ) return false;
		this.detach();
		this.blocks = null;
		
		return true;
	}

	public boolean isOld()
	{
		long oldWhen = this.getLastActive() + TimeUnit.MILLIS_PER_DAY * MConf.get().daysWithNoActivityBeforeAChunkIsConsideredOld;
		return System.currentTimeMillis() >= oldWhen;
	}
	
}
