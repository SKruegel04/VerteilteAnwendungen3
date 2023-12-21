package de.berlin.htw.entity.dto;

import java.util.List;

import jakarta.persistence.*;

/**
 * @author Simone Krügel [simone.kruegel@student.htw-berlin.de]
 */
@Entity
@Table(name = "`ORDER`")
public class OrderEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<ItemEntity> items;

    @Column(name = "TOTAL")
    private Float total;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        for (ItemEntity item : items) {
            item.setOrder(this);
        }
        this.items = items;
    }

    public void addItem(ItemEntity item) {
        item.setOrder(this);
        this.items.add(item);
    }

    public void removeItem(ItemEntity item) {
        item.setOrder(null);
        this.items.remove(item);
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
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
        if (other instanceof OrderEntity) {
            if (((OrderEntity) other).getId() == getId()) {
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
