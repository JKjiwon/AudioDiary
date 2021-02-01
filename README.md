# AudioDiary (안드로이드 음성일기 앱)

## 1. 소개
로컬 데이터베이스를 이용하여 CRUD 환경이 갖추어진 일기 안드로이드 어플리케이션이다. 본 앱은 일기 데이터에 사진과 음성 녹음 기능을 포함한다.

## 2. 프로젝트 구현 환경
|---|---|---|---|
|POST|/users/login|NONE|로그인 시  JWT Token 반환|
|POST|/users/refresh|JWT Token|JWT Token 검증 후 새로운 토큰 반환|
|GET|/users/validate|JWT Token|JWT Token 검증|
