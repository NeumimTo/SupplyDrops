package cz.neumimto;

import com.google.inject.Inject;
import cz.neumimto.configuration.ConfigMapper;
import cz.neumimto.core.ioc.IoC;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

@Plugin(
		id = "supplydrops",
		name = "SupplyDrops",
		version = "1.0"
)
public class SupplyDrops {

	@Inject
	private Logger logger;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;

	public AtomicLong lastTimeRun;

	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		Path path = Paths.get(privateConfigDir.toString(), "lastTimeRun");
		if (path.toFile().exists()) {
			try {
				lastTimeRun = new AtomicLong(Long.parseLong(new String(Files.readAllBytes(path))));
			} catch (Exception e) {
				lastTimeRun = new AtomicLong(0);
			}
		} else {
			lastTimeRun = new AtomicLong(0);
		}

		IoC.get().register(this);
		IoC.get().build(SupplyDropsService.class);
		ConfigMapper.init("SupplyDrops", privateConfigDir);
		ConfigMapper.get("SupplyDrops").loadClass(Settings.class);


		SimpleDispatcher rootCommand = new SimpleDispatcher();


		CommandSpec spec = CommandSpec.builder()
				.description(Text.of("Forces supplydrop at current location"))
				.permission("supplydrops.admin")
				.executor(IoC.get().build(ForceSupplyDrop.class))
				.arguments(
						GenericArguments.onlyOne(
								GenericArguments.flags()
								.flag("r")
								.flag("t")
								.buildWith(GenericArguments.none())
						))
				.build();
		rootCommand.register(spec, "force");

		spec = CommandSpec.builder()
				.description(Text.of("Reloads supplydrop configuration"))
				.permission("supplydrops.admin")
				.executor(IoC.get().build(ReloadSupplyDrop.class))
				.build();
		rootCommand.register(spec, "reload", "r");

		Sponge.getCommandManager().register(this, rootCommand, "supplydrops", "sd");

		IoC.get().postProcess();

	}

	public void updateLastTimeRun() {
		lastTimeRun.set(System.currentTimeMillis());
		Sponge.getScheduler().createAsyncExecutor(this).execute(new WriteToFile(lastTimeRun.get(), Paths.get(privateConfigDir.toString(), "lastTimeRun")));
	}



	void boradcast(Location location) {
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			player.sendMessage(ChatTypes.ACTION_BAR, Text.builder(Settings.dropMessage).style(TextStyles.BOLD).color(TextColors.RED).append(locationToString(location)).build());
		}
	}

	Text locationToString(Location location) {
		return Text.builder("[" +location.getBlockX() + "," + location.getBlockZ() + "]").style(TextStyles.ITALIC).color(TextColors.GOLD).build();
	}

}
