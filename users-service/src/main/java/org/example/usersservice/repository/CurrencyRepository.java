package org.example.usersservice.repository;

import org.example.usersservice.model.Currency;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurrencyRepository extends Repository<Currency, Long> {


    @Query("insert into currencies (currency) " +
            "values (:currency::json);")
    @Modifying
    void addCurrency(@Param("currency") String currency);

    @Query("select * " +
            "from currencies")
    List<Currency> getAllCurrencies();

    @Query("select id, currency " +
            "from currencies " +
            "where id = :id;")
    Currency getCurrencyById(@Param("id") Long currencyId);

    @Query("UPDATE currencies " +
            "SET currency = :updatedJson::json " +
            "WHERE id = :id;")
    @Modifying
    void updateCurrency(@Param("id") Long currencyId, @Param("updatedJson") String updatedJson);

    @Query("delete from currencies where id = :id")
    @Modifying
    void deleteCurrency(@Param("id") Long currencyId);
}
