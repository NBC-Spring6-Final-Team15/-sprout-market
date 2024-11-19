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

## ✨  KEY SUMMARY

<details><summary>쿠폰 발급시 Redisson 락을 통한 동시성 제어와 tryLock을 통한 응답 속도 향상</summary>

### 배경<br>
선착순 쿠폰 발급 이벤트를 가정하여 동시성 문제 해결

### 문제<br>
1.짧은 시간 동안 트래픽이 몰릴 때 발생하는 동시성 문제<br>
2.모든 요청에 락을 걸어 요청이 몰렸을 때 응답 속도가 지연되는 문제<br>

### 해결 방안<br>
**동시성 문제 해결**<br>
Reddison 락을 사용해 동시성 문제 해결

테스트 설정<br>
쿠폰 발급 개수 : 100
JMeter를 사용해 10초 동안 3000개의 요청을 보냄

락 사용 전 : 102개의 쿠폰이 발급됨

<img src = "https://media.discordapp.net/attachments/1262935762484068405/1308304950723346452/E18489E185B3E1848FE185B3E18485E185B5E186ABE18489E185A3E186BA_2024-11-15_E1848BE185A9E18492E185AE_2.24.43.png?ex=673d756f&is=673c23ef&hm=863107e890433615978a331e0103d7db64c2f7d2b7b2f2133f4cb6da85a44f57&=&format=webp&quality=lossless"> <br>

락 사용 후 : 정확히 100개의 쿠폰이 발급됨

<img src = "https://media.discordapp.net/attachments/1262935762484068405/1308304951038185524/E18489E185B3E1848FE185B3E18485E185B5E186ABE18489E185A3E186BA_2024-11-15_E1848BE185A9E18492E185AE_2.27.24.png?ex=673d756f&is=673c23ef&hm=dd7f96b7a35058127e22ec141d41813b3a92d97cd1e502eb8026ca91d137e254&=&format=webp&quality=lossless">

**응답 속도 지연 문제 해결**

lock.tryLock으로 락 대기 시간을 줄여 응답 속도 향상

테스트 설정<br>
쿠폰 발급 개수 : 100
JMeter를 사용해 10초 동안 3000개의 요청을 보냄

tryLock 사용 전 : 평균 응답 속도 1025ms<br>

<img src = "https://media.discordapp.net/attachments/1262935762484068405/1308304949947404318/0f10256b-cfce-4acd-aa75-ee736ebc90bd.png?ex=673d756f&is=673c23ef&hm=47898f651580325d7ddff8aec95b286150d44227fdcf7b9217c3ec3475968396&=&format=webp&quality=lossless"><br>

tryLock 사용 후 : 평균 응답 속도 73ms (약 92% 응답 속도 개선)<br>

<img src = "https://media.discordapp.net/attachments/1262935762484068405/1308304950232743936/673a06b6-bc98-46d8-a792-2c43d0a4b594.png?ex=673d756f&is=673c23ef&hm=8f62b7d8458d62dcb885348799a7ca5b709e0d233d8b8ff322cd519cd68e07e3&=&format=webp&quality=lossless"> 
</details>



## 트러블 슈팅
<details><summary> 주변 행정구역 리스트 조회 쿼리 캐싱 </summary>

### 배경
사용자가 매물을 조회할 때 주변 행정구역 리스트를 반환받아야 하는 쿼리를 DB에 날려야함

### 문제
특정 포인트로부터 일정한 거리만큼 떨어진 공간 연산 쿼리이기 때문에 사용자가 몰리면 DB에 부하가 많이 걸리는 문제가 있음


### 해결방안
해당 쿼리 결과를 캐싱해서 같은 요청이 들어올 시 캐시된 데이터를 응답하도록 시도함

**장점**
- 인메모리 캐시를 사용하기 때문에 응답시간이 빠름.
- 요청에 대해 MySQL에서 연산을 하지 않기 때문에 아무리 사용자가 몰려도 DB에 부하가 없음

### 실행 결과
3554개의 행정구역의 모든 경우의 수에서 캐싱한 결과 <br>
사용한 메모리 : 4.65MB <br>
캐싱 이전 쿼리 시간 : 평균 92ms <br>
캐싱 이후 쿼리 시간 : 평균 9ms (1022% 속도 향상) <br>
</details> <br>


<details><summary>배포 사이트에서 브라우저가 위치 확인을 막아버리는 문제
</summary>

