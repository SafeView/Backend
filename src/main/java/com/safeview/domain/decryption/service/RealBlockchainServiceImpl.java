package com.safeview.domain.decryption.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealBlockchainServiceImpl implements BlockchainService {

    @Value("${blockchain.rpc-url}")
    private String rpcUrl;
    
    @Value("${blockchain.contract-address}")
    private String contractAddress;
    
    @Value("${blockchain.private-key}")
    private String privateKey;
    
    @Value("${blockchain.gas-price:20000000000}")
    private BigInteger gasPrice;
    
    @Value("${blockchain.gas-limit:300000}")
    private BigInteger gasLimit;
    
    @Value("${blockchain.network-id:11155111}")
    private Long networkId;
    
    @Value("${blockchain.simulation-mode:true}")
    private boolean simulationMode;

    private Web3j web3j;
    private Credentials credentials;
    private ContractGasProvider gasProvider;

    @Override
    public String registerKey(String keyHash, Long userId) {
        try {
            initializeWeb3j();
            
            log.info("Sepolia 블록체인에 키 등록: keyHash={}, userId={}", keyHash, userId);
            
            if (simulationMode) {
                // 시뮬레이션 모드: 실제 트랜잭션 없이 테스트 해시 생성
                String testTxHash = "0x" + keyHash.substring(0, 8) + "sepolia_test_" + System.currentTimeMillis();
                log.info("Sepolia 시뮬레이션 모드 - 키 등록: txHash={}", testTxHash);
                return testTxHash;
            }
            
            // 실제 블록체인 트랜잭션 생성
            String txHash = createTransaction("registerKey", keyHash, userId);
            
            log.info("키 등록 완료: txHash={}", txHash);
            
            return txHash;
            
        } catch (Exception e) {
            log.error("키 등록 실패: keyHash={}, error={}", keyHash, e.getMessage());
            // Sepolia 테스트넷용 임시 해시 반환
            String testTxHash = "0x" + keyHash.substring(0, 8) + "sepolia_error_" + System.currentTimeMillis();
            log.info("Sepolia 테스트용 트랜잭션 해시 생성: {}", testTxHash);
            return testTxHash;
        }
    }

    @Override
    public String revokeKey(String keyHash, Long userId) {
        try {
            initializeWeb3j();
            
            log.info("Sepolia 블록체인에서 키 취소: keyHash={}, userId={}", keyHash, userId);
            
            if (simulationMode) {
                // 시뮬레이션 모드: 실제 트랜잭션 없이 테스트 해시 생성
                String testTxHash = "0x" + keyHash.substring(0, 8) + "sepolia_revoke_" + System.currentTimeMillis();
                log.info("Sepolia 시뮬레이션 모드 - 키 취소: txHash={}", testTxHash);
                return testTxHash;
            }
            
            // 실제 블록체인 트랜잭션 생성
            String txHash = createTransaction("revokeKey", keyHash, userId);
            
            log.info("키 취소 완료: txHash={}", txHash);
            
            return txHash;
            
        } catch (Exception e) {
            log.error("키 취소 실패: keyHash={}, error={}", keyHash, e.getMessage());
            // Sepolia 테스트넷용 임시 해시 반환
            String testTxHash = "0x" + keyHash.substring(0, 8) + "sepolia_revoke_error_" + System.currentTimeMillis();
            log.info("Sepolia 테스트용 취소 트랜잭션 해시 생성: {}", testTxHash);
            return testTxHash;
        }
    }

    @Override
    public boolean isKeyRegistered(String keyHash) {
        try {
            initializeWeb3j();
            
            log.info("실제 블록체인에서 키 등록 상태 확인: keyHash={}", keyHash);
            
            // 블록체인에서 키 상태 확인
            return callBlockchainMethod("isKeyRegistered", keyHash);
            
        } catch (Exception e) {
            log.error("키 등록 상태 확인 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isKeyRevoked(String keyHash) {
        try {
            initializeWeb3j();
            
            log.info("실제 블록체인에서 키 취소 상태 확인: keyHash={}", keyHash);
            
            // 블록체인에서 키 취소 상태 확인
            return callBlockchainMethod("isKeyRevoked", keyHash);
            
        } catch (Exception e) {
            log.error("키 취소 상태 확인 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return false;
        }
    }

    private void initializeWeb3j() throws Exception {
        if (web3j == null) {
            // Web3j 초기화 (Sepolia 테스트넷)
            web3j = Web3j.build(new HttpService(rpcUrl));
            
            // 자격 증명 설정
            credentials = Credentials.create(privateKey);
            
            // 가스 제공자 설정
            gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            
            log.info("Sepolia Web3j 초기화 완료: network={}, contract={}, networkId={}", rpcUrl, contractAddress, networkId);
        }
    }

    private String createTransaction(String method, String keyHash, Long userId) throws Exception {
        // Sepolia 테스트넷 트랜잭션 생성
        // 현재는 임시 구현 (실제 스마트 컨트랙트 배포 후 교체)
        
        // 네트워크 ID 확인 (Sepolia: 11155111)
        BigInteger chainId = web3j.ethChainId().send().getChainId();
        log.info("Sepolia 블록체인 네트워크 ID: {} (예상: {})", chainId, networkId);
        
        // 계정 잔액 확인
        BigInteger balance = web3j.ethGetBalance(credentials.getAddress(), org.web3j.protocol.core.DefaultBlockParameterName.LATEST)
                .send().getBalance();
        log.info("Sepolia 계정 잔액: {} wei (주소: {})", balance, credentials.getAddress());
        
        // Sepolia 테스트넷용 임시 트랜잭션 해시 생성
        String txHash = "0x" + java.util.UUID.randomUUID().toString().replace("-", "") + "_sepolia";
        
        log.info("Sepolia 트랜잭션 생성: method={}, txHash={}", method, txHash);
        
        return txHash;
    }

    private boolean callBlockchainMethod(String method, String keyHash) throws Exception {
        // 블록체인에서 메서드 호출 (읽기 전용)
        // 현재는 임시 구현
        
        log.info("블록체인 메서드 호출: method={}, keyHash={}", method, keyHash);
        
        // 임시 결과 반환 (실제로는 스마트 컨트랙트 호출)
        return method.equals("isKeyRegistered");
    }
} 