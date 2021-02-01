# AudioDiary (안드로이드 음성일기 앱)

## 1. 소개
로컬 데이터베이스를 이용하여 CRUD 환경이 갖추어진 일기 안드로이드 어플리케이션이다. 본 앱은 일기 데이터에 사진과 음성 녹음 기능을 포함한다.

<br>

## 2. 프로젝트 구현 환경
* **Language**           :	JAVA 11.0.9
* **IDE**                :	안드로이드 스튜디오 4.1.1
* **Minimum SDK**        :	API 26, Android 8.0 (Oreo)

<br>

## 3. 프로젝트 테스트 환경
* **Test SDK	API 29** : Android 10.0 (Q)
* 안드로이드 스튜디오에서 제공하는 에뮬레이터를 사용하였을 때는 외부 저장소(사진과 녹음 파일 저장) 이용이 원활하지 않아 제대로 테스트 할 수 없었다.
* 가급적이면 개인 단말기를 사용하여 테스트 할 것을 권장한다.

<br>

## 4. 화면 상세 (인터페이스) & 기능 설명


## 5. 데이터베이스 스키마

CREATE TABLE TABLE_NAME (
_ID INTEGER PRIMARY KEY AUTOINCREMENT,
DATE TEXT,
CONTENTS TEXT,
PHOTO_PATH TEXT,
RECODE_PATH TEXT )
