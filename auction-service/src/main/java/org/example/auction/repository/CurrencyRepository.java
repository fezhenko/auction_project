package org.example.auction.repository;

import org.example.auction.model.Currency;
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
}
