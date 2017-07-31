package cz.neumimto;

import com.flowpowered.math.vector.Vector3i;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by NeumimTo on 30.7.2017.
 */
@Singleton
public class SupplyDropsService {

	@Inject
	private SupplyDrops plugin;

	public static Task asyncJob;

	public void schedule() {
		if (plugin.lastTimeRun.get() < System.currentTimeMillis() - Settings.interval) {
			plugin.updateLastTimeRun();
			List<SupplyDrop> toSpawn = findRandomDrops();
			if (!toSpawn.isEmpty()) {
				Sponge.getScheduler().createTaskBuilder().execute(() -> createChest(toSpawn)).submit(plugin);
			}
		}
	}

	public List<SupplyDrop> findRandomDrops() {
		Random random = new Random();
		List<SupplyDrop> toSpawn = new ArrayList<>();
		for (SupplyDrop supplyDrop : Settings.supplyDropsContainer.getSupplyDrops()) {
			int rolls = supplyDrop.getRolls();
			while (rolls > 0) {
				int i = random.nextInt(10000) / 100;
				if (i <= supplyDrop.getChance()) {
					toSpawn.add(supplyDrop);
				}
				rolls--;
			}
		}
		return toSpawn;
	}

	public void createChest(List<SupplyDrop> drops) {
		Collection<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers();
		if (onlinePlayers.size() < Settings.playerRequired) {
			return;
		}
		Location location = getMeanLocation(onlinePlayers);
		plugin.boradcast(location);
		Location realLoc = randomizeLocation(location).add(0,1,0);
		createChest(realLoc, drops);
	}

	public void createChest(Location<World> location, List<SupplyDrop> content) {
		BlockType type = Sponge.getRegistry().getType(BlockType.class, Settings.container).get();
		location.setBlockType(type, Cause.of(NamedCause.of("sd", Sponge.getPluginManager().fromInstance(plugin).get())));
		Optional<TileEntity> tileEntity = location.getTileEntity();
		TileEntity tileEntity1 = tileEntity.get();
		Chest c = (Chest) tileEntity1;
		for (SupplyDrop drop : content) {
			c.getInventory().offer(toItemStack(drop));
		}
	}

	public Location randomizeLocation(Location meanLocation) {
		int i = Settings.randomSpill / 2;
		Random random = new Random();
		int x = random.nextInt(Settings.randomSpill) - i;
		int z = random.nextInt(Settings.randomSpill) - i;
		Extent w = meanLocation.getExtent();
		Vector3i vec = meanLocation.getPosition().toInt().add(x, 0, z);
		return new Location<>(meanLocation.getExtent(), w.getHighestPositionAt(vec));
	}

	private Location getMeanLocation(Collection<Player> onlinePlayers) {
		int z = 0;
		int x = 0;
		for (Player onlinePlayer : onlinePlayers) {
			z += onlinePlayer.getLocation().getBlockZ();
			x += onlinePlayer.getLocation().getBlockX();
		}
		z /= onlinePlayers.size();
		x /= onlinePlayers.size();
		World w = Sponge.getServer().getWorld(Settings.world).get();
		return new Location<>(w, x, 0, z);
	}

	@PostProcess
	public void restart() {
		if (asyncJob != null)
			asyncJob.cancel();
		asyncJob = Sponge.getScheduler()
				.createTaskBuilder()
				.async()
				.delay(10, TimeUnit.SECONDS)
				.interval(1, TimeUnit.SECONDS)
				.execute(this::schedule)
				.submit(plugin);
	}

	public ItemStack toItemStack(SupplyDrop drop) {
		return ItemStack.of(drop.getItemType(), drop.getQuantity());
	}
}
