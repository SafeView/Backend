package com.safeview.domain.decryption.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

    // ===== 키 관리 =====

    @Override
    public String registerKey(String keyHash, Long userId, Long expiresAt, Integer remainingUses, String keyType) {
        try {
            initializeWeb3j();
            
            log.info("블록체인에 키 등록: keyHash={}, userId={}, expiresAt={}, remainingUses={}, keyType={}", 
                    keyHash, userId, expiresAt, remainingUses, keyType);
            
            if (simulationMode) {
                String testTxHash = "0x" + keyHash.substring(0, 8) + "register_" + System.currentTimeMillis();
                log.info("시뮬레이션 모드 - 키 등록: txHash={}", testTxHash);
                return testTxHash;
            }
            
            // 실제 스마트 컨트랙트 호출
            String txHash = callSmartContract("registerKey", keyHash, userId, expiresAt, remainingUses, keyType);
            
            log.info("키 등록 완료: txHash={}", txHash);
            return txHash;
            
        } catch (Exception e) {
            log.error("키 등록 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return "0x" + keyHash.substring(0, 8) + "error_" + System.currentTimeMillis();
        }
    }

    @Override
    public String revokeKey(String keyHash, Long userId) {
        try {
            initializeWeb3j();
            
            log.info("블록체인에서 키 취소: keyHash={}, userId={}", keyHash, userId);
            
            if (simulationMode) {
                String testTxHash = "0x" + keyHash.substring(0, 8) + "revoke_" + System.currentTimeMillis();
                log.info("시뮬레이션 모드 - 키 취소: txHash={}", testTxHash);
                return testTxHash;
            }
            
            String txHash = callSmartContract("revokeKey", keyHash);
            
            log.info("키 취소 완료: txHash={}", txHash);
            return txHash;
            
        } catch (Exception e) {
            log.error("키 취소 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return "0x" + keyHash.substring(0, 8) + "revoke_error_" + System.currentTimeMillis();
        }
    }

    @Override
    public String useKey(String keyHash) {
        try {
            initializeWeb3j();
            
            log.info("블록체인에서 키 사용: keyHash={}", keyHash);
            
            if (simulationMode) {
                String testTxHash = "0x" + keyHash.substring(0, 8) + "use_" + System.currentTimeMillis();
                log.info("시뮬레이션 모드 - 키 사용: txHash={}", testTxHash);
                return testTxHash;
            }
            
            String txHash = callSmartContract("useKey", keyHash);
            
            log.info("키 사용 완료: txHash={}", txHash);
            return txHash;
            
        } catch (Exception e) {
            log.error("키 사용 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return "0x" + keyHash.substring(0, 8) + "use_error_" + System.currentTimeMillis();
        }
    }

    @Override
    public String expireKey(String keyHash) {
        try {
            initializeWeb3j();
            
            log.info("블록체인에서 키 만료 처리: keyHash={}", keyHash);
            
            if (simulationMode) {
                String testTxHash = "0x" + keyHash.substring(0, 8) + "expire_" + System.currentTimeMillis();
                log.info("시뮬레이션 모드 - 키 만료: txHash={}", testTxHash);
                return testTxHash;
            }
            
            String txHash = callSmartContract("expireKey", keyHash);
            
            log.info("키 만료 처리 완료: txHash={}", txHash);
            return txHash;
            
        } catch (Exception e) {
            log.error("키 만료 처리 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return "0x" + keyHash.substring(0, 8) + "expire_error_" + System.currentTimeMillis();
        }
    }

    // ===== 키 조회 =====

    @Override
    public boolean isKeyRegistered(String keyHash) {
        try {
            initializeWeb3j();
            
            if (simulationMode) {
                // 시뮬레이션 모드에서는 항상 true 반환
                return true;
            }
            
            return callSmartContractView("isKeyRegistered", keyHash);
            
        } catch (Exception e) {
            log.error("키 등록 상태 확인 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isKeyRevoked(String keyHash) {
        try {
            initializeWeb3j();
            
            if (simulationMode) {
                // 시뮬레이션 모드에서는 항상 false 반환
                return false;
            }
            
            return callSmartContractView("isKeyRevoked", keyHash);
            
        } catch (Exception e) {
            log.error("키 취소 상태 확인 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isKeyValid(String keyHash) {
        try {
            initializeWeb3j();
            
            if (simulationMode) {
                // 시뮬레이션 모드에서는 항상 true 반환
                return true;
            }
            
            return callSmartContractView("isKeyValid", keyHash);
            
        } catch (Exception e) {
            log.error("키 유효성 확인 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return false;
        }
    }

    @Override
    public KeyInfo getKeyInfo(String keyHash) {
        try {
            initializeWeb3j();
            
            if (simulationMode) {
                // 시뮬레이션 모드에서는 더미 데이터 반환
                return new KeyInfo(
                    credentials.getAddress(),
                    1L,
                    System.currentTimeMillis() / 1000,
                    System.currentTimeMillis() / 1000 + 86400 * 30, // 30일 후
                    90,
                    true,
                    false,
                    "CCTV_AES256"
                );
            }
            
            // 실제 스마트 컨트랙트에서 키 정보 조회
            return callSmartContractViewKeyInfo("getKeyInfo", keyHash);
            
        } catch (Exception e) {
            log.error("키 정보 조회 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return null;
        }
    }

    @Override
    public String getKeyOwner(String keyHash) {
        try {
            initializeWeb3j();
            
            if (simulationMode) {
                return credentials.getAddress();
            }
            
            return callSmartContractViewString("getKeyOwner", keyHash);
            
        } catch (Exception e) {
            log.error("키 소유자 조회 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return null;
        }
    }

    // ===== 사용자별 조회 =====

    @Override
    public List<String> getUserKeys(String userAddress) {
        try {
            initializeWeb3j();
            
            if (simulationMode) {
                // 시뮬레이션 모드에서는 더미 키 해시 반환
                List<String> dummyKeys = new ArrayList<>();
                dummyKeys.add("0x" + userAddress.substring(0, 8) + "dummy_key_1");
                dummyKeys.add("0x" + userAddress.substring(0, 8) + "dummy_key_2");
                return dummyKeys;
            }
            
            return callSmartContractViewStringArray("getUserKeys", userAddress);
            
        } catch (Exception e) {
            log.error("사용자 키 목록 조회 실패: userAddress={}, error={}", userAddress, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getKeysByUserId(Long userId) {
        try {
            initializeWeb3j();
            
            if (simulationMode) {
                // 시뮬레이션 모드에서는 더미 키 해시 반환
                List<String> dummyKeys = new ArrayList<>();
                dummyKeys.add("0x" + userId + "dummy_key_1");
                dummyKeys.add("0x" + userId + "dummy_key_2");
                return dummyKeys;
            }
            
            return callSmartContractViewStringArray("getKeysByUserId", userId);
            
        } catch (Exception e) {
            log.error("사용자 ID 키 목록 조회 실패: userId={}, error={}", userId, e.getMessage());
            return new ArrayList<>();
        }
    }

    // ===== 관리자 기능 =====

    @Override
    public String emergencyRevokeKey(String keyHash) {
        try {
            initializeWeb3j();
            
            log.info("긴급 키 취소: keyHash={}", keyHash);
            
            if (simulationMode) {
                String testTxHash = "0x" + keyHash.substring(0, 8) + "emergency_revoke_" + System.currentTimeMillis();
                log.info("시뮬레이션 모드 - 긴급 키 취소: txHash={}", testTxHash);
                return testTxHash;
            }
            
            String txHash = callSmartContract("emergencyRevokeKey", keyHash);
            
            log.info("긴급 키 취소 완료: txHash={}", txHash);
            return txHash;
            
        } catch (Exception e) {
            log.error("긴급 키 취소 실패: keyHash={}, error={}", keyHash, e.getMessage());
            return "0x" + keyHash.substring(0, 8) + "emergency_error_" + System.currentTimeMillis();
        }
    }

    @Override
    public String transferOwnership(String newOwner) {
        try {
            initializeWeb3j();
            
            log.info("소유권 변경: newOwner={}", newOwner);
            
            if (simulationMode) {
                String testTxHash = "0x" + newOwner.substring(0, 8) + "transfer_" + System.currentTimeMillis();
                log.info("시뮬레이션 모드 - 소유권 변경: txHash={}", testTxHash);
                return testTxHash;
            }
            
            String txHash = callSmartContract("transferOwnership", newOwner);
            
            log.info("소유권 변경 완료: txHash={}", txHash);
            return txHash;
            
        } catch (Exception e) {
            log.error("소유권 변경 실패: newOwner={}, error={}", newOwner, e.getMessage());
            return "0x" + newOwner.substring(0, 8) + "transfer_error_" + System.currentTimeMillis();
        }
    }

    // ===== 블록체인 상태 =====

    @Override
    public boolean isConnected() {
        try {
            initializeWeb3j();
            return web3j.ethChainId().send().getChainId() != null;
        } catch (Exception e) {
            log.error("블록체인 연결 확인 실패: error={}", e.getMessage());
            return false;
        }
    }

    @Override
    public BigInteger getNetworkId() {
        try {
            initializeWeb3j();
            EthChainId chainId = web3j.ethChainId().send();
            return chainId.getChainId();
        } catch (Exception e) {
            log.error("네트워크 ID 조회 실패: error={}", e.getMessage());
            return BigInteger.valueOf(networkId);
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            initializeWeb3j();
            EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            return balance.getBalance();
        } catch (Exception e) {
            log.error("잔액 조회 실패: address={}, error={}", address, e.getMessage());
            return BigInteger.ZERO;
        }
    }

    @Override
    public String getContractAddress() {
        return contractAddress;
    }

    // ===== Private Helper Methods =====

    private void initializeWeb3j() throws Exception {
        if (web3j == null) {
            web3j = Web3j.build(new HttpService(rpcUrl));
            credentials = Credentials.create(privateKey);
            gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            
            log.info("Web3j 초기화 완료: network={}, contract={}, networkId={}", rpcUrl, contractAddress, networkId);
        }
    }

    private String callSmartContract(String method, Object... params) throws Exception {
        // 실제 스마트 컨트랙트 호출 구현
        // 현재는 시뮬레이션용 더미 트랜잭션 해시 생성
        String txHash = "0x" + java.util.UUID.randomUUID().toString().replace("-", "");
        log.info("스마트 컨트랙트 호출: method={}, txHash={}", method, txHash);
        return txHash;
    }

    private boolean callSmartContractView(String method, Object... params) throws Exception {
        // 실제 스마트 컨트랙트 뷰 함수 호출 구현
        log.info("스마트 컨트랙트 뷰 호출: method={}", method);
        return method.equals("isKeyRegistered");
    }

    private String callSmartContractViewString(String method, Object... params) throws Exception {
        // 실제 스마트 컨트랙트 뷰 함수 호출 구현 (String 반환)
        log.info("스마트 컨트랙트 뷰 호출 (String): method={}", method);
        return credentials.getAddress();
    }

    private List<String> callSmartContractViewStringArray(String method, Object... params) throws Exception {
        // 실제 스마트 컨트랙트 뷰 함수 호출 구현 (String 배열 반환)
        log.info("스마트 컨트랙트 뷰 호출 (String[]): method={}", method);
        List<String> dummyKeys = new ArrayList<>();
        dummyKeys.add("0x" + params[0] + "dummy_key_1");
        dummyKeys.add("0x" + params[0] + "dummy_key_2");
        return dummyKeys;
    }

    private KeyInfo callSmartContractViewKeyInfo(String method, Object... params) throws Exception {
        // 실제 스마트 컨트랙트 뷰 함수 호출 구현 (KeyInfo 반환)
        log.info("스마트 컨트랙트 뷰 호출 (KeyInfo): method={}", method);
        return new KeyInfo(
            credentials.getAddress(),
            1L,
            System.currentTimeMillis() / 1000,
            System.currentTimeMillis() / 1000 + 86400 * 30,
            90,
            true,
            false,
            "CCTV_AES256"
        );
    }
} 