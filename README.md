# 폐기물 관리 시스템 앱 개발 

## 목표 

- 병원 별 폐기물 관리 체계 개발
- 사전 지식없이 어플에서 유도되는대로 따라갈시 법적 사항을 모두 준수하게 개발

## 필요 기능 
- 폐기물 등록
- 폐기물 조회
  - 폐기물 상세조회
  - 폐기물 고급검색
- 폐기물 처리
  - 상태별 관리 (수집, 이동, 저장, 배출)
  - 권한별 차별점
- 폐기물 배출
  - 창고별 저장된 폐기물만 출력
  - 관리직이 사용
- 블루투스 기능
  - 블루투스 (비콘) 탐색
  - 비콘 검색
- 푸시 알림 기능
  - 페기물 종류마다 존재하는 저장기한에 따른 푸시알림
  - 관리자가 설정해놓은 방역일자에 따른 푸시알림

---
# 앱 설명 

#### 앱 이름 : 폐기 수첩 
#### 프로젝트 개발기간: 약 한달
#### 참여자 
##### 프론트(앱): 강정훈, 프론트(웹): 고호진, 백(서버): 안준영(정), 홍서준(부), 기타: 윤지원

### 프로젝트 설명 
의료 폐기물은 감염성이나 유해한 물질을 포함하고 있으며 질병의 전파로 인한 대규모의 감염병 발생과 환경 문제로 인해 국내외 법적 규제가 강화되고 있어 이를 체계적으로 관리 할 수 있는 플랫폼을 개발

## 시작 화면 
### 로그인 페이지 회원가입 페이지 

회원 가입시 웹에서 관리자가 승인및 권한 부여를 해주면 로그인이 가능하다
<!-- Login & Register -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/f1b924d8-fbf6-4c74-a371-fd7ab10fe854" width="200" alt="Login Screen" title="Login Screen"/>
  <img src="https://github.com/user-attachments/assets/b9849255-6cfa-4f55-95c2-6254cbc35524" width="200" alt="Register Screen" title="Register Screen"/>
</p>

## 홈 페이지
플랫폼에 필요한 기능을 수행할수 있는 초기화면 

<!-- Home, DarkMode -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/64dbc270-fa59-4760-8096-5e8271e7be27" width="200" alt="Home Screen" title="Home Screen"/>
  <img src="https://github.com/user-attachments/assets/4f79c92c-766e-40ba-932b-51728e641467" width="200" alt="Dark Mode Screen" title="Dark Mode Screen"/>
</p>


## 부가 페이지
### 알림, 설정, 사용자 정보 수정 페이지
알림에서 최근에 온 알림을 확인할 수 있다. 
설정에서 다크모드 전환 및 사용자 정보 수정페이지로 이동 가능하며 로그아웃을 할 수 있다. 
사용자 수정 페이지에서 자신의 정보를 수정할 수 있다.
<!-- Notification, Settings, ProfileEdit -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/ca8c5177-0c54-4449-9a1c-73c77b4a6994" width="200" alt="Notification Screen" title="Notification Screen"/>
  <img src="https://github.com/user-attachments/assets/11bac255-a081-414f-a279-bd2b0cd9edd4" width="200" alt="Settings Screen" title="Settings Screen"/>
  <img src="https://github.com/user-attachments/assets/065168d8-585d-44f4-b256-77ca15b384ec" width="200" alt="Profile Edit Screen" title="Profile Edit Screen"/>
</p>

## 폐기물 등록 페이지
비콘을 검색하여 비콘을 등록하고 폐기물 정보를 입력하여 폐기물을 시스템에 등록할 수 있다.
<!-- WasteRegister -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/d37e3015-4141-4cc8-80b2-061288a28b4f" width="200" alt="Waste Register Screen" title="Waste Register Screen"/>
</p>

## 폐기물 조회 페이지 
처리된 폐기물이나 처리중인 폐기물을 조회할 수 있고, 상세 조회 또한 가능하다.
<!-- WasteList, WasteDetail -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/57f422b6-513b-4868-9a3d-fbc8283a3c77" width="200" alt="Waste List Screen" title="Waste List Screen"/>
  <img src="https://github.com/user-attachments/assets/193f70aa-0ffb-4061-bfc5-2692c09a08d8" width="200" alt="Waste Detail Screen" title="Waste Detail Screen"/>
  <img src="https://github.com/user-attachments/assets/187c5c07-dc71-49e9-b92d-a21cb8366c26" width="200" alt="Waste Detail Search Screen" title="Waste Detail Search Screen"/>
</p>

## 폐기물 처리 페이지
### 폐기물 상태 이동 페이지 (유저), 폐기물 상태 이동 및 처리 페이지 (창고 관리자)
폐기물의 비콘을 검색하여 검색된 비콘들에 등록된 폐기물 상태를 바꾸어 처리할수 있는 페이지이다.
USER 권한인 사용자는 수집중 -> 이동 시작, 이동 시작 -> 이동 완료 까지의 처리를 할 수 있고, 
WAREHOUSE_MANGER 권한인 사용자는 이동완료 -> 창고 저장중, 창고 저장중 -> 배출 완료 의 처리를 할 수 있다.

<!-- WasteMove, WasteRemove -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/02e0d253-3b46-4c47-84f0-04fd0fd11ff1" width="200" alt="Waste Move Screen" title="Waste Move Screen"/>
  <img src="https://github.com/user-attachments/assets/43c02c82-f7cb-4e36-8423-b9ac3076c9ff" width="200" alt="Waste Remove Screen" title="Waste Remove Screen"/>
</p>




