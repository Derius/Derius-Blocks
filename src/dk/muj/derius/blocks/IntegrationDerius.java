package dk.muj.derius.blocks;

import com.massivecraft.massivecore.integration.IntegrationAbstract;

public class IntegrationDerius extends IntegrationAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static IntegrationDerius i = new IntegrationDerius();
	public static IntegrationDerius get() { return i; }
	private IntegrationDerius() { super(INTEGRATION_PLUGIN_NAME); }
	
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final String INTEGRATION_PLUGIN_NAME = "Derius-Core";
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private boolean activated = false;
	public boolean isActivated() { return activated; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void activate()
	{
		BlockMixinBlocks.inject();
		this.activated = true;
	}
	
	@Override
	public void deactivate()
	{
		this.activated = false;
	}
	
}
