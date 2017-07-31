package cz.neumimto;

import cz.neumimto.configuration.ConfigValue;
import cz.neumimto.configuration.ConfigurationContainer;

/**
 * Created by NeumimTo on 30.7.2017.
 */
@ConfigurationContainer(filename = "Settings.conf", path = "{WorkingDir}")
public class Settings {

	@ConfigValue(name = "container_id")
	public static String container = "minecraft:chest";

	@ConfigValue(name = "supply_drops", as = SupplyDropsContainerMarshaller.class)
	public static SupplyDropsContainer supplyDropsContainer = new SupplyDropsContainer();

	@ConfigValue(name = "broadcast_message")
	public static String dropMessage = "Supply drop called at the location: ";

	@ConfigValue(name = "natural_interval")
	public static long interval = 30*60*1000;

	@ConfigValue(name = "min_players_required")
	public static int playerRequired = 3;

	@ConfigValue(name = "world")
	public static String world = "Overworld";

	@ConfigValue(name = "randomSpillDiameter")
	public static int randomSpill = 150;
}
