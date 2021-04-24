## 실행순서는 다음과 같다

### 1. model_main_tf2.py : 모델 학습을 실행
실행 방법
<br>

`python model_main.py --model_dir=학습 중 체크포인트가 저장될 폴더 경로--pipeline_config_path=학습할 모델의 pipeline.config 파일의 경로`
`(--checkpoint_dir=학습한 체크포인트가 저장된 경로 model_dir와 동일 / 입력 시 validation mode)`
<br>
<br>

### 2. export_tflite_graph_tf2.py:  .pb 형태의 모델 파일 생성
실행 방법
<br>

`python export_tflite_graph_tf2.py --pipeline_config_path=학습할 때 넣은 pipeline.config 파일 경로--trained_checkpoint_dir=학습한 모델의 체크포인트 파일이 존재하는 폴더 경로--output_directory=모델을 export할 폴더 경로`
<br>
<br>

### 3. tflite_converter.py: .pb 형식의 모델을 tflite로 변환
실행 방법
<br>

`python tflite_converter.py --model_dir=export된 모델 directory path --output_path=생성할 경로/이름.tflite`
<br>
<br>

### 4. tflite_metadata.py: 변환된 tflite 파일에 필요하다면 메타데이터 입력
* tflite_metadata.py 파일은 SSD_mobilenet_v2_640x640 기준으로 작성되었다. 
* 다른 모델에서 메타데이터 추가하고자 한다면 **[메타데이터 추가 방법](https://www.tensorflow.org/lite/convert/metadata?hl=ko)** 참조

실행 방법
<br>

`python tflite_metadata.py`
