package com.manthan.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
public class User extends BaseModel {
    private String email;
    private String password;

    @ManyToMany
    // setting it to new hashset to that we dont have to deal with NullPointerExceptions
    // because String fields above have default values -- but default value of Set<> is null -- thus initializing it to empty HashSet<>
    private Set<Role> roles = new HashSet<>();
}
