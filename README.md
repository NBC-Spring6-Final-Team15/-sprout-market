# 🌱  Welcome to SproutMarket!

<div style="background-color: #f9f9f9; padding: 10px; ">
    <strong>새싹마켓</strong>은 지역 기반에 중고 거래 플랫폼으로써 작은 실천으로 시작하는 지속 가능한 미래를 위해 만들었습니다.
</div>




<br>

## ✨ 프로젝트 핵심 목표
1. **API 문서 자동화**

2. **성능 최적화**

3. **운영 및 배포 효율화**
    - Docker와 Github Actions를 이용한 CI/CD 파이프라인 구축으로 배포 자동화.

## ✨  KEY SUMMARY(예시)
##### 🍁 **성능 개선 : 최저가 상품 조회 성능, Redis 도입으로 3배 이상 향상**

1. **한 줄 요약**
    - Redis 도입으로 기존 DB 조회보다 **348% 성능 개선**
    - 대규모 트래픽 환경에서도 안정적인 서비스 유지

   ![성능 개선 이미지]

2. **도입 배경**
    - 상품의 최저가를 제공하기 위해 외부 서버에서 제공하는 타임세일 상품의 할인율과  
      상품 자체의 할인율을 비교하는 기능이 필요

3. **기술적 선택지**

    1. **DB 데이터 적재**
        - 스케줄링 작업으로 짧은 시간 내 대량의 데이터를 수정하는 것은 데이터베이스에 과도한 부하 발생
        - 상품 자체의 할인율과 타임세일 할인율을 분리하여 별도 컬럼 저장 필요

    2. **Redis 캐싱**
        - 실시간 최저가 할인율로 최신 정보와 가격 제공
        - TTL 설정으로 타임세일 종료 시 자동 데이터 삭제

   **결론:** Redis 도입을 결정하여 성능 및 효율성을 크게 개선  
   <br>
   <br>

## 트러블 슈팅

<details><summary>🍁 LazyConnectionDataSourceProxy - 불필요한 커넥션 점유 해결(예시)
</summary>
1. **배경**  
   - **스프링 배치 5버전 도입**  
     - 정산은 실시간이 아닌, 이용자가 적은 시간에 일괄 처리하도록 배치 선택  
     - 메인 DB와 배치 메타데이터 DB 분리 필요  
   - **배치 메타데이터 테이블 생성 필수화**  
     - 메타데이터 전용 DB를 나누는 구조로 전환  
   - **멀티 DataSource 구성**  
     - 메인 DataSource와 배치 DataSource로 데이터베이스 모듈 구분  

2. **문제**
    - 실제 DB 요청 없이도 불필요한 커넥션 점유 발생
        - 스프링은 트랜잭션 진입 시 커넥션 풀에서 커넥션을 점유
        - 멀티 DataSource로 인해 두 DataSource 모두 커넥션 점유

3. **해결 방안**
    - **LazyConnectionDataSourceProxy 클래스 사용**
        - 실제 DB 요청 전까지 커넥션 점유를 지연시키는 프록시 DataSource 활용

   ![LazyConnectionDataSourceProxy 이미지]

    - 이를 통해 **실제 DB 요청 시에만 커넥션 점유**로 불필요한 리소스 낭비를 해결
</details>

<details><summary> 주변 행정구역 리스트 조회 쿼리 캐싱 </summary>
1. **배경**
    -어떤 유저가 매물을 조회할 때마다 

</details>



<br>



## ✨ 주요 기능 설명
✅ 회원가입 할 때 입력된 주소를 기반으로, 주변 매물을 검색하고, 판매자와 실시간으로 소통할 수 있습니다.

✅ 물건 설명, 가격을 작성하고 사진을 첨부하여 매물을 등록할 수 있습니다.

✅ 거래가 완료되면 리뷰를 작성해 평판을 올리거나 내려서, 신뢰도를 쌓을 수 있습니다.

✅ 관심이 있는 매물이나, 카테고리가 있다면, 알림 설정을 해서 알림을 받아볼 수 있습니다. (예시 : 관심 등록한 매물의 가격이 변동되었을 경우)

✅ 관리자로 임명된 분들은 카테고리를 관리하고, 부적절한 매물을 삭제할 수 있습니다.


<br>
<br>

## ✨ 기술적 고도화(예시)

<details>
<summary><b>🍁 분산락 Redisson 도입으로 CPU 점유율 2배 개선(예시)</b></summary>

### 왜 동시성 제어 시 여러 선택지가 있는데, 분산락을 사용했을까요?
---
#### 낙관적 락과 비관적 락의 선택지
분산락을 채택하기 이전에는 비관적 락으로 동시성 제어를 선택했습니다.
- **비관적 락**  
  비관적 락으로 데이터를 조회하면 해당 트랜잭션이 끝나기 전까지는 데이터에 대한 Insert 작업이 불가능합니다.
    - 단점: 트래픽이 많은 경우 성능 저하 발생 및 타임아웃 문제.
- **낙관적 락**  
  낙관적 락은 충돌 발생 시 롤백 처리를 요구하며, 충돌 비용이 높습니다.
    - 단점: CPU 점유율이 상승하고, 예상치 못한 오류 발생 가능.
