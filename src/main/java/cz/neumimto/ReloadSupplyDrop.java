package cz.neumimto;

import cz.neumimto.configuration.ConfigMapper;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.concurrent.TimeUnit;

/**
 * Created by NeumimTo on 31.7.2017.
 */
@Singleton
public class ReloadSupplyDrop implements CommandExecutor {

    @Inject
    private SupplyDropsService supplyDropsService;

    @Inject
    private SupplyDrops plugin;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        SupplyDropsService.asyncJob.cancel();
        Sponge
                .getScheduler()
                .createTaskBuilder()
                .delay(3, TimeUnit.SECONDS) //wait in case async job would still run at this point
                .execute(() -> {
                    ConfigMapper.get("SupplyDrops").loadClass(Settings.class);
                    src.sendMessage(Text.builder("[SupplyDrops] Configuration reloaded.").build());
                    supplyDropsService.restart();
                }).submit(plugin);
        return CommandResult.success();
    }
}
