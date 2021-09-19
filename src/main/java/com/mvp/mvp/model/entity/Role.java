package com.mvp.mvp.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "role_vending")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }


    public static final class RoleBuilder {
        private Long id;
        private String name;

        private RoleBuilder() {
        }

        public static RoleBuilder aRole() {
            return new RoleBuilder();
        }

        public RoleBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public RoleBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Role build() {
            Role role = new Role();
            role.setId(id);
            role.setName(name);
            return role;
        }
    }
}
