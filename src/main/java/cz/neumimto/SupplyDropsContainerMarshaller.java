package cz.neumimto;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import cz.neumimto.configuration.MarshallerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import java.util.*;

/**
 * Created by NeumimTo on 30.7.2017.
 */
public class SupplyDropsContainerMarshaller extends MarshallerImpl {

	@Override
	public Object unmarshall(Config c) {
		SupplyDropsContainer supplyDropsContainer = new SupplyDropsContainer();
		ConfigObject co = c.root();
		Set<Map.Entry<String, ConfigValue>> entries = co.entrySet();
		ConfigValue value = entries.stream().findFirst().get().getValue();
		ConfigList list = (ConfigList) value;
		List unwrapped = list.unwrapped();
		for (Object configValue : unwrapped) {
			HashMap<String, String> e = (HashMap<String, String>) configValue;
			SupplyDrop supplyDrop = new SupplyDrop();
			for (Map.Entry<String, String> entry : e.entrySet()) {

				switch (entry.getKey().toLowerCase()) {
					case "item_type":
						Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, entry.getValue());
						if (!type.isPresent()) {
							System.err.println(entry.getValue() + " - Unknown Type");
						} else {
							supplyDrop.setItemType(type.get());
						}
						break;
					case "rolls":
						supplyDrop.setRolls(Integer.parseInt(entry.getValue()));
						break;
					case "chance":
						supplyDrop.setChance(Double.parseDouble(entry.getValue()));
						break;
					case "tier":
						supplyDrop.setTier(Integer.parseInt(entry.getValue()));
						break;
					case "quantity":
						supplyDrop.setQuantity(Integer.parseInt(entry.getValue()));
						break;
				}

			}
			System.out.println(supplyDrop);
			supplyDropsContainer.getSupplyDrops().add(supplyDrop);
		}

		return supplyDropsContainer;
	}

	@Override
	public String marshall(Object o) {
		return "{list:[]}";
	}
}
