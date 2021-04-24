## 모델을 학습시키고 안드로이드 애플리케이션에 사용할수 있도록 바꾸는 작업

다음 환경에서 작동하니 작업 환경을 확인

* Tensorflow 2.4.1 python 3.6~3.8(GPU 사용 시, cuDNN 8.0 / CUDA 11.0 설치. pip install tensorflow)
* pip install tf-slim, tflite_support, pycocotools

### [1. Tensorflow Object Detection API 설치](./models-master) 

### [2. 사용할 모델 정하고 설정하기](./SSD-MobileNet-V2_FPNLite_640x640)

### 3. 모델 학습하고 애플리케이션에 사용할 수 있게 tflite로 변환하기.

