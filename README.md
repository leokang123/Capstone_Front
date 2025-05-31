# 폐기물 관리 시스템 앱 개발

## 목표

- 병원별 맞춤형 폐기물 관리 체계 개발
- 사전 지식이 없는 사용자도 앱의 안내에 따라 사용하면 법적 요건을 자동으로 충족하도록 설계

## 주요 기능
- 폐기물 등록
- 폐기물 조회
  - 상세 조회
  - 고급 검색 기능
- 폐기물 처리
  - 상태별 분류 (수집, 이동, 저장, 배출)
  - 사용자 권한에 따른 기능 구분
- 폐기물 배출
  - 창고에 저장된 폐기물만 조회 및 처리 가능
  - 관리자 전용 기능
- 블루투스 연동
  - 비콘 탐색 및 연결 기능
- 푸시 알림 기능
  - 폐기물 종류별 저장 기한 도달 시 알림
  - 관리자 설정 방역 일정에 따른 알림

---

# 앱 개요

#### 앱 이름: 폐기 수첩  
#### 개발 기간: 약 1개월  
#### 개발 참여자  
- 프론트엔드 (앱): 강정훈  
- 프론트엔드 (웹): 고호진  
- 백엔드: 안준영 (정), 홍서준 (부)  
- 기타 지원: 윤지원

### 프로젝트 배경  
의료 폐기물은 감염성 또는 유해 물질을 포함하고 있어 질병 확산 및 환경오염의 위험이 큽니다. 이에 따라 국내외적으로 법적 규제가 강화되고 있으며, 이러한 폐기물을 효율적이고 체계적으로 관리할 수 있는 플랫폼이 필요합니다.

---

## 시작 화면
### 로그인 및 회원가입

회원가입 후 웹 관리자 승인과 권한 부여를 받아야 로그인 및 사용이 가능합니다.
<!-- Login & Register -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/f1b924d8-fbf6-4c74-a371-fd7ab10fe854" width="200" alt="Login Screen" title="Login Screen"/>
  <img src="https://github.com/user-attachments/assets/b9849255-6cfa-4f55-95c2-6254cbc35524" width="200" alt="Register Screen" title="Register Screen"/>
</p>

## 홈 화면  
앱의 주요 기능으로 이동할 수 있는 메인 대시보드입니다.
<!-- Home, DarkMode -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/64dbc270-fa59-4760-8096-5e8271e7be27" width="200" alt="Home Screen" title="Home Screen"/>
  <img src="https://github.com/user-attachments/assets/4f79c92c-766e-40ba-932b-51728e641467" width="200" alt="Dark Mode Screen" title="Dark Mode Screen"/>
</p>

## 부가 기능 화면
### 알림, 설정, 사용자 정보 수정

- 최근 수신된 알림 확인  
- 다크 모드 전환, 로그아웃, 사용자 정보 수정 가능
<!-- Notification, Settings, ProfileEdit -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/ca8c5177-0c54-4449-9a1c-73c77b4a6994" width="200" alt="Notification Screen" title="Notification Screen"/>
  <img src="https://github.com/user-attachments/assets/11bac255-a081-414f-a279-bd2b0cd9edd4" width="200" alt="Settings Screen" title="Settings Screen"/>
  <img src="https://github.com/user-attachments/assets/065168d8-585d-44f4-b256-77ca15b384ec" width="200" alt="Profile Edit Screen" title="Profile Edit Screen"/>
</p>

## 폐기물 등록 화면  
비콘 탐색 후 등록된 비콘에 대해 폐기물 정보를 입력하여 시스템에 등록할 수 있습니다.
<!-- WasteRegister -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/d37e3015-4141-4cc8-80b2-061288a28b4f" width="200" alt="Waste Register Screen" title="Waste Register Screen"/>
</p>

## 폐기물 조회 화면  
처리 상태에 따른 폐기물을 조회하고 상세 정보를 확인할 수 있으며, 고급 검색도 지원합니다.
<!-- WasteList, WasteDetail -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/57f422b6-513b-4868-9a3d-fbc8283a3c77" width="200" alt="Waste List Screen" title="Waste List Screen"/>
  <img src="https://github.com/user-attachments/assets/193f70aa-0ffb-4061-bfc5-2692c09a08d8" width="200" alt="Waste Detail Screen" title="Waste Detail Screen"/>
  <img src="https://github.com/user-attachments/assets/187c5c07-dc71-49e9-b92d-a21cb8366c26" width="200" alt="Waste Detail Search Screen" title="Waste Detail Search Screen"/>
</p>

## 폐기물 처리 화면  
### 일반 사용자 및 창고 관리자 권한에 따른 처리

- 일반 사용자 (USER):  
  - '수집 중 → 이동 시작', '이동 시작 → 이동 완료' 상태 변경 가능  
- 창고 관리자 (WAREHOUSE_MANAGER):  
  - '이동 완료 → 창고 저장 중', '창고 저장 중 → 배출 완료' 상태 변경 가능