### 배경 & 문제
회원가입을 할 때 현재 위치 확인 버튼을 누르면 사용자의 현재 위치를 가져오는데, 로컬 환경에서 [localhost:8080](http://localhost:8080) 식으로 들어갔을 때에는 브라우저가 위치를 잘 가져다줬는데, ec2에 배포한 후 들어가보니 작동을 하지 않았다.

크롬이 무언가 잘못되었나 생각해서 엣지, 파이어폭스 등 다른 브라우저로도 시도해보았지만 똑같이 로컬에선 되고, 배포 환경에서는 작동하지 않았다.

코드가 잘못되었으면 로컬에서도 안 됐을 것이니 코드는 문제가 없을 것이라고 생각하고, 뭔가 다른 이유를 찾다보니, 비 보안 연결(http)에서는 브라우저단에서 위치같은 정보를 서버에 넘겨주지 않는다는 것이었다.

로컬 환경에서는(localhost8080) 개발 편의를 위해 브라우저에서 딱히 막지 않는다고 한다.

### 해결

배포 환경에 무료 도메인을 발급받고, ssl 인증을 받은 후, 보안 연결(https)로 다시 시도해보니 배포환경에서도 작동한다는 것을 확인했다.
</details>




## ✨ 주요 기능 설명
✅ 회원가입 할 때 입력된 주소를 기반으로, 주변 매물을 검색하고, 판매자와 실시간으로 소통할 수 있습니다.

✅ 물건 설명, 가격을 작성하고 사진을 첨부하여 매물을 등록할 수 있습니다.

✅ 거래가 완료되면 리뷰를 작성해 평판을 올리거나 내려서, 신뢰도를 쌓을 수 있습니다.

✅ 관심이 있는 매물이나, 카테고리가 있다면, 알림 설정을 해서 알림을 받아볼 수 있습니다. (예시 : 관심 등록한 매물의 가격이 변동되었을 경우)

✅ 관리자로 임명된 분들은 카테고리를 관리하고, 부적절한 매물을 삭제할 수 있습니다.


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
<details><summary> Github Actions로 CI/CD 구축
</summary>

### Jenkins 대신에 Github Actions를 사용한 이유

**Jenkins**<br>
다양한 플러그인을 지원하고 온프레미스 도구이기 때문에 보안에 유리함<br>
직접 서버를 구축 해줘야 하고, 어느정도 자원을 차지하기 때문에 운용 비용이 발생한다.

**Github Actions**<br>
깃허브 레포지토리의 이벤트 기반으로 파이프라인을 가동하기 쉽고, 다양한 워크플로우 템플릿이 있음.<br>
따로 서버를 구축해주지 않아도 사용 가능함. <br>
퍼블릭 레포지토리의 경우 무제한으로 무료 사용 가능.<br>
워크 플로우 파일이 외부에 노출이 되기 때문에 취약점에 노출되기 쉬움, 소스 코드를 외부(깃허브)에 업로드 해야함.

우리 프로젝트의 경우 Jenkins 서버를 구축할만큼 예산이 없었고, 항상 public으로 레포지토리를 관리하기 때문에 구축이 간편하고, 무료로 사용할 수 있는
Github Actions를 사용하였다.
</details><br>

<details><summary> 통계청에서 제공하는 행정구역 공간 정보를 활용하여 위치 기반 서비스 구현
</summary>

### 통계청에서 제공하는 행정구역 공간 정보를 활용하여 구현

지역 기반 중고 거래 플랫폼이기 때문에 위치 기반 서비스를 제공할 필요가 있었다.<br>
좌표를 주소로 변환해주는 지오코딩 API를 사용하는 방법과 프로젝트 DB에 행정 구역 공간 정보를 넣어서 직접 조회하는 방식을 고민했어야 했다.
<br><br>
지오코딩 API의 경우 국도교통부, 네이버 지도, 카카오 지도, 구글 맵스, 티맵 등 여러 서비스 업체들이 제공해줌.<br>
무료 API 사용량이 제한되어 있으며, 반환받은 값을 DB에 저장하면 안 됨. <br>
그리고 우리는 범위 검색이 필요하기 때문에 어떤 포인트를 요청해서 포인트를 반환받는 API 특성상 구현 난이도가 높아질 것으로 예상됨.
또한 프로젝트 특성 상 동네라는 개념이 중요한데, 선택한 API 서비스에 장애가 발생하면 우리 프로젝트에 치명적임.<br><br>
이런 단점으로 통계청에서 제공하는 행정동 경계 정보를 받아서 그대로 DB에 저장 후 필요할 때마다 조회해서 사용하는 방식으로 결정했다.
<br><br>
</details>

<br>
<details><summary> Spring Rest Docs + Swagger UI 를 사용하여 API 문서 자동화
</summary>

### API 문서 작성 시기

API 문서를 작성하는 방식을 개발 전에 정리를 하고 시작 할지, 개발 후에 자동화 도구를 활용해서 작성할 것인지에 대해 정해야 했다.

개발 전에 API 문서를 작성하면 팀원 모두가 같은 문서를 보고 응답 흐름을 계속 상기하면서 개발할 수 있음. 하지만 계획과 다르게 개발 도중 수정이 됐는데 
문서에 반영을 안 했을 수도 있음. 

또한 버그나 이해도 문제로 실제와 다른 응답이 나갈 수도 있음.

하지만 개발이 끝나고 자동화 도구를 활용해서 문서화를 하면 실제 응답과 일치하는 API 문서를 작성할 수 있음.

실제 응답 기반 문서화라는 것이 엄청난 장점이라고 생각돼서 기능 개발 후 자동화 도구를 활용하여 API 문서를 만들기로 결정함.

### API 문서 자동화 도구 선택

Swagger VS Spring Rest Docs

**Swagger**

- UI가 이쁨.
- Try It Out 기능을 활용하여 실제로 서버에 요청을 날려보고 제대로 응답이 반환되는지 확인할 수 있음. 
- 컨트롤러 레이어에 문서화 관련 코드가 추가로 들어감

**Spring Rest Docs**

- 테스트 코드 기반으로 문서화를 시켜줄 수 있음. 
- UI가 별로 이쁘지 않음.

Swagger의 경우 컨트롤러 레이어에 문서화 관련 코드가 추가로 들어간다는 점이 단일 책임 원칙을 위반하는 것 같고,
Spring Rest Docs가 테스트 기반 문서화라는 점이 문서의 신뢰도를 더 높여줄 수 있을 것 같아서 Spring Rest Docs를 사용하려고 결정함.
<br><br>
더 찾아보니 Spring Rest Docs로 문서를 만들면, 그것을 Swagger UI로 볼 수 있게 OpenAPI 3 문서로 변환해주는 도구를 발견해서 이것도 사용하려고 결정했다.

결과적으로 테스트 코드 기반이라 신뢰도도 있고, UI도 이쁘면서, 프로덕트 코드에 문서화 코드가 섞이는 것도 방지한 것 같아서 이 방법으로 
API 문서화를 하기로 최종 결정했다.
</details>

<br>

<details><summary> Redis를 활용해서 조회수 기반 인기 매물 조회 기능 구현
</summary>

### 배경

가장 인기있는 매물 5개를 상단에 노출 시키기 위해 조회수 기반 인기 매물 조회 기능을 구현해야 했다.
한 유저가 자기 매물의 조회수를 마음대로 올려서 항상 상위에 노출 시킬 수도 있기 때문에 조회수 어뷰징도 신경써야했다.

### Redis를 활용해서 구현

1. 사용자 단위 조회 기록 관리 
사용자 단위로 조회 여부를 판단하기 위해 Redis Key를 UserView:ItemId:{itemId}:UserId:{userId} 포맷으로 생성하고,
조회 기록은 1시간의 TTL로 관리했다.
사용자가 동일 매물을 1시간 내 반복 조회하더라도 조회수는 증가하지 않도록 설계했다.


2. 실시간 조회수 증가 처리
조회수는 ViewCount:ItemId:{itemId}로 Redis에 저장된다. 
Redis의 INCR 명령어를 통해 조회수 증가를 원자적으로 처리했다.


3. 조회수 기반 상위 매물 조회
사용자 지역 정보를 기반으로 매물 리스트를 필터링한 후, Redis에서 각 매물의 조회수를 가져와 정렬했다. <br>
상위 5개 매물은 조회수를 기준으로 내림차순 정렬해 반환했다.


### 구현 방법 의사결정 이유
1. 처리 속도
Redis는 메모리 기반으로 작동해 데이터 읽기, 쓰기 속도가 매우 빠르다. <br>
대규모 동시 요청도 효율적으로 처리할 수 있어서 조회 성능이 향상된다.


2. 데이터베이스 부하 감소
조회수 데이터를 Redis에서 관리하여 DB의 부하를 줄였다.


3. TTL 기반 실시간 관리
회원별 조회 데이터를 TTL로 관리해 중복을 방지하고 실시간 인기 매물을 정확하게 조회할 수 있다.
</details>

---
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