// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

/**
 * @title KeyManagement
 * @dev CCTV 복호화 키 관리를 위한 스마트 컨트랙트
 */
contract KeyManagement {
    
    // ===== 구조체 정의 =====
    
    struct KeyInfo {
        address owner;           // 키 소유자 주소
        uint256 userId;         // 사용자 ID
        uint256 issuedAt;       // 발급 시간
        uint256 expiresAt;      // 만료 시간
        uint256 remainingUses;  // 남은 사용 횟수
        bool isActive;          // 활성 상태
        bool isRevoked;         // 취소 상태
        string keyType;         // 키 타입
    }
    
    // ===== 상태 변수 =====
    
    address public owner;                    // 컨트랙트 소유자
    mapping(bytes32 => KeyInfo) public keys; // 키 해시 -> 키 정보
    mapping(address => bytes32[]) public userKeys; // 사용자 -> 키 해시 배열
    mapping(uint256 => bytes32[]) public userIdKeys; // 사용자 ID -> 키 해시 배열
    
    // ===== 이벤트 정의 =====
    
    event KeyRegistered(bytes32 indexed keyHash, address indexed owner, uint256 userId, uint256 issuedAt);
    event KeyRevoked(bytes32 indexed keyHash, address indexed owner, uint256 userId, uint256 revokedAt);
    event KeyUsed(bytes32 indexed keyHash, address indexed user, uint256 remainingUses);
    event KeyExpired(bytes32 indexed keyHash, address indexed owner, uint256 expiredAt);
    
    // ===== 생성자 =====
    
    constructor() {
        owner = msg.sender;
    }
    
    // ===== 수정자 =====
    
    modifier onlyOwner() {
        require(msg.sender == owner, "Only owner can call this function");
        _;
    }
    
    modifier onlyKeyOwner(bytes32 keyHash) {
        require(keys[keyHash].owner == msg.sender, "Only key owner can call this function");
        _;
    }
    
    modifier keyExists(bytes32 keyHash) {
        require(keys[keyHash].owner != address(0), "Key does not exist");
        _;
    }
    
    modifier keyIsActive(bytes32 keyHash) {
        require(keys[keyHash].isActive, "Key is not active");
        require(!keys[keyHash].isRevoked, "Key is revoked");
        require(keys[keyHash].expiresAt > block.timestamp, "Key has expired");
        require(keys[keyHash].remainingUses > 0, "Key has no remaining uses");
        _;
    }
    
    // ===== 주요 함수 =====
    
    /**
     * @dev 키 등록
     * @param keyHash 키 해시
     * @param userId 사용자 ID
     * @param expiresAt 만료 시간
     * @param remainingUses 남은 사용 횟수
     * @param keyType 키 타입
     */
    function registerKey(
        bytes32 keyHash,
        uint256 userId,
        uint256 expiresAt,
        uint256 remainingUses,
        string memory keyType
    ) external {
        require(keys[keyHash].owner == address(0), "Key already exists");
        require(expiresAt > block.timestamp, "Expiration time must be in the future");
        require(remainingUses > 0, "Remaining uses must be greater than 0");
        
        KeyInfo memory newKey = KeyInfo({
            owner: msg.sender,
            userId: userId,
            issuedAt: block.timestamp,
            expiresAt: expiresAt,
            remainingUses: remainingUses,
            isActive: true,
            isRevoked: false,
            keyType: keyType
        });
        
        keys[keyHash] = newKey;
        userKeys[msg.sender].push(keyHash);
        userIdKeys[userId].push(keyHash);
        
        emit KeyRegistered(keyHash, msg.sender, userId, block.timestamp);
    }
    
    /**
     * @dev 키 취소
     * @param keyHash 키 해시
     */
    function revokeKey(bytes32 keyHash) external keyExists(keyHash) onlyKeyOwner(keyHash) {
        require(keys[keyHash].isActive && !keys[keyHash].isRevoked, "Key is not active");
        
        keys[keyHash].isRevoked = true;
        
        emit KeyRevoked(keyHash, msg.sender, keys[keyHash].userId, block.timestamp);
    }
    
    /**
     * @dev 키 사용 (사용 횟수 감소)
     * @param keyHash 키 해시
     */
    function useKey(bytes32 keyHash) external keyExists(keyHash) keyIsActive(keyHash) {
        keys[keyHash].remainingUses--;
        
        emit KeyUsed(keyHash, msg.sender, keys[keyHash].remainingUses);
        
        // 사용 횟수가 0이 되면 자동으로 비활성화
        if (keys[keyHash].remainingUses == 0) {
            keys[keyHash].isActive = false;
        }
    }
    
    /**
     * @dev 만료된 키 정리
     * @param keyHash 키 해시
     */
    function expireKey(bytes32 keyHash) external keyExists(keyHash) {
        require(keys[keyHash].expiresAt <= block.timestamp, "Key has not expired yet");
        require(keys[keyHash].isActive, "Key is already inactive");
        
        keys[keyHash].isActive = false;
        
        emit KeyExpired(keyHash, keys[keyHash].owner, block.timestamp);
    }
    
    // ===== 조회 함수 =====
    
    /**
     * @dev 키 정보 조회
     * @param keyHash 키 해시
     * @return 키 정보
     */
    function getKeyInfo(bytes32 keyHash) external view returns (KeyInfo memory) {
        return keys[keyHash];
    }
    
    /**
     * @dev 키 등록 여부 확인
     * @param keyHash 키 해시
     * @return 등록 여부
     */
    function isKeyRegistered(bytes32 keyHash) external view returns (bool) {
        return keys[keyHash].owner != address(0);
    }
    
    /**
     * @dev 키 취소 여부 확인
     * @param keyHash 키 해시
     * @return 취소 여부
     */
    function isKeyRevoked(bytes32 keyHash) external view returns (bool) {
        return keys[keyHash].isRevoked;
    }
    
    /**
     * @dev 키 유효성 확인
     * @param keyHash 키 해시
     * @return 유효성 여부
     */
    function isKeyValid(bytes32 keyHash) external view returns (bool) {
        KeyInfo memory key = keys[keyHash];
        return key.owner != address(0) && 
               key.isActive && 
               !key.isRevoked && 
               key.expiresAt > block.timestamp && 
               key.remainingUses > 0;
    }
    
    /**
     * @dev 사용자의 키 목록 조회
     * @param userAddress 사용자 주소
     * @return 키 해시 배열
     */
    function getUserKeys(address userAddress) external view returns (bytes32[] memory) {
        return userKeys[userAddress];
    }
    
    /**
     * @dev 사용자 ID로 키 목록 조회
     * @param userId 사용자 ID
     * @return 키 해시 배열
     */
    function getKeysByUserId(uint256 userId) external view returns (bytes32[] memory) {
        return userIdKeys[userId];
    }
    
    /**
     * @dev 키 소유자 조회
     * @param keyHash 키 해시
     * @return 소유자 주소
     */
    function getKeyOwner(bytes32 keyHash) external view returns (address) {
        return keys[keyHash].owner;
    }
    
    // ===== 관리자 함수 =====
    
    /**
     * @dev 컨트랙트 소유자 변경
     * @param newOwner 새로운 소유자
     */
    function transferOwnership(address newOwner) external onlyOwner {
        require(newOwner != address(0), "New owner cannot be zero address");
        owner = newOwner;
    }
    
    /**
     * @dev 긴급 키 취소 (관리자만)
     * @param keyHash 키 해시
     */
    function emergencyRevokeKey(bytes32 keyHash) external onlyOwner keyExists(keyHash) {
        keys[keyHash].isRevoked = true;
        keys[keyHash].isActive = false;
        
        emit KeyRevoked(keyHash, keys[keyHash].owner, keys[keyHash].userId, block.timestamp);
    }
} 