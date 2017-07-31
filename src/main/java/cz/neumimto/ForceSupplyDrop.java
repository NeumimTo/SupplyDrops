package cz.neumimto;

import com.flowpowered.math.vector.Vector3i;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

/**
 * Created by NeumimTo on 31.7.2017.
 */
@Singleton
public class ForceSupplyDrop implements CommandExecutor {

    @Inject
    SupplyDrops plugin;

    @Inject
    private SupplyDropsService supplyDropsService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;

        if (args.hasAny("t")) {
            BlockRay<World> blockRay = BlockRay
                    .from(player)
                    .skipFilter(
                            BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 20)
                    )
                    .build();
            while (blockRay.hasNext()) {
                BlockRayHit<World> next = blockRay.next();
                Vector3i blockPosition = next.getBlockPosition();
                World w = player.getWorld();
                BlockState block = player.getLocation().getExtent().getBlock(blockPosition);
                if (block.getType() == Sponge.getRegistry().getType(BlockType.class, Settings.container).get()) {
                    if (args.hasAny("r")) {
                        Sponge.getScheduler().createTaskBuilder()
                                .async()
                                .execute(() -> {
                                        createRandomContent(blockPosition, w);
                                    }
                                )
                                .submit(plugin);
                    } else {
                        Location l = new Location(player.getWorld(), blockPosition);
                        Location broadcastLoc = supplyDropsService.randomizeLocation(l);
                        plugin.boradcast(broadcastLoc);
                    }
                    break;
                }
            }
        }
        return CommandResult.success();
    }

    private void createRandomContent(Vector3i blockPosition, World e) {
        List<SupplyDrop> randomDrops = supplyDropsService.findRandomDrops();
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            supplyDropsService.createChest(new Location<>(e, blockPosition), randomDrops);
            plugin.boradcast(supplyDropsService.randomizeLocation(new Location<>(e, blockPosition)));
        }).submit(plugin);
    }

}
