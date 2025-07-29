package com.hsu_mafia.motoo.api.domain.stockprice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceDailyRepository extends JpaRepository<StockPriceDaily, StockPriceDailyId> {
    
    @Query("SELECT sp FROM StockPriceDaily sp WHERE sp.stockCode = :stockCode ORDER BY sp.date DESC")
    List<StockPriceDaily> findByStockCodeOrderByDateDesc(@Param("stockCode") String stockCode);
    
    @Query("SELECT sp FROM StockPriceDaily sp WHERE sp.stockCode = :stockCode AND sp.date BETWEEN :startDate AND :endDate ORDER BY sp.date")
    List<StockPriceDaily> findByStockCodeAndDateBetween(@Param("stockCode") String stockCode, 
                                                       @Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT sp FROM StockPriceDaily sp WHERE sp.stockCode = :stockCode AND sp.date = :date")
    Optional<StockPriceDaily> findByStockCodeAndDate(@Param("stockCode") String stockCode, 
                                                    @Param("date") LocalDate date);
    
    @Query("SELECT sp FROM StockPriceDaily sp WHERE sp.date BETWEEN :startDate AND :endDate ORDER BY sp.stockCode, sp.date")
    List<StockPriceDaily> findByDateBetween(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
} 