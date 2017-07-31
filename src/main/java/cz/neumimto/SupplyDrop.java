package cz.neumimto;

import org.spongepowered.api.item.ItemType;

/**
 * Created by NeumimTo on 30.7.2017.
 */
public class SupplyDrop {
	private ItemType itemType;
	private int rolls;
	private double chance;
	private int tier;
	private int quantity;

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public int getRolls() {
		return rolls;
	}

	public void setRolls(int rolls) {
		this.rolls = rolls;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "SupplyDrop["+ itemType + ", " + chance +"%, "+ rolls + "x" + "]";
	}
}
