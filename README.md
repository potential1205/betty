# ⚾ 블록체인 기반 팬 주도형 프로야구 플랫폼 『 BETTY 』

### 🔖 소개
Betty는 블록체인 기반의 투명한 토큰 경제 시스템으로 팬들이 실시간 소통하고 구단 의사결정에 직접 참여할 수 있는 새로운 프로야구 생태계를 제공합니다.
<br>

## 🔗 프로젝트 링크

**[Betty](https://j12a609.p.ssafy.io/)**

<br>

## 📆 프로젝트 정보
**기간**: 2025.02.24 ~ 2025.04.11 (7주)  
**인원**: 6명

<br>

## 🧡 팀 소개

|                                      이재훈                                      |                                      김도희                                      |                                      예세림                                      |                                      이성희                                      |                                      이유나                                      |                                      이주호                                      |
|:-----------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|
| <img src="https://avatars.githubusercontent.com/u/62222791?v=4" width="120"/> | <img src="https://avatars.githubusercontent.com/u/62222791?v=4" width="120"/> | <img src="https://avatars.githubusercontent.com/u/62222791?v=4" width="120"/> | <img src="https://avatars.githubusercontent.com/u/62222791?v=4" width="120"/> | <img src="https://avatars.githubusercontent.com/u/62222791?v=4" width="120"/> | <img src="https://avatars.githubusercontent.com/u/84345021?v=4" width="120"/> |
|                                 **팀장, Infra**                                 |                                   **Front**                                   |                                   **Back**                                    |                                   **Back**                                    |                                   **Front**                                   |                                 **Frontend**                                  |
|              [@potential1205](https://github.com/potential1205)               |              [@potential1205](https://github.com/potential1205)               |              [@potential1205](https://github.com/potential1205)               |              [@potential1205](https://github.com/potential1205)               |              [@potential1205](https://github.com/potential1205)               |              [@potential1205](https://github.com/potential1205)               |

| Contributors | Role                | Position                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|--------------|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 이재훈          | 팀장, <br /> Backend  | - **인프라 구성 및 관리:** AWS(EC2, S3), Docker, Jenkins, NGINX를 이용한 웹 서비스 인프라 설계·운영<br> - **CI/CD 자동 배포:** Jenkins와 GitLab Webhook, Docker Compose 기반 자동 배포 파이프라인 구축<br> - **프라이빗 블록체인 네트워크 구축 (협업):** Geth를 활용한 전용 블록체인 네트워크 환경 설계 및 구축<br> - **전광판 실시간 소켓 통신:** Spring WebSocket(STOMP), SimpMessagingTemplate을 이용한 전광판 데이터 송수신 기능 구현<br> - **전광판 접근 권한 검증:** Web3j 기반 토큰 검증 로직으로 사용자별 접근 제어 구현<br> - **전광판 데이터 조회 API:** Spring Data Redis 및 JPA를 활용한 실시간 전광판 데이터 조회 및 캐싱 기능 개발<br> - **인증 시스템 (로그인·회원가입):** Web3Auth 연동으로 지갑 기반 로그인·회원가입 기능 구현<br> - **팀 채널 투표 시스템:** Solidity 스마트 컨트랙트와 Web3j 연동을 통해 안건 등록·투표(토큰 이동) 로직 구현 |
| 김도희          | 팀원, <br /> Frontend | - |
| 예세림          | 팀원, <br /> Backend  | - |
| 이성희          | 팀원, <br /> Backend  | - |
| 이유나          | 팀원, <br /> Frontend | - |
| 이주호          | 팀원, <br /> Frontend | - |

<br>

## 💡 기획 의도 및 배경
> 우리 서비스는 블록체인의 투명성과 불변성, 분산 거버넌스를 바탕으로 ‘팬이 팀의 진정한 주인’이 되는 프로야구 문화를 구현하고자 기획되었습니다. 팬 토큰 거래와 구단 운영 관련 투표, 전광판 픽셀 아트 기록 등이 모두 블록체인에 영구 저장되기 때문에, 팬들은 자신의 참여가 언제나 검증 가능하고 왜곡 없이 보존된다는 확신을 얻습니다. 이를 통해 단순 관람을 넘어 구단 의사결정 과정에 직접 목소리를 내고, 그 결과를 실시간으로 확인하면서 팬과 구단 간의 소통 창구가 완전히 분산된 형태로 재정립됩니다. 또한, 경기 중 코인으로 그린 전광판 픽셀 아트와 이벤트 참여 기록은 NFT로 전환해 교환·수집·전시함으로써 디지털 자산으로서의 팬 경험 가치를 한층 확장합니다. 마지막으로 마이페이지에서 나의 지갑과 거래 내역, 참여 이력을 모두 투명하게 관리할 수 있어, 팬들은 자신이 플랫폼에 쌓아온 모든 순간을 자랑거리로 돌아보고 언제든 재확인할 수 있습니다. 이렇듯 블록체인의 핵심 특성을 통해 팬 중심의 프로야구 생태계를 구축함으로써, 팬들이 함께 만들고 함께 누리는 진정한 ‘팬 주도형 스포츠 문화’를 실현합니다.

<br>

## 🚀 핵심 기능 요약
1. **팬 토큰 거래소**: 핵심 기능에 대한 간략한 설명
2. **실시간 이벤트 & 전광판 그리기**: 핵심 기능에 대한 간략한 설명
3. **팀 채널 & 의사결정 안건 제안 및 투표**: 핵심 기능에 대한 간략한 설명
4. **마이페이지**: 핵심 기능에 대한 간략한 설명

<br>

## ✨ 핵심 기능 상세

### 1. 팬 토큰 거래소
- 실시간 가격 변동
  AMM 알고리즘을 활용하여 팬 토큰의 가격이 팬들의 거래 활동에 따라 실시간으로 결정

- 구단별 맞춤형 경제 시스템
  각 구단의 팬 토큰 가격은 해당 팀 팬들의 활동과 수요에 따라 다르게 형성되어, 팬들이 구단에 대한 애정과 참여도를 직접적으로 반영

- 다양한 거래 옵션
  팬들은 단순 토큰 환전뿐 아니라, 팬 토큰을 사용해 구단의 의사결정에 참여할 수 있고 실시간 전광판 그리기에도 참여할 수 있고 플랫폼 내 다양한 상품(굿즈 등)을 구매할 수 있도록 확장할 수 있음

![기능1 이미지](https://github.com/user-attachments/assets/ab2f8d7b-a1a8-45ff-b903-0ca8adacd5e5)

<br>

### 2. 실시간 야구 중계 연동 퀴즈

- 경기 상황 연동 이벤트
  실시간 야구 중계 정보와 연계된 이벤트 및 퀴즈를 통해 팬들이 경기 상황을 더욱 몰입감 있게 즐길 수 있음

- 참여 기반 전광판 그리기
  팬들이 코인을 소모하며 전광판에 그림을 그리게 하여, 팬들의 창의적 참여와 경쟁을 유도하고 일정 이닝마다 전광판 그리기의 결과가 이미지로 저장되어 경기를 대표하는 픽셀 아트가 아카이브에 저장함

![기능2 이미지](https://github.com/user-attachments/assets/91ddbb9b-edcf-4047-aa00-b242756c365f)

<br>

### 3. 우승 팀, MVP 예측
경기 상황 연동 이벤트
- 실시간 야구 중계 정보와 연계된 이벤트 및 퀴즈를 통해 팬들이 경기 상황을 더욱 몰입감 있게 즐길 수 있음

참여 기반 전광판 그리기
- 팬들이 코인을 소모하며 전광판에 그림을 그리게 하여, 팬들의 창의적 참여와 경쟁을 유도하고 일정 이닝마다 전광판 그리기의 결과가 이미지로 저장되어 경기를 대표하는 픽셀 아트가 아카이브에 저장함

![기능2 이미지](https://github.com/user-attachments/assets/91ddbb9b-edcf-4047-aa00-b242756c365f)

<br>

### 4. 팀 채널 & 의사결정 안건 제안 및 투표
![기능3 이미지](기능3_이미지.gif)
> 세 번째 핵심 기능에 대한 상세 설명을 작성해주세요.

<br>

### 5. 마이페이지
![기능4 이미지](기능4_이미지.gif)
- 개인 거래 및 활동 관리
    팬들은 자신의 지갑 주소, 거래 내역, 트랜잭션 정보를 한눈에 확인할 수 있어, 모든 활동이 투명하게 관리됩니다.
통합 활동 기록
플랫폼 내에서의 모든 활동(거래, 이벤트 참여, 팀 채널 활동 등)이 마이페이지에 기록되어, 팬들이 자신의 참여 내역과 성과를 쉽게 추적할 수 있습니다.

<br>

### 6. 지갑 소셜 로그인 (Web3Auth)
- Google Oauth와 Web3Auth를 활용한 지갑 로그인
  기존 MetaMask 지갑 로그인과 다르게 사용자의 번거로움을 덜어줌

![기능1 이미지](https://github.com/user-attachments/assets/4d1a1075-8d02-4c36-becd-cbdde801770d)

<br>

## 🛠 기술적 챌린지

### 챌린지 1: 프라이빗 네트워크 구축 과정
**메인넷 → 테스트넷 → 프라이빗 네트워크 전환** 
> Ethereum 메인넷을 사용했지만, 네트워크가 과도하게 혼잡해 가스비가 평균 몇십 달러에 달했고(거래 수수료 급등), 트랜잭션 확정에도 수십 분 이상 걸리는 등 운영 비용과 사용자 경험 면에서 치명적 한계를 보임
> 이에 수수료가 저렴하고 블록 생성 속도가 빠른 Polygon Edge로 전환했으나, 곧 프로젝트 지원 종료가 결정되어 공식적으로 유지보수가 중단되었고 네트워크 안정성도 보장할 수 없다는 문제가 발생함
> 최종적으로 Geth 기반 자체 프라이빗 네트워크를 구축하여, 합의 알고리즘, 가스비 등을 직접 설정하고 빠른 처리 속도를 보장할 수 있게 함

### 챌린지 2: 토큰 경제 시스템 설계 (거래소, 수요와 공급)
> 프로젝트 진행 중 마주친 두 번째 기술적 어려움과 이를 어떻게 해결했는지 작성해주세요.

### 챌린지 3: 기술적 도전 제목 
> 프로젝트 진행 중 마주친 세 번째 기술적 어려움과 이를 어떻게 해결했는지 작성해주세요.

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
![시스템 아키텍처](아키텍처_이미지.png)

### ERD
![ERD](ERD_이미지.png)

### API 명세서
[🔗 API 문서 링크](API_문서_링크)

### 기타 문서
[🔗 팀 노션](팀_노션_링크)  
[🔗 와이어프레임](와이어프레임_링크)

<br>