---
#### Redis로 분산락을 채택한 이유
1. **Lettuce의 문제점**  
   Lettuce는 스핀락 방식을 사용하여 락이 풀릴 때까지 계속 Redis에 요청을 보냅니다.
    - 결과적으로 Redis CPU 점유율이 높아지는 문제가 발생.
2. **Redisson의 장점**  
   Redisson은 Pub-Sub 구조로 락이 종료될 때 이벤트를 발행하며, 락 요청을 효율적으로 처리합니다.
    - 결과적으로 Redis CPU 점유율이 낮아집니다.
---
### 적용 후
- **CPU 점유율:** 기존 60% → 30% 감소
- **TPS:** 기존 1400 → 2500으로 향상
</details>


<br>
<br>

## ✨ 인프라 아키텍처 & 적용 기술

<img src="https://camo.githubusercontent.com/a939a97343b526155a402189c6327f5a82a4d74d61daa9794127e4d7fdd9a757/68747470733a2f2f63646e2e646973636f72646170702e636f6d2f6174746163686d656e74732f313236323933353736323438343036383430352f313330333535363433393834323838313635372f556e7469746c65642e706e673f65783d36373263326630612669733d363732616464386126686d3d3065383662616366613633333163633838386134366636346538653733303765313434393533656665303934363031316261383466616539333237326364626626">


### ✨ Backend

<img src="https://img.shields.io/badge/JDK-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/Spring Boot-3.3.4-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
<img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white">

<img src="https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=Redis&logoColor=white">

### ✨ FrontEnd Test
<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white">
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">

### ✨ Infra & CI/CD
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white">
<img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">
<img src="https://img.shields.io/badge/Amazon ElastiCache-C925D1?style=for-the-badge&logo=Amazon ElastiCache&logoColor=white">
<img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=Amazon RDS&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/Portainer-13BEF9?style=for-the-badge&logo=Portainer&logoColor=white">
<img src="https://img.shields.io/badge/Github Actions-2088FF?style=for-the-badge&logo=Github Actions&logoColor=white">


### ✨ API Docs

<img src="https://img.shields.io/badge/OpenAPI Initiative-6BA539?style=for-the-badge&logo=OpenAPI Initiative&logoColor=white">
<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">


###  ✨ 협업, 프로젝트 매니징 툴
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">
<img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white">
<img src="https://img.shields.io/badge/Trello-0052CC?style=for-the-badge&logo=Trello&logoColor=white">
<img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white">
<br><br>

### 의사 결정
<details><summary> Docker
</summary>
 &emsp;✔️ 적용 위치: 모든 서비스 컨테이너화 <br>
 &emsp;✔️ 사용 이유: 환경 이식성과 배포 속도 개선.  
</details>

<details><summary> Github Actions
</summary>
 &emsp;✔️ CI/CD 파이프라인  <br>
 &emsp;✔️ 사용 이유: 자동화된 코드 품질 검사와 배포 구현.  
</details>

<br>
<br>


## ✨ Hello Introduce US

|신승재|김기혜|장기현|이지택|양혜민|
|:----:|:----:|:----:|:----:|:----:|
|![enter image description here](https://avatars.githubusercontent.com/u/147094944?v=4)%7C![enter image description here](https://avatars.githubusercontent.com/u/150889625?v=4)%7C![enter image description here](https://avatars.githubusercontent.com/u/109169177?v=4)%7C![enter image description here](https://teamsparta.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F0ebe0603-a6a3-4be2-8deb-061448070a66%2FUntitled.jpeg?table=block&id=d7bf83a8-c4ea-46b8-b8f8-51984c3c63c9&spaceId=83c75a39-3aba-4ba4-a792-7aefe4b07895&width=670&userId=&cache=v2)%7C![enter image description here](https://ca.slack-edge.com/T06B9PCLY1E-U074B44JZN0-0d66bbf845ed-192)%7C
|[@durururuk](https://github.com/durururuk)%7C[@kikye04040](https://github.com/kikye04040)%7C[@EtherXion](https://github.com/EtherXion)%7C[@jitaeklee](https://github.com/jitaeklee)%7C[@asitwas729](https://github.com/asitwas729)%7C
| CI/CD<br>Area<br>ELK Stack<br>Category  | Kakao Login<br>User<br>Alert<br>RabbitMQ | Chatting<br> | Trade<br>Review<br>Report<br>Popular Item<br>Coupon | Item<br>Image |



##### [💚 Let's Go Our GitHub](https://github.com/NBC-Spring6-Final-Team15/sprout-market)

##### [💚 Let's Go Our Notion](https://teamsparta.notion.site/15-15-aac3459b7971408392231a60149bcb9f)

##### [💚 Let's Go Our Github Rules](https://teamsparta.notion.site/Github-Rules-010c40cb458947e8ba9ac1483f7c0871)

##### [💚 Let's Go Our Code Convention](https://teamsparta.notion.site/Code-Convention-435a94ebb5a94dcc9cfda16a434d6846)

<details><summary>
기타 자료
</summary>

### ERD
<img src="https://github.com/user-attachments/assets/ceb2e667-73d4-4b23-a53f-c17663cba43e">

### API 문서
http://43.203.87.214:8081/

### 자료 출처
행정구역 geoJson :  https://github.com/vuski/admdongkor

### 프로젝트 구조

<details><summary> 프로젝트 구조
</summary>

*Write here!*
</details>

</details>
<br>

## ✨ 추후 목표