package com.hsu_mafia.motoo.api.domain.stock;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    
    // 키워드 검색 + 페이지네이션
    @Query("SELECT s FROM Stock s WHERE LOWER(s.stockName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.stockCode) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY s.stockCode")
    List<Stock> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 키워드 검색 + Slice
    @Query("SELECT s FROM Stock s WHERE LOWER(s.stockName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.stockCode) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY s.stockCode")
    Slice<Stock> findSliceByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 시장 타입별 검색 + 페이지네이션
    @Query("SELECT s FROM Stock s WHERE s.marketType = :marketType ORDER BY s.stockCode")
    List<Stock> findByMarketType(@Param("marketType") String marketType, Pageable pageable);
    
    // 시장 타입별 검색 + Slice
    @Query("SELECT s FROM Stock s WHERE s.marketType = :marketType ORDER BY s.stockCode")
    Slice<Stock> findSliceByMarketType(@Param("marketType") String marketType, Pageable pageable);
    
    // 산업별 검색 + 페이지네이션
    @Query("SELECT s FROM Stock s WHERE LOWER(s.industry.name) LIKE LOWER(CONCAT('%', :industryName, '%')) ORDER BY s.stockCode")
    List<Stock> findByIndustry(@Param("industryName") String industryName, Pageable pageable);
    
    // 산업별 검색 + Slice
    @Query("SELECT s FROM Stock s WHERE LOWER(s.industry.name) LIKE LOWER(CONCAT('%', :industryName, '%')) ORDER BY s.stockCode")
    Slice<Stock> findSliceByIndustry(@Param("industryName") String industryName, Pageable pageable);

    
    // 활성화된 종목만 조회
    @Query("SELECT s FROM Stock s WHERE s.isActive = true ORDER BY s.stockCode")
    List<Stock> findActiveStocks();
    
    // 시장 타입별 활성화된 종목 조회
    @Query("SELECT s FROM Stock s WHERE s.marketType = :marketType AND s.isActive = true ORDER BY s.stockCode")
    List<Stock> findActiveStocksByMarketType(@Param("marketType") String marketType);
    
    // 종목코드로 조회
    Optional<Stock> findByStockCode(String stockCode);
    
    // 종목코드 존재 여부 확인
    boolean existsByStockCode(String stockCode);
} 