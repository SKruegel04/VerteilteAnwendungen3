package de.berlin.htw.boundary.dto;

import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
public class Basket extends Order {

    private Float remainingBalance;

	@NotNull
	@Size(max=10)
	private List<Item> items = new ArrayList<>();

	public Float getRemainingBalance() {
		return remainingBalance;
	}

	public void setRemainingBalance(Float remainingBalance) {
		this.remainingBalance = remainingBalance;
	}

	@Override
	public List<Item> getItems() {
		return items;
	}

	@Override
	public void setItems(List<Item> items) {
		this.items = items;
	}

}