<!-- WasteMove, WasteRemove -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/02e0d253-3b46-4c47-84f0-04fd0fd11ff1" width="200" alt="Waste Move Screen" title="Waste Move Screen"/>
  <img src="https://github.com/user-attachments/assets/43c02c82-f7cb-4e36-8423-b9ac3076c9ff" width="200" alt="Waste Remove Screen" title="Waste Remove Screen"/>
</p>

---
## English Ver


# Medical Waste Management App

## Goals

- Develop a hospital-specific waste management system  
- Ensure legal compliance simply by following the guided steps within the app, even without prior knowledge

## Core Features
- Waste Registration
- Waste Inquiry
  - Detailed View of Waste
  - Advanced Search
- Waste Processing
  - Status Management (Collected, In Transit, Stored, Disposed)
  - Role-based Access Control
- Waste Disposal
  - Show only waste stored in assigned warehouses
  - Used by warehouse managers
- Bluetooth Integration
  - Search and connect to BLE beacons
- Push Notifications
  - Notifications based on the legal storage period for each waste type
  - Notifications based on scheduled disinfection dates set by the admin

---

# App Overview

#### App Name: WasteLog
#### Development Period: ~1 Month
#### Team Members  
- Frontend (Mobile): Kang Jung-hoon  
- Frontend (Web): Ko Ho-jin  
- Backend: Ahn Jun-young (Lead), Hong Seo-jun (Support)  
- Miscellaneous: Yoon Ji-won

### Project Summary  
Medical waste often contains infectious or hazardous materials. Due to the risk of widespread disease outbreaks and environmental damage, regulations are becoming stricter globally. This platform was built to help manage such waste systematically and in compliance with regulations.

---

## Start Screen
### Login & Registration

Users must sign up and then wait for admin approval via the web interface to gain access.
<p align="left">
  <img src="https://github.com/user-attachments/assets/f1b924d8-fbf6-4c74-a371-fd7ab10fe854" width="200" alt="Login Screen" title="Login Screen"/>
  <img src="https://github.com/user-attachments/assets/b9849255-6cfa-4f55-95c2-6254cbc35524" width="200" alt="Register Screen" title="Register Screen"/>
</p>

## Home Screen  
Initial landing page with all available features of the platform.
<p align="left">
  <img src="https://github.com/user-attachments/assets/64dbc270-fa59-4760-8096-5e8271e7be27" width="200" alt="Home Screen" title="Home Screen"/>
  <img src="https://github.com/user-attachments/assets/4f79c92c-766e-40ba-932b-51728e641467" width="200" alt="Dark Mode Screen" title="Dark Mode Screen"/>
</p>

## Additional Screens
### Notifications, Settings, Profile Editing

- Check recent alerts in the Notifications screen  
- Settings screen lets users switch to dark mode, update profile, or log out  
- In Edit Profile, users can modify their personal information

<p align="left">
  <img src="https://github.com/user-attachments/assets/ca8c5177-0c54-4449-9a1c-73c77b4a6994" width="200" alt="Notification Screen" title="Notification Screen"/>
  <img src="https://github.com/user-attachments/assets/11bac255-a081-414f-a279-bd2b0cd9edd4" width="200" alt="Settings Screen" title="Settings Screen"/>
  <img src="https://github.com/user-attachments/assets/065168d8-585d-44f4-b256-77ca15b384ec" width="200" alt="Profile Edit Screen" title="Profile Edit Screen"/>
</p>



## Waste Registration Screen  
Register waste by detecting BLE beacons and entering relevant information.
<p align="left">
  <img src="https://github.com/user-attachments/assets/d37e3015-4141-4cc8-80b2-061288a28b4f" width="200" alt="Waste Register Screen" title="Waste Register Screen"/>
</p>

## Waste Inquiry Screen  
View current and processed waste records, with detailed view and advanced search.
<p align="left">
  <img src="https://github.com/user-attachments/assets/57f422b6-513b-4868-9a3d-fbc8283a3c77" width="200" alt="Waste List Screen" title="Waste List Screen"/>
  <img src="https://github.com/user-attachments/assets/193f70aa-0ffb-4061-bfc5-2692c09a08d8" width="200" alt="Waste Detail Screen" title="Waste Detail Screen"/>
  <img src="https://github.com/user-attachments/assets/187c5c07-dc71-49e9-b92d-a21cb8366c26" width="200" alt="Waste Detail Search Screen" title="Waste Detail Search Screen"/>
</p>

## Waste Processing Screen  
### For General Users and Warehouse Managers

- Users with USER role:  
  - Can change status from Collected → In Transit and In Transit → Transit Complete  
- Users with WAREHOUSE_MANAGER role:  
  - Can change status from Transit Complete → Stored in Warehouse and Stored → Disposed
 <!-- WasteMove, WasteRemove -->
<p align="left">
  <img src="https://github.com/user-attachments/assets/02e0d253-3b46-4c47-84f0-04fd0fd11ff1" width="200" alt="Waste Move Screen" title="Waste Move Screen"/>
  <img src="https://github.com/user-attachments/assets/43c02c82-f7cb-4e36-8423-b9ac3076c9ff" width="200" alt="Waste Remove Screen" title="Waste Remove Screen"/>
</p>


