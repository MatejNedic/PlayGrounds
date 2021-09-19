package com.mvp.mvp.repository;

import com.mvp.mvp.model.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("select r from Role  r where r.name in (:name)")
    List<Role> findAllByName(Iterable<String> name);
}
