# 시각장애인을 위한 SSD-MobileNet 기반 횡단보도 보행 보조 안드로이드 애플리케이션
## 구성원: 신승배 , 김종현 , 이다혜 , 유정용

<br>

### 개발 배경
*  현재 시각장애인들은 횡단보도 보행을 돕는 기기 횡단보도에 설치되어 있음에도 불가하고 파손 및 잘못된 설치로 사용하지 못하고 있다. 
*  실제로 한국시각장애인연합회에 따르면 파손 및 잘못된 설치로 인해 미사용률이 74%로 나타났다.

<br>

### 애플리케이션 소개
* **시각장애인의 횡단보도 보행을 돕는 안드로이드 애플리케이션**
* 이 애플리케이션은 객체 탐지를 위해 모바일 환경에서 작동하기 가벼운 SSD-MobileNet을 사용하였으며, **[중국](https://dl.orangedox.com/9ZvH36)** 과 한국의 Dataset을 전이 학습시켰다.
* 신호등(초록 색, 빨간 색)과 횡단보도, 자동차를 탐지 할 뿐만 아니라,  횡단보도 보행 시 상황에 따라 각기 다른 음성 안내 를 지원한다.

<br>
<br>

### 시연 영상 (클릭 하세요)

[![화면 캡처 2021-04-22 010405](https://user-images.githubusercontent.com/68493312/115585551-ae0ab280-a306-11eb-9b5b-602259812938.png)](https://www.youtube.com/watch?v=PIw5ZJnyuwk)

<br>
<br>

### 데이터셋
* 학습에 사용된 이미지는 횡단보도, 신호등이 같이 존재 하는 장소 사진이며 **[중국 Dataset](https://dl.orangedox.com/9ZvH36)** 과 직접 촬영을 통해 수집하여 만든 **한국 Dataset**을 사용하였다

![화면 캡처 2021-04-23 143723](https://user-images.githubusercontent.com/68493312/115823897-1e6a1e80-a442-11eb-96ce-c0fcd3a522f2.png)

<br>
<br>

### 라벨링
이미지 파일에서 탐지하고자 하는 객체 Bounding Box = (x1, y1, x2, y2)처리하여 csv 파일로 만들었다.

- [class,	xmin,	ymin,	xmax,	ymax,	filename,	width,	height]
- 예시: [Crosswalk,	24,	367,	756,	466,	john_IMG_0680.jpg,	768,	576]

class는 탐지하고자 하는 객체로 다음과 같이 라벨링 하였다.
  * 횡단보도 = Crosswalk
  *  빨간불 = Traffic_Light_red
  *  초록불 = Traffic_Light_green
  *  자동차 = Car

<br>
<br>

### 모델

* 모델 학습시 성능과 속도 향상을 위해 라벨링된 csv 파일과 Dataset의 이미지 파일을 **[tfrecord로 변환](./tfrecord)**
* **[SSD-MobileNet-V2 FPNLite 640x640](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md)** 모델 에 tfrecord 파일을  **전이 학습** 시켰다

<br>

**map 결과**
<br>

| 모델 | 결과 값 | 
|:--------|:--------:|
| **[SSD-MobileNet-V2 FPNLite 640x640](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md)** | 28.2 | 
| 전이 학습한 모델 | **32.0** |
<br>

학습한 모델의 성능 평가를 위해 객체 탐지 모델 평가 방식 중 하나인 mAP 방식을 사용하였다.
<br>
전이 학습을 위해 사용한 **[SSD-MobileNet-V2 FPNLite 640x640](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md)** 의 경우 탐지하는 객체가 다르기는 하였지만 실제 객체를 탐지하는 데에는 문제가 없었다. 
**[SSD-MobileNet-V2 FPNLite 640x640](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md)** 모델 보다 **학습한 모델**의 결과 값이 더 높게 나왔기에 사용해도 좋다고 판단하였다.

<br>

**모델에 관련된 보다 더 자세한 사항은** [**model 참조**](./model)
<br>
<br>
<br>

### [애플리케이션](./android_code/android/)
* 애플리케이션을 실행하면 모바일 기기에서 실시간으로 영상을 받아 객체를 탐지한다.
* 탐지 후에는 상황에 따라서 사용자에게 TTS(Text-To-Speech)을 통해 음성 안내를 시작한다.

다음과 같은 순서대로 작동한다. 
[**검은색=진행방향, 초록색=객체 탐지(인식) 했을경우, 빨간색=객체 탐지(인식) 못했을경우**]
![화면 캡처 2021-04-22 002334](https://user-images.githubusercontent.com/68493312/115579319-02ab2f00-a301-11eb-8e59-8814ce4d2b05.png)

<br>

#### 상황 별 음성안내

![화면 캡처 2021-04-24 175719](https://user-images.githubusercontent.com/68493312/115953465-b04b5780-a526-11eb-9f9a-feaa42cdcbcd.png)

<br>

#### 그 밖의 기능
![화면 캡처 2021-04-25 125956](https://user-images.githubusercontent.com/68493312/115981550-99147480-a5cf-11eb-9fed-edcd546f9577.png) 
![화면 캡처 2021-04-25 130011](https://user-images.githubusercontent.com/68493312/115981551-9a45a180-a5cf-11eb-9964-7b6edd0d24a5.png)

<br>

### 프로젝트에 관한 더 자세한 설명은 [TeamProject.pdf 참조](./TeamProject.pdf)
