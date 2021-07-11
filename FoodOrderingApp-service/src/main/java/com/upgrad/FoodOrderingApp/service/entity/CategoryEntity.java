package com.upgrad.FoodOrderingApp.service.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries(
        {
                @NamedQuery(name = "categoryByUuid", query = "select c from CategoryEntity c where c.uuid=:uuid"),
                @NamedQuery(name = "categoryById", query = "select c from CategoryEntity c where c.id=:id"),
                @NamedQuery(name = "allCategories", query = "select c from CategoryEntity c order by c.categoryName")
        }
)
public class CategoryEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "category_name")
    @Size(max = 255)
    private String categoryName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id", referencedColumnName="id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "item_id", referencedColumnName="id", nullable = false)
    )
    private List<ItemEntity> itemEntities =new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<ItemEntity> getItemEntities() {
        return itemEntities;
    }

    public void setItemEntities(List<ItemEntity> itemEntities) {
        this.itemEntities = itemEntities;
    }

}