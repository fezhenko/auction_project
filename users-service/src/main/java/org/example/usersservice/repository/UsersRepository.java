package org.example.usersservice.repository;

import org.example.usersservice.model.AppUser;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends Repository<AppUser, Long> {
    @Query("SELECT * FROM users")
    List<AppUser> findUsers();

    @Query("SELECT email, role, balance, created_at " +
            "FROM users " +
            "WHERE user_id = :userId;")
    AppUser getAppUserById(@Param("userId") Long userId);

    @Modifying
    @Query("INSERT INTO users (email, password, role) " +
            "VALUES (:email, :password, :role);")
    //TODO: сделать возможность добавления админов только для админов
    AppUser createUser(@Param("email") String email,
                    @Param("password") String password,
                    @Param("role") String role);

    @Modifying
    @Query("UPDATE users " +
            "SET firstname = :firstname, " +
            "    lastname  = :lastname, " +
            "    phone_number = :phoneNumber " +
            "WHERE user_id = :userId;")
    AppUser updateNonMandatoryFieldsById(@Param("userId") Long userId,
                                         @Param("firstname") String firstname,
                                         @Param("lastname") String lastname,
                                         @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query("UPDATE users u " +
            "SET u.email = :email " +
            "WHERE user_id = :userId;")
    AppUser updateUserEmailById(@Param("userId") Long userId,
                                @Param("email") String email);

    @Modifying
    @Query("UPDATE users u " +
            "SET u.password = :password " +
            "WHERE user_id = :userId;")
    AppUser updateUserPasswordById(@Param("userId") Long userId,
                                   @Param("password") String password);

    @Modifying
    @Query("DELETE " +
            "FROM users u " +
            "WHERE u.user_id = :userId")
    void deleteAppUserById(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM users u " +
            "WHERE u.email = :userEmail;")
    boolean validateUser(@Param("userEmail") String userEmail);
}
