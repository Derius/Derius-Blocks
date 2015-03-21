package dk.muj.derius.blocks;

import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.util.Txt;

import dk.muj.derius.blocks.entity.MChunk;
import dk.muj.derius.blocks.entity.MChunkColl;
import dk.muj.derius.blocks.entity.MConf;

/**
 * This class logs block placed/broken/otherwise modified.
 * It does also cleanup the database system for old files.
 * A chunk is considered old when i haven't been modified in more than 20 days.
 */
public class EngineBlocks extends EngineAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineBlocks i = new EngineBlocks();
	public static EngineBlocks get() { return i; }
	private EngineBlocks() {}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return DeriusBlocks.get();
	}

	@Override
	public void run()
	{
		cleanWorlds();
	}
	
	@Override
	public boolean isSync()
	{
		//  Our task only modifies MStore and MStore is thread safe :)
		return false;
	}
	
	@Override
	public Long getPeriod()
	{
		// Every 6 hours we perform a cleanup
		// It is also done on startup
		//     1Sec 1min 1hour 6hours
		return 20 * 60 * 60 * 6L;
	}
	
	// -------------------------------------------- //
	// WORLDS
	// -------------------------------------------- //
	
	public void cleanWorlds()
	{
		long start = System.currentTimeMillis();
		DeriusBlocks.get().log(Level.INFO, "Begins chunk cleanup ");
		
		if (MConf.get().doExtraChunkCleanup)
		{
			MChunkColl.get().getAll().forEach(MChunk::clean);
		}
		
		MChunkColl.get().getAll().forEach(MChunk::tryDetach);
		
		long end = System.currentTimeMillis();
		DeriusBlocks.get().log(Level.INFO, Txt.parse("Finished chunk cleanup took <h>" + String.valueOf(end - start) + " millis"));
		
		return;
	}
	
	
	// -------------------------------------------- //
	// BLOCK EVENTS
	// -------------------------------------------- //
	
	// Actual logic.
	private void handlePlaced(final Block block)
	{
		MChunkColl.setBlockPlacedByPlayer(block, true);
	}
	
	private void handleBroken(final Block block)
	{
		Bukkit.getScheduler().runTask(DeriusBlocks.get(), () -> MChunkColl.setBlockPlacedByPlayer(block, false));
	}
	
	// Event shortcut
	private void handlePlaced(final BlockEvent event)
	{
		this.handlePlaced(event.getBlock());
	}
	
	private void handleBroken(final BlockEvent event)
	{
		this.handleBroken(event.getBlock());
	}

	// Listeners
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void setBroken(final BlockBreakEvent event)
	{	
		this.handleBroken(event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void setPlaced(final BlockPlaceEvent event)
	{	
		this.handlePlaced(event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void setBroken(final BlockFadeEvent event)
	{	
		this.handleBroken(event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void setBroken(final BlockBurnEvent event)
	{
		this.handleBroken(event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void setBroken(final EntityExplodeEvent event)
	{
		event.blockList().forEach(this::handleBroken);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void setBroken(final LeavesDecayEvent event)
	{
		this.handleBroken(event);
	}
	
	// Even if a player hasn't placed a block,
	// we mark it as such when modified by a piston.
	// This is to be 100% sure that cheating with pistons can't be done.
	// In most cases we won't log it anyways, since it
	// is not on the list if logged material types.
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPistonExtend(final BlockPistonExtendEvent event)
	{
		final Collection<Block> blocks = event.getBlocks();
		final BlockFace dir = event.getDirection();
		
		// We need two loops in case one block was pushed to a location
		// another block was already in.
		blocks.forEach(this::handleBroken);
		blocks.stream().map(b -> b.getRelative(dir)).forEach(this::handlePlaced);
		
		return;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPistonRetract(final BlockPistonRetractEvent event)
	{
		final Collection<Block> blocks = event.getBlocks();
		final BlockFace dir = event.getDirection();
		
		// We need two loops in case one block was pushed to the location
		// another block was in.
		blocks.forEach(this::handleBroken);
		blocks.stream().map(b -> b.getRelative(dir)).forEach(this::handlePlaced);

		return;
	}
	
}
