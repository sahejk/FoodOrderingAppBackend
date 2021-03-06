package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "state",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries(
        {       @NamedQuery(name = "allStates", query = "select s from StateEntity s"),
                @NamedQuery(name = "stateByUuid",query="select s from StateEntity s where s.uuid=:uuid"),
                @NamedQuery(name = "stateById", query = "select s from StateEntity s where s.id=:id")
        }
)

public class StateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;


    @Column(name = "state_name")
    @Size(max = 30)
    private String stateName;

    public StateEntity(String stateUuid, String state) {
        this.uuid = stateUuid;
        this.stateName = state;
    }

    public StateEntity(Long i, String stateUuid, String state) {
        this.uuid = stateUuid;
        this.stateName = state;
        this.id = id;
    }

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

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}