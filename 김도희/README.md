## 2025-03-04 (화)
- 기획 아이디어 고민
    - 푸드뱅크 아이디어 구체화
    ### **블록체인 적용 시 기대 효과**

    | 문제점 | 블록체인 솔루션 | 기대 효과 |
    | --- | --- | --- |
    | 식량 공급·수요 불균형 | 스마트 컨트랙트 기반 자동 배분 | 과잉·부족 문제 해결 |
    | 유통기한 문제 | NFT 기반 추적 시스템 | 기부된 식품의 유통 경로 실시간 모니터링 |
    | 물류·저장 문제 | DApp 기반 물류 추적 | 물류 경로 최적화 |
    | 운영 투명성 부족 | DAO 기반 거버넌스 | 기부금 및 물품 배분 내역 공개 |
    | 중복 지원 문제 | DID(탈중앙화 신원 인증) | 수혜자 중복 지원 방지 |
- 기술 스택 조사
    ### BackEnd
    - 역할
        - 블록체인 이벤트 감지
        - 실시간 업데이트
        - 트랜잭션 기록 저장 및 분석 / 파일 저장
        - 블록체인 데이터 조회
        - 프론트엔드에서 블록체인 이벤트 실시간 감지가 어려운 이유
            
            ### **1) 왜 프론트엔드에서 블록체인 이벤트 감지가 어려운가?**
            
            - 블록체인은 Polling 방식이 기본이며, WebSocket을 지원하는 서비스가 많지 않음.
            - Ethers.js나 Wagmi 같은 라이브러리는 기본적으로 WebSocket을 지원하지 않음.
            - 실시간 이벤트 감지를 하려면 WebSocket 지원 노드(Infura, Alchemy)가 필요하며, 비용이 발생할 수 있음.
            
            ### **2) 백엔드(Spring Boot)에서 블록체인 이벤트 감지를 하면 어떤 장점이 있나?**
            
            - Web3j 또는 Ethers.js를 사용해 블록체인 WebSocket을 직접 활용할 수 있음.
            - 블록체인 데이터를 백엔드에서 가공하여 프론트엔드에 전달 가능.
            - 프론트엔드에서 불필요한 Polling 요청을 줄이고, 실시간 업데이트가 가능.
    - SpringBoot + **Web3 JS**
        - `vs` **node JS + Express JS + Ethers JS**
        - Ethers JS vs Web3 JS (Ethers.js가 Web3.js보다 나은 이유)
            
            - 더 가볍고 빠름 (85KB vs 10MB 이상)
            
            - 더 직관적인 API 제공
            
            - TypeScript 공식 지원
            
            - 스마트 컨트랙트, 트랜잭션, 서명 등을 쉽게 처리 가능
            
            - Ethereum 커뮤니티에서도 Ethers.js를 추천하는 추세
            
    - **IPFS**
        - 분산형 데이터베이스

    ### FrontEnd

    - 역할
        - 지갑 연결
        - 트랜잭션 실행, 데이터 조회
    - 언어
        - React 18
        - TypeScript
    - 상태관리
        - Zustand
    - 라이브러리
        - Tailwind CSS
        - Shad CN (UI Library)
        - **Three JS (Web GL 기반 3D 이펙트 JS)**
        - zod (폼 검증)
    - 블록체인
        - **Wagmi**
            - React에서 Ethers.js를 쉽게 다룰 수 있도록 도와주는 라이브러리, MetaMask, WalletConnect 등 다양한 지갑을 지원
            - **React 기반 DApp** 개발
        - **Ethers JS**
            - 블록체인과 상호작용하는 JavaScript 라이브러리
            - 이더리움 네트워크 연결, 지갑 연동, 이더리움 잔액 조회, 스마트 컨트랙트 호출

    ### BlockChain

    - **Solidity**
        - 스마트 컨트랙트 개발 언어
    - **Hardhat (Ethers JS / node JS 기반) vs Truffle (Web3 Js 지원) + Ganache vs Remix IDE**
        - 블록체인 애플리케이션 개발 및 테스트 프레임워크
        - 스마트 컨트랙트 개발 / 배포
    - **MetaMask**
        - 지갑
    - **Sepolia TestNet**
        - 블록체인 네트워크 (개발 운영용)
        - 실 서비스 운영을 할꺼면 테스트네트워크가 실 운영중인 네트워크를 선택 해야 함
        - 비교 분석
            
            
            | 비교 항목 | **Ethereum** | **Hyperledger Fabric** | **Avalanche** | **Polygon (MATIC)** |
            | --- | --- | --- | --- | --- |
            | **기본 개념** | 탈중앙화 스마트 컨트랙트 플랫폼 | 기업용 프라이빗 블록체인 | 빠른 트랜잭션 처리 L1 블록체인 | 이더리움 확장성 솔루션 (L2) |
            | **사용 목적** | 퍼블릭 DApp 및 DeFi | 기업 내 프라이빗 블록체인 구축 | 빠른 트랜잭션 처리 | 이더리움 가스비 절감 및 성능 개선 |
            | **네트워크 유형** | 퍼블릭 블록체인 | 프라이빗 블록체인 | 퍼블릭 블록체인 | Layer 2 (L2) 블록체인 |
            | **합의 알고리즘** | PoS (Proof of Stake) | PBFT (Practical Byzantine Fault Tolerance) | Snowman & Avalanche Consensus | PoS + ZK-Rollups |
            | **속도 & 확장성** | 느림 (TPS 낮음, 가스비 높음) | 빠름 (기업 내 트랜잭션) | 빠름 (4500+ TPS) | 빠름 (Ethereum 대비 저렴한 수수료) |
            | **보안성** | 매우 높음 | 기업 내 보안 설정 가능 | 높은 보안성 | Ethereum과 동일한 보안 |
            | **개발 환경** | Solidity, EVM 기반 | Golang, Java, Node.js | Solidity, EVM 기반 | Solidity, EVM 기반 |
            | **가스비** | 높음 | 없음 (기업 내부 운영) | 낮음 | 낮음 (Ethereum보다 저렴) |
            | **대표적인 사용 사례** | NFT, DeFi, DApp | 공급망 관리, 금융, 의료 | 빠른 결제, NFT 마켓 | NFT, 게임, DApp |
        - 프라이빗 블록체인 네트워크
            
            
            | 사용 목적 | 추천 블록체인 |
            | --- | --- |
            | **기업용 프라이빗 블록체인 (금융, 공급망, 의료)** | **Hyperledger Besu** |
            | **맞춤형 EVM 블록체인 (NFT, 게임, Web3 서비스)** | **Polygon Supernets** |
            | **초고속 트랜잭션 필요 (DeFi, 대형 금융 서비스)** | **Avalanche Subnets** |
            | **BSC 기반 프라이빗 블록체인 운영** | **BSC Enterprise** |
    - **GraphQL (The Graph)**
        - 블록체인 데이터 쿼리 서비스 (인덱싱 기반으로 블록체인 데이터 조회가 더 빠름)

    ### CI/CD

    - Jenkins
    - Docker  
