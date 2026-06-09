package com.jesus_dev.arena_hub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jesus_dev.arena_hub.model.enums.RoleName;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity() @Table(name = "roles")
public class Role {

    // Attributes
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    // Relationships
    @ManyToMany(mappedBy = "roles") @JsonIgnore
    private Set<User> users = new HashSet<>();

    // Constructors
    public Role() {
    }

    public Role(UUID id, RoleName name, Set<User> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
