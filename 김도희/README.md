## 2025-03-04 (화)
<details>
<summary>기획 아이디어 고민 (푸드뱅크 아이디어 구체화)</summary>
<div>

### 블록체인 적용 시 기대 효과

| 문제점 | 블록체인 솔루션 | 기대 효과 |
| --- | --- | --- |
| 식량 공급·수요 불균형 | 스마트 컨트랙트 기반 자동 배분 | 과잉·부족 문제 해결 |
| 유통기한 문제 | NFT 기반 추적 시스템 | 기부된 식품의 유통 경로 실시간 모니터링 |
| 물류·저장 문제 | DApp 기반 물류 추적 | 물류 경로 최적화 |
| 운영 투명성 부족 | DAO 기반 거버넌스 | 기부금 및 물품 배분 내역 공개 |
| 중복 지원 문제 | DID(탈중앙화 신원 인증) | 수혜자 중복 지원 방지 |
</div>
</details>

<details>
<summary>기술 스택 조사</summary>

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
</details>

## 2025-03-05 (수)
<details>
<summary>기획 아이디어 구체화</summary>

- 추가 아이디어 제안 및 기존 아이디어 발전 & 유효성 확인인
    - 디지털 납골당
    - 타임 캡슐느낌으로, 유언장에 제한되지 않고 미래의 내가 아는 사람에게 보내는 편지 느낌으로! (남긴 내용이 바로 전달되는게 아니라, 시기를 정해서 전달되도록 함. 현재 초등학생이 딸이 대학들어가는 시기에 메세지가 전달되도록 함)
    - 이런 내용들은 분산형 DB에 저장되고, 블록체인은 해쉬값 저장
    - 넘긴 내용을 디지털 자산으로 상속도 가능하게
- 장인 후원 플랫폼
    - 스포츠 후원말고 전통문화나 기술장인들을 후원하는 플랫폼은 어떤지?
    - 장인들이 미래에 만들 작품들을 사용자가 추천 및 투표 (DAO)
    - 투명한 후원 → 후원에 따라서 물품 구매 우선권이나 맞춤형 작품 제작 가능
    - 작품들을 NFT화
- 싸피인을 위한 CS + 취준 + 게임
    - 방식이 썸원이어도 되고, 쯔꾸르여도 되지만, 메인은 지식을 키운다!
    - 그걸 보여주는 방식이 게임이거나 질문지이거나, 선택지이거나!
    - 매력적인 이용자풀이 1000명 가까이 있고, MVP만들면 실제 테스트가 가능합니다→ 요게 가장 매력적
    - 업적같은 것을 NFT화 하는 것

- CS + 게임 아이디어 구체화 및 논의

</details>
<details>
<summary>Web3Auth 지갑 체크</summary>

- 백엔드에서 지갑 발급하는 것 : 신뢰성 문제
- MetaMask를 활용하는 법 : 유저가 MetaMask를 설치하게 하는데에서 장벽이 생길 것으로 예상
- Google Auth나 KaKao Auth를 사용하는 제 3의 서비스를 이용하는 것은 어떤지 조사함.

- Web3Auth
    - Webin
    - Klip

</details>
