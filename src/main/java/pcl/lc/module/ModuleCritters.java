package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

import cpw.mods.fml.common.registry.EntityRegistry;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.entity.EntityTokra;
import pcl.lc.module.ModuleManager.Module;
import pcl.lc.worldgen.TradeHandler;

public class ModuleCritters implements IModule {

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return null;
	}

	@Override
	public void preInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		EntityRegistry.registerModEntity(EntityTokra.class, "tokra", 0, LanteaCraft.getInstance(), 80, 1, true);

		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft Tokra villagers...");
		LanteaCraft.getProxy().tokraVillagerID = LanteaCraft.getProxy().addVillager(
				LanteaCraft.getProxy().getConfig().getVillager("tokra"), "tokra", LanteaCraft.getResource("textures/skins/tokra.png"));
		RegistrationHelper.addTradeHandler(LanteaCraft.getProxy().tokraVillagerID, new TradeHandler());
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
