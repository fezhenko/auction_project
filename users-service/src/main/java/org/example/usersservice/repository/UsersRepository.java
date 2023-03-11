package org.example.usersservice.repository;

import org.example.usersservice.model.AppUser;
import org.example.usersservice.model.Payment;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends Repository<AppUser, Long> {
    @Query("SELECT * FROM users")
    List<AppUser> findUsers();

    @Query("SELECT user_id, firstname, lastname, email, role, balance, phone_number, created_at " +
            "FROM users " +
            "WHERE user_id = :userId;")
    AppUser getAppUserById(@Param("userId") Long userId);

    @Modifying
    @Query("INSERT INTO users (email, password, role) " +
            "VALUES (:email, :password, :role);")
        //TODO: сделать возможность добавления админов только для админов
    void createUser(@Param("email") String email,
                    @Param("password") String password,
                    @Param("role") String role);

    @Modifying
    @Query("UPDATE users " +
            "SET email = :email, " +
            "    firstname = :firstname, " +
            "    lastname  = :lastname, " +
            "    phone_number = :phoneNumber " +
            "WHERE user_id = :userId;")
    void updateFieldsByUserId(@Param("userId") Long userId,
                              @Param("email") String email,
                              @Param("firstname") String firstname,
                              @Param("lastname") String lastname,
                              @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query("UPDATE users " +
            "SET password = :password " +
            "WHERE user_id = :userId;")
    void updateUserPasswordById(@Param("userId") Long userId,
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

    @Query("select payment_id, amount, payment_date " +
            "from payments p " +
            "         join users u on u.user_id = p.user_id " +
            "where u.user_id = :userId;")
    List<Payment> findPaymentsByUserId(
            @Param("userId") Long userId
    );

    @Query("update payments as p " +
            "set card_number     = :cardNum, " +
            "    expiration_date = :expDate " +
            "from users as u " +
            "where payment_id = :paymentId " +
            "  and u.user_id = :userId;")
    Payment updatePaymentInformationByUserId(Long userId, Long paymentId, String cardNumber, String expirationDate);
}
