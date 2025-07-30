package com.safeview.domain.decryption.repository;

import com.safeview.domain.decryption.entity.BlockchainTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockchainTransactionRepository extends JpaRepository<BlockchainTransaction, Long> {

    // 트랜잭션 해시로 조회
    Optional<BlockchainTransaction> findByTxHash(String txHash);
    
    // 상태별 트랜잭션 조회
    List<BlockchainTransaction> findByStatus(String status);
    
    // 타입별 트랜잭션 조회
    List<BlockchainTransaction> findByTxType(String txType);
    
    // 상태와 타입별 트랜잭션 조회
    List<BlockchainTransaction> findByStatusAndTxType(String status, String txType);
    
    // 블록 번호별 트랜잭션 조회
    List<BlockchainTransaction> findByBlockNumber(Long blockNumber);
    
    // 주소별 트랜잭션 조회 (발신자)
    List<BlockchainTransaction> findByFromAddress(String fromAddress);
    
    // 주소별 트랜잭션 조회 (수신자)
    List<BlockchainTransaction> findByToAddress(String toAddress);
    
    // 주소별 트랜잭션 조회 (발신자 또는 수신자)
    @Query("SELECT bt FROM BlockchainTransaction bt WHERE bt.fromAddress = :address OR bt.toAddress = :address")
    List<BlockchainTransaction> findByAddress(@Param("address") String address);
    
    // 대기 중인 트랜잭션 조회
    @Query("SELECT bt FROM BlockchainTransaction bt WHERE bt.status = 'PENDING' ORDER BY bt.createdAt ASC")
    List<BlockchainTransaction> findPendingTransactions();
    
    // 실패한 트랜잭션 조회
    @Query("SELECT bt FROM BlockchainTransaction bt WHERE bt.status = 'FAILED' ORDER BY bt.createdAt DESC")
    List<BlockchainTransaction> findFailedTransactions();
    
    // 트랜잭션 페이징 조회
    Page<BlockchainTransaction> findByStatus(String status, Pageable pageable);
    
    // 트랜잭션 해시 존재 여부 확인
    boolean existsByTxHash(String txHash);
    
    // 블록 번호 존재 여부 확인
    boolean existsByBlockNumber(Long blockNumber);
} 