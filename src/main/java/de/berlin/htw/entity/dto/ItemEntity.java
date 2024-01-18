package de.berlin.htw.entity.dto;

import jakarta.persistence.*;

/**
 * @author Simone Krügel [simone.kruegel@student.htw-berlin.de]
 */
@Entity
@Table(name = "ITEM")
public class ItemEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    private OrderEntity order;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "COUNT")
    private Integer count;

    @Column(name = "PRICE")
    private Float price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

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

    public void setCount(Integer count) {
        this.count = (count == null) ? 0 : count;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof ItemEntity) {
            if (((ItemEntity) other).getId() == getId()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return getId().toString();
    }

}
