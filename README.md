# ⚾ 블록체인 기반 팬 주도형 프로야구 플랫폼 『 BETTY 』

### 🔖 소개
Betty는 블록체인 기반의 투명한 토큰 경제 시스템으로 팬들이 실시간 소통하고 구단 의사결정에 직접 참여할 수 있는 새로운 프로야구 생태계를 제공합니다.
<br>

## 🔗 프로젝트 링크

**[Betty](https://j12a609.p.ssafy.io/)**
- 운영기간 : 2025.04.01(화) ~ 2025.04.15(화)

<br>

## 📆 프로젝트 정보
**기간**: 2025.02.24(월) ~ 2025.04.11(금), 7주  
**인원**: 6명

<br>

## 🧡 팀 소개

|                                      이재훈                                      |                                      김도희                                      |                                      예세림                                      |                                      이성희                                      |                     이유나                      |                                      이주호                                      |
|:-----------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|:--------------------------------------------:|:-----------------------------------------------------------------------------:|
| <img src="https://avatars.githubusercontent.com/u/62222791?v=4" width="120"/> | <img src="https://avatars.githubusercontent.com/u/68512221?v=4" width="120"/> | <img src="https://avatars.githubusercontent.com/u/163365711?v=4" width="120"/> | <img src="https://avatars.githubusercontent.com/u/44632544?v=4" width="120"/> |          <img src="https://github.com/user-attachments/assets/c5ef17b1-9d78-4114-8e1f-51845b46b02f" width="120"/>           | <img src="https://avatars.githubusercontent.com/u/84345021?v=4" width="120"/> |
|                                 **팀장, Infra**                                 |                                   **Front**                                   |                                   **Back**                                    |                                   **Back**                                    |                  **Front**                   |                                 **Front**                                  |
|              [@potential1205](https://github.com/potential1205)               |                       [@Dobee-Kim](https://github.com/Dobee-Kim)                       |              [@serimmmaime](https://github.com/serimmmaime)               |              [@LeeSeongHui](https://github.com/LeeSeongHui)               | [@yuuuna-lee](https://github.com/yuuuna-lee) |              [@joeholee](https://github.com/joeholee)               |

| Contributors | Role                | Position                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|--------------|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 이재훈          | 팀장, <br /> Backend  | - **인프라 구성 및 관리:** AWS(EC2, S3), Docker, Jenkins, NGINX를 이용한 웹 서비스 인프라 설계·운영<br> - **CI/CD 자동 배포:** Jenkins와 GitLab Webhook, Docker Compose 기반 자동 배포 파이프라인 구축<br> - **프라이빗 블록체인 네트워크 구축 (협업):** Geth를 활용한 전용 블록체인 네트워크 환경 설계 및 구축<br> - **전광판 실시간 소켓 통신:** Spring WebSocket(STOMP), SimpMessagingTemplate을 이용한 전광판 데이터 송수신 기능 구현<br> - **전광판 접근 권한 검증:** Web3j 기반 토큰 검증 로직으로 사용자별 접근 제어 구현<br> - **전광판 데이터 조회 API:** Spring Data Redis 및 JPA를 활용한 실시간 전광판 데이터 조회 및 캐싱 기능 개발<br> - **인증 시스템 (로그인·회원가입):** Web3Auth 연동으로 지갑 기반 로그인·회원가입 기능 구현<br> - **팀 채널 투표 시스템:** Solidity 스마트 컨트랙트와 Web3j 연동을 통해 안건 등록·투표(토큰 이동) 로직 구현 |
| 김도희          | 팀원, <br /> Frontend | - |
| 예세림          | 팀원, <br /> Backend  | - **팬 토큰 거래소 기능 전담:** 팬 토큰 간 교환, 예치, 회수 로직을 중심으로 거래소 구조를 설계하고, 관련 스마트 컨트랙트를 Foundry로 작성해 배포<br> - **AMM 기반 가격 조정 로직 구현:** x*y=k 수식 기반의 자동 가격 결정 구조를 설계하고, 유동성 풀 상태를 기준으로 실시간 가격이 반영되도록 컨트랙트 및 API 설계<br> - **Web3j 연동 거래 API 구현:** 컨트랙트 상태 기반으로 가격 계산, 잔액 확인, 트랜잭션 실행 및 결과 조회를 포함한 RESTful API를 Spring Boot로 구현하고 Web3j로 연동<br> - **RewardPool 설계 및 보상 순환 구조 구현:** 경기 예측, DAO 투표 등의 참여 활동에 따라 보상 토큰을 분배하고, 일부를 유동성 풀에 자동 재투입하는 순환 메커니즘 구현<br> - **트랜잭션 상태 처리 및 흐름 연동:** 거래 요청 시 `PENDING → SUCCESS/FAIL` 상태로 전환되는 구조를 설계하고, 프론트와의 트랜잭션 연동 기준 및 예외 처리 조건을 문서화<br> - **스마트 컨트랙트 설계 및 테스트:** `Swap`, `AddLiquidity`, `RemoveLiquidity`, `RewardPool` 등 복수 컨트랙트 설계 및 Foundry 기반 테스트 케이스 작성<br> - **사용자 중심 흐름 설계:** ‘토큰 선택 → 금액 입력 → 예상 가격 확인 → 거래 실행 → 결과 반환’의 사용자 흐름을 기준으로 기능 구조를 정리하고, API 응답 형식과 예외 케이스를 기획 단계부터 설계 및 문서화<br> - **배포 환경 및 권한 구조 적용:** `Ownable`, `Burnable`, `SafeERC20` 등 OpenZeppelin 모듈을 학습하고, Solidity 버전 충돌 및 종속성 이슈를 해결하며 컨트랙트 배포 안정화 진행 |
| 이성희          | 팀원, <br /> Backend  | - **경기 정보 수집 및 캐싱 시스템:** Spring Scheduler, Redis, Selenium을 활용해 KBO 경기 일정, 라인업, 중계 데이터를 자동 수집 및 캐싱하는 시스템 구축<br> - **멀티 스레드 기반 실시간 크롤링:** Selenium과 TaskScheduler를 활용한 멀티 스레드 구조로 실시간 중계 데이터 수집 기능 구현<br> - **실시간 문제 생성 및 전송 시스템:** Redis에 저장된 중계 데이터를 기반으로 경기 상황에 따른 예측 문제를 자동 생성하고, WebSocket(STOMP) 기반으로 클라이언트에 실시간 전송<br> - **중계 기반 문제 정답 판별 시스템:** 실시간 중계 데이터를 기반으로 출제된 문제의 조건 충족 여부를 확인하고, 정답 정보를 Redis에 자동 반영하는 판별 시스템 구현<br> - **경기 결과 처리 및 정산 연동 시스템:** 경기 종료 시점에 승패, MVP 등 결과 데이터를 Redis에 저장하고, Web3j 기반 스마트컨트랙트의 정산 함수를 호출하여 정산이 이뤄지도록 연동 처리 |
| 이유나          | 팀원, <br /> Frontend | - |
| 이주호          | 팀원, <br /> Frontend | - **프로젝트 구조 설계:** 팬 토큰 거래소의 프론트엔드(React), 백엔드(Spring Boot), 블록체인(Web3j 연동) 전반의 구조를 설계하고, 도메인 분할 및 기능 흐름의 기준을 정립<br> - **팬 토큰 거래소 설계 및 구현:** KRW, BTC, FAN 간 거래를 위한 교환 시스템 설계 및 구현. 엔티티·레포지토리·서비스·컨트롤러 계층 구성, 트랜잭션 상태(`PENDING`, `SUCCESS`, `FAIL`) 기반 처리 구조 포함  <br> - **거래소 UI 기능 구현 및 트랜잭션 연동:** 실시간 가격 차트, 거래 폼, 결과 출력 등 프론트 단 UI 구현. 프론트에서 트랜잭션 서명 요청 후 결과를 API 연동해 처리하는 전체 흐름 구성<br> - **AMM 기반 경제 시스템 설계:** Uniswap V1의 x*y=k 알고리즘을 기반으로 자동 가격 조정 로직을 설계하고, 초기 유동성, 슬리피지 계산, 실시간 가격 반영 구조까지 고려한 경제 시스템 구현<br> - **스마트 컨트랙트 연동 및 고도화:** Solidity 컨트랙트를 수정하고, Web3j CLI로 ABI/BIN을 생성해 Java Wrapper 클래스를 자동 생성한 뒤, 백엔드 서비스에 연동<br> - **로그인 시스템 및 마이페이지 연동:** Web3Auth 기반 지갑 로그인 기능을 구현하고, 로그인된 사용자 기준으로 팬 토큰 보유 내역, 거래 내역을 마이페이지에서 조회 가능하도록 API 설계 및 연결<br> - **인프라 설정 및 HTTPS 프록시 구성:** HTTP/HTTPS 간 충돌 이슈 해결을 위해 NGINX 기반 리버스 프록시 설정. 별도 EC2 서버를 통한 안전한 인증 경로 구성<br> - **거래소 관련 API 설계 및 구현, 연동:** 프론트 연동을 고려해 거래 요청/응답 흐름과 예외 처리 기준을 설계하고, 이를 바탕으로 실거래 가능한 RESTful API 구현<br> - **서기 및 프로젝트 포트폴리오 제작:** 회의록 작성 및 팀 작업 흐름 정리. 최종 발표용 영상 포트폴리오를 직접 기획하고, 콘텐츠 구성 및 제작 전반 주도 |

<br>

## 💡 기획 의도 및 배경
> 우리 서비스는 블록체인의 투명성과 불변성, 분산 거버넌스를 바탕으로 ‘팬이 팀의 진정한 주인’이 되는 프로야구 문화를 구현하고자 기획되었습니다. 팬 토큰 거래와 구단 운영 관련 투표 등이 모두 블록체인에 영구 저장되기 때문에, 팬들은 자신의 참여가 언제나 검증 가능하고 왜곡 없이 보존된다는 확신을 얻습니다. 이를 통해 단순 관람을 넘어 구단 의사결정 과정에 직접 목소리를 내고, 그 결과를 실시간으로 확인하면서 팬과 구단 간의 소통 창구가 탈중앙성과 투명성, 불변성을 가지고 재정립됩니다. 또한 생태계내에서 사용되는 토큰은 환전 및 스왑이 가능하므로 디지털 자산으로서의 팬 경험 가치를 한층 확장합니다. 마지막으로 마이페이지에서 나의 지갑과 거래 내역, 참여 이력을 모두 투명하게 관리할 수 있어, 팬들은 자신이 플랫폼에 쌓아온 모든 순간을 자랑거리로 돌아보고 언제든 재확인할 수 있습니다. 이렇듯 블록체인의 핵심 특성을 통해 팬 중심의 프로야구 생태계를 구축함으로써, 팬들이 함께 만들고 함께 누리는 진정한 ‘팬 주도형 스포츠 문화’를 실현합니다.

<br>

## ✨ 핵심 기능

### 1. 팬 토큰 거래소 (AMM 기반)
<img src="https://github.com/user-attachments/assets/512f4c59-c445-4c44-a14c-9e8e0ec5ddff" width=400>
<br>
**실시간 가격 변동**
- 팬 토큰 거래소는 AMM(Automated Market Maker) 방식을 활용해 BETTY ↔ 팬토큰 간 가격이 자동으로 조정되는 탈중앙화 거래소입니다. 스마트 컨트랙트에서 모든 스왑 로직이 실행되며, Web3j와 연동된 백엔드 시스템은 거래 흐름과 상태를 실시간으로 관리합니다.

### 핵심 기능

- **AMM 방식(x*y=k)**을 사용해 BETTY ↔ 팬토큰 간 가격이 자동으로 결정되는 탈중앙화 거래소 구조로 설계되었습니다.
- 거래는 모두 **스마트 컨트랙트에서 실행되며**, Web3j 기반 백엔드 API와 연동해 **거래 흐름 및 상태를 관리**합니다.
- 팬 토큰 보상 로직을 위해 **리워드풀**을 구현했습니다.
- 실시간 가격 계산, 유동성 예치/회수, 스왑 기능 등을 포함한 **전체 거래 기능과 구조를 직접 설계·구현**하였습니다.

### 핵심 구조

- **유동성 공급:** 시즌 시작 시 각 풀에 팬토큰 1,000,000개, BETTY 10,000,000개를 예치해 초기 거래 환경을 구성합니다.
- **스왑:** `x * y = k` 공식을 기반으로, 거래량에 따라 팬토큰의 가격이 실시간으로 조정됩니다.
- **가격 결정:** 초기 가격은 10 BETTY로 설정되며, 거래에 따른 수급 변화에 따라 자동 조정됩니다.
- **리워드풀:** 경기 예측, DAO 투표, 퀴즈 등에서 사용자에게 팬토큰을 분배하기 위해 별도 리워드풀 컨트랙트를 구현하고, 보상 지급 트랜잭션과 연동하였습니다.
- **거래 내역 관리:** 스마트 컨트랙트 상의 트랜잭션을 Web3j Scheduler로 0.5초 마다 반영하여 실시간으로 제공했습니다.
- **상태 처리:** 트랜잭션 발생 직후 상태를 PENDING으로 표시하고, 성공 여부를 Web3j에서 주기적으로 확인하여 최종 결과를 업데이트합니다.

<br>

### 2. 실시간 야구 중계 연동 퀴즈 및 실시간 전광판

**경기 상황 연동 이벤트**
- 실시간 야구 중계 정보와 연계된 이벤트 및 퀴즈를 통해 팬들이 경기 상황을 더욱 몰입감 있게 즐길 수 있습니다.

**참여 기반 전광판 그리기**
- 팬들이 코인을 소모하며 전광판에 그림을 그리게 하여, 팬들의 창의적 참여와 경쟁을 유도하고 일정 이닝마다 전광판 그리기의 결과가 이미지로 저장되어 경기를 대표하는 픽셀 아트가 아카이브에 저장되어 누구나 관람할 수 있습니다.

<img src="https://github.com/user-attachments/assets/46db7e25-0cb3-42a2-b18d-96bde4353c07" width=350>
<img src="https://github.com/user-attachments/assets/895cb441-7f12-4ff6-8ffa-92c5b301bcd7" width=322>

<br>

### 3. 사전 승리 팀, MVP 예측
**승리 팀 예측**
- 경기 시작 30분전부터 예상 승리 팀에 팬 토큰을 배팅할 수 있습니다.

**MVP 예측**
- 경시 시작 30분전부터 양팀 선발 라인업을 기준으로 예상 MVP 선수에 토큰을 배팅할 수 있습니다.

**정산 시스템**
- 사전 예측에 참여한 토큰은 모두 스마트 컨트랙트에 예치되고, 경기가 끝나면 정답자들은 먼저 베팅한 원금을 돌려받습니다.
- 오답자들의 베팅 토큰 전액은 losersPool로 적립된 뒤, 그 절반은 개인 베팅액 비율에 따라, 나머지 절반은 정답자 수에 따라 균등하게 나눠 보상됩니다.

<img src="https://github.com/user-attachments/assets/45416034-30b8-4aaa-820e-4187ce7d5c22" width=350>

<br>

### 4. 팀 채널
**의사결정 안건 제안 및 투표**
- 팬들은 팬 토큰을 사용해 팀 운영과 관련된 안건을 제안하고, 다른 팬들의 지지를 얻기 위해 투표할 수 있습니다.
- 집단 지성 활용: 팬들의 의견을 모아 구단 운영에 반영하는 과정이 투명하게 이루어져, 팬들이 단순 관람을 넘어 직접 구단 운영에 참여하는 경험을 제공합니다.

![기능4 이미지1](https://github.com/user-attachments/assets/98404e9d-4e77-443e-ae2f-654c97c4cd63)

<br>

### 5. 마이페이지
**개인 거래 및 활동 관리**
- 팬들은 자신의 지갑 주소, 거래 내역, 트랜잭션 정보를 한눈에 확인할 수 있어, 모든 활동이 투명하게 관리됩니다.

**통합 활동 기록**
- 플랫폼 내에서의 모든 활동(거래, 이벤트 참여, 팀 채널 활동 등)이 마이페이지에 기록되어, 팬들이 자신의 참여 내역과 성과를 쉽게 추적할 수 있습니다.

![기능5 이미지1](https://github.com/user-attachments/assets/ce9f00e5-7ce8-49c1-ae17-a98ce413f9bd)
![기능5 이미지2](https://github.com/user-attachments/assets/c65d9324-4db9-4d1a-91ab-693850f6e9c7)

<br>

### 6. 지갑 소셜 로그인 (Web3Auth)
**Google Oauth와 Web3Auth를 활용한 지갑 로그인**
- MetaMask 설치나 별도 지갑 생성 없이, 단 한 번의 Google 계정 인증만으로 지갑이 자동 생성·연결됩니다.

![기능6 이미지1](https://github.com/user-attachments/assets/4d1a1075-8d02-4c36-becd-cbdde801770d)

<br>

## 🛠 기술적 챌린지

### 챌린지 1: 프라이빗 네트워크 구축 과정
**메인넷 → 테스트넷 → 프라이빗 네트워크 전환**
> Ethereum 메인넷을 사용했지만, 네트워크가 과도하게 혼잡해 가스비가 평균 몇십 달러에 달했고(거래 수수료 급등), 트랜잭션 확정에도 수십 분 이상 걸리는 등 운영 비용과 사용자 경험 면에서 치명적 한계를 보임
> 이에 수수료가 저렴하고 블록 생성 속도가 빠른 Polygon Edge로 전환했으나, 곧 프로젝트 지원 종료가 결정되어 공식적으로 유지보수가 중단되었고 네트워크 안정성도 보장할 수 없다는 문제가 발생함
> 최종적으로 Geth 기반 자체 프라이빗 네트워크를 구축하여, 합의 알고리즘, 가스비 등을 직접 설정하고 빠른 처리 속도를 보장할 수 있게 함

### 챌린지 2: 토큰 경제 시스템 설계 (거래소, 수요와 공급)
> 사용자 기준에서 “금액 입력 → 예상 가격 확인 → 스왑 실행” 등의 흐름을 컨트랙트 설계, 백엔드 연동, 프론트 UX까지 모두 정렬하여 완성시킴.

    - 모델 정의
        
        AMM의 x*y=k 원리를 그래프와 예시 중심으로 정리해 팀 내 공유.
        사용자 경험 관점에서도 직관적 가격 변동이 일어나도록 구조화.
        
    - 유동성 분배 정책
        
        각 팀 팬 토큰의 초기 공급량을 동일하게 설정하고,
        RewardPool 수익의 10%를 각 팀 풀에 분배하는 구조로 보상 메커니즘 확장.
        → 이를 통해 스왑 사용이 생태계 전체에 이득이 되도록 설계.
        
    - 실시간 가격 반영 구조
        
        Redis 캐싱 없이 스마트 컨트랙트 상태에서 직접 reserve 값을 읽어
        가격 계산 및 예상 금액 반영.
        → 온체인 데이터를 기준으로 가격 반영 신뢰도 확보.
        
    - 버전 이슈 및 권한 관리 처리
        
        초기 컨트랙트 설계 시 `IERC20Burnable`, `Ownable`, `SafeERC20` 등
        권한 제어 및 토큰 소각 관련 개념을 새로 학습하고 적용.
        Foundry 배포 시 발생한 Solidity 버전 충돌, openzeppelin dependency 오류 등을
        직접 디버깅하여 정상 배포까지 완료.

### 챌린지 3: 스마트 컨트랙트 작성 및 테스트
> 스마트 컨트랙트의 로직 복잡도가 높아지면서, 단순한 전송이나 밸런스 체크를 넘어 다음과 같은 요구사항을 만족해야 했음:
>
> - 토큰 간 스왑 로직 (`add/remove/buy/sell/swap`)
> - 보상풀 분배 및 회수 로직 (`RewardPool`, `LoserPool`)
> - 컨트랙트 상태 기반으로 스왑 조건 검증 및 잔액 체크
> - 트랜잭션 실패 시 사용자 자산 보호를 위한 예외 처리
>
> 이에 따라 다음과 같은 개발 전략을 적용함:
>
> - **Foundry 기반 유닛 테스트 환경 구성:** 실제 스왑/예치/회수 상황을 시나리오 단위로 구현하여 컨트랙트 안정성 검증
> - **Mock Contract 및 테스트용 토큰 생성:** 각종 인터랙션을 시뮬레이션할 수 있도록 가짜 컨트랙트와 토큰 배포
> - **BC-BE 연동 검증:** `.abi`, `.bin`, Web3j wrapper를 기준으로 컨트랙트 배포 후 연동 테스트 수행, 상태 조회와 예외 응답 검증 포함
>
> 이러한 테스트 기반 접근을 통해 안정적인 컨트랙트 배포와 운영이 가능해졌고, 버그 및 논리 오류 발생률을 사전에 크게 줄일 수 있었음.
>
<br>

## ⚙️ 기술 스택

<table>
    <thead>
        <tr>
            <th>분류</th>
            <th>기술 스택</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><p>백엔드</p></td>
            <td>
                <img src="https://img.shields.io/badge/Java-007396?logo=java&logoColor=white"/>
                <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white"/>
                <img src="https://img.shields.io/badge/Selenium-43B02A?logo=selenium&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td><p>DB</p></td>
            <td>
                <img src="https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white"/>
                <img src="https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td><p>블록체인</p></td>
            <td>
                <img src="https://img.shields.io/badge/Web3j-8A2BE2?logo=ethereum&logoColor=white"/>
                <img src="https://img.shields.io/badge/Web3Auth-0A0A0A?logo=web3auth&logoColor=white"/>
                <img src="https://img.shields.io/badge/Ethers.js-4F1B16?logo=ethers&logoColor=white"/>
                <img src="https://img.shields.io/badge/Geth-000000?logo=ethereum&logoColor=white"/>
                <img src="https://img.shields.io/badge/Foundry-3E3E3E?logo=foundry&logoColor=white"/>
                <img src="https://img.shields.io/badge/Solidity-363636?logo=solidity&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td><p>프론트엔드</p></td>
            <td>
                <img src="https://img.shields.io/badge/React-61DAFB?logo=react&logoColor=white"/>
                <img src="https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white"/>
                <img src="https://img.shields.io/badge/Zustand-2848FF?logo=zustand&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td><p>협업</p></td>
            <td>
                <img src="https://img.shields.io/badge/Git-F05032?logo=git&logoColor=white"/>
                <img src="https://img.shields.io/badge/GitLab-FCA121?logo=gitlab&logoColor=white"/>
                <img src="https://img.shields.io/badge/Jira-0052CC?logo=jira&logoColor=white"/>
                <img src="https://img.shields.io/badge/Notion-000000?logo=notion&logoColor=white"/>
                <img src="https://img.shields.io/badge/Figma-F24E1E?logo=figma&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td><p>인프라</p></td>
            <td>
                <img src="https://img.shields.io/badge/Jenkins-D24939?logo=jenkins&logoColor=white"/>
                <img src="https://img.shields.io/badge/NGINX-009639?logo=nginx&logoColor=white"/>
                <img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white"/>
                <img src="https://img.shields.io/badge/EC2-232F3E?logo=amazonec2&logoColor=white"/>
                <img src="https://img.shields.io/badge/S3-569A31?logo=amazons3&logoColor=white"/>
            </td>
        </tr>
    </tbody>
</table>


<br />

<br>

## 📜 프로젝트 산출물

### 시스템 아키텍처
![시스템 아키텍처](https://github.com/user-attachments/assets/e316ee77-a489-46ef-ad0c-bdae45e6810c)

### ERD
[🔗 ERD 링크](https://www.erdcloud.com/d/f9Pkch87MAqGJRsPN)

### API 명세서
[🔗 Swagger 링크](https://j12a609.p.ssafy.io/swagger-ui/index.html)

### 와이어프레임
[🔗 와이어프레임](https://www.figma.com/design/365mhCQRfWrpdos8sXHTu9/%ED%8A%B9%ED%99%94%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8?node-id=412-7460&p=f&t=uLrGmKzdOYew5s9h-0)

<br>

