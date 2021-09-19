package com.mvp.mvp.repository;

import com.mvp.mvp.model.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Modifying(clearAutomatically = true)
    @Query("update User us set us.deposit = us.deposit - :deposit where us.id = :id")
    void subtractDepositFromUserById(@Param("deposit") Long deposit, @Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("update User us set us.password = :password  where us.id = :id")
    void changePasswordById(@Param("id") Long id, @Param("password") String password);

    @Modifying(clearAutomatically = true)
    @Query("update User us set us.deposit = us.deposit + :deposit where us.id = :id")
    void addDepositToUserById(@Param("deposit") Long deposit, @Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("update User us set us.deposit = 0 where us.id = :id")
    void resetDeposit(@Param("id") Long id);
}
