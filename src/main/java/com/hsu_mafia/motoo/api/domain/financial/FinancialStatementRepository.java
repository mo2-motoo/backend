package com.hsu_mafia.motoo.api.domain.financial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialStatementRepository extends JpaRepository<FinancialStatement, Long> {
    
    /**
     * 특정 종목의 최신 재무제표 조회
     */
    Optional<FinancialStatement> findTopByStockOrderByReportDateDesc(String stockCode);
    
    /**
     * 특정 종목의 특정 날짜 이후 재무제표 조회
     */
    List<FinancialStatement> findByStockOrderByReportDateDesc(String stockCode, LocalDate afterDate);
    
    /**
     * 특정 종목의 특정 연도 재무제표 조회
     */
    @Query("SELECT fs FROM FinancialStatement fs WHERE fs.stock.stockCode = :stockCode AND YEAR(fs.reportDate) = :year ORDER BY fs.reportDate DESC")
    List<FinancialStatement> findByStockAndYear(@Param("stockCode") String stockCode, @Param("year") int year);
    
    /**
     * 특정 종목의 분기별 재무제표 조회
     */
    @Query("SELECT fs FROM FinancialStatement fs WHERE fs.stock.stockCode = :stockCode AND fs.reportType = 'QUARTERLY' ORDER BY fs.reportDate DESC")
    List<FinancialStatement> findQuarterlyByStock(@Param("stockCode") String stockCode);
    
    /**
     * 특정 종목의 연간 재무제표 조회
     */
    @Query("SELECT fs FROM FinancialStatement fs WHERE fs.stock.stockCode = :stockCode AND fs.reportType = 'ANNUAL' ORDER BY fs.reportDate DESC")
    List<FinancialStatement> findAnnualByStock(@Param("stockCode") String stockCode);
    
    /**
     * 특정 종목의 특정 날짜 재무제표 조회
     */
    Optional<FinancialStatement> findByStockAndReportDate(String stockCode, LocalDate reportDate);
    
    /**
     * 모든 종목의 최신 재무제표 조회
     */
    @Query("SELECT fs FROM FinancialStatement fs WHERE fs.id IN (SELECT MAX(fs2.id) FROM FinancialStatement fs2 GROUP BY fs2.stock.stockCode)")
    List<FinancialStatement> findLatestByAllStocks();
} 