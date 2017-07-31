package cz.neumimto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 30.7.2017.
 */
public class SupplyDropsContainer {
	List<SupplyDrop> supplyDrops = new ArrayList<SupplyDrop>();

	public List<SupplyDrop> getSupplyDrops() {
		return supplyDrops;
	}

	public void setSupplyDrops(List<SupplyDrop> supplyDrops) {
		this.supplyDrops = supplyDrops;
	}
}
