package org.example.usersservice.repository;

import org.example.usersservice.model.Payment;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface PaymentsRepository extends Repository<Payment, Long> {


    @Query("select * from payments")
    List<Payment> findAllPayments();

    @Query("select * from payments " +
            "where payment_id = :paymentId;")
    Payment findPaymentsByPaymentId(
            @Param("paymentId") Long paymentId
    );

    @Transactional(rollbackFor = {SQLException.class})
    @Modifying
    @Query("insert into payments(user_id, card_number, expiration_date, amount, payment_date) " +
            "values (:userId, :cardNumber, :expirationDate, :amount, now());")
    void addPaymentByUserId(@Param("userId") Long userId,
                            @Param("cardNumber") String cardNumber,
                            @Param("expirationDate") String expirationDate,
                            @Param("amount") Double amount
    );

    @Modifying
    @Query("update payments " +
            "set card_number = :cardNumber, expiration_date = :expirationDate, payment_date = now() " +
            "where user_id = :userId;")
    void updatePaymentInformationByUserId(@Param("userId") Long userId,
                                          @Param("cardNumber") String cardNumber,
                                          @Param("expirationDate") String expirationDate);

}
