package de.berlin.htw.boundary.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
public class Item {

	@NotNull
	@Length(max=255)
    private String productName;

	@NotNull
	@Pattern(regexp = "^[0-9]-[0-9]-[0-9]-[0-9]-[0-9]-[0-9]$")
    private String productId;

    private Integer count;

	@NotNull
	@Range(min=10, max=100)
    private Float price;

    public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }
    
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

}
