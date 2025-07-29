package com.hsu_mafia.motoo.api.domain.stockprice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceHourRepository extends JpaRepository<StockPriceHour, StockPriceHourId> {
    
    @Query("SELECT sp FROM StockPriceHour sp WHERE sp.stockCode = :stockCode ORDER BY sp.timestamp DESC")
    List<StockPriceHour> findByStockCodeOrderByTimestampDesc(@Param("stockCode") String stockCode);
    
    @Query("SELECT sp FROM StockPriceHour sp WHERE sp.stockCode = :stockCode AND sp.timestamp BETWEEN :startTime AND :endTime ORDER BY sp.timestamp")
    List<StockPriceHour> findByStockCodeAndTimestampBetween(@Param("stockCode") String stockCode, 
                                                           @Param("startTime") LocalDateTime startTime, 
                                                           @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT sp FROM StockPriceHour sp WHERE sp.stockCode = :stockCode AND sp.timestamp = :timestamp")
    Optional<StockPriceHour> findByStockCodeAndTimestamp(@Param("stockCode") String stockCode, 
                                                        @Param("timestamp") LocalDateTime timestamp);
    
    @Query("SELECT sp FROM StockPriceHour sp WHERE sp.timestamp BETWEEN :startTime AND :endTime ORDER BY sp.stockCode, sp.timestamp")
    List<StockPriceHour> findByTimestampBetween(@Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
} 