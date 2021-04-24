object detection api의 설치가 완료되었다면 이제 학습을 위해 COCO Dataset에 Pre-trained된 [model 파일을 다운로드 한다.](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md)

* 여기서 주의할점은 model zoo에 존재하는 모든 모델들이 TFLite로의 export를 지원하지 않는다는 점이다. Head가 SSD인 모델들만 지원한다고 하니, TFLite로 export하는 경우에는 SSD 모델만 선택한다.

#### object-detection.pbxt 를 통해 탐지할 객체를 라벨링한다.

#### pipeline.config 는 모델 구조와 하이퍼 파라미터, 학습과 관련된 설정 파일로 기본적으로 알아야 할 설정들은 다음과 같다.
* num_classes: 탐지하고자 하는 class의 수
* batch_size: 배치 사이즈 / 클수록 더 큰 GPU 메모리 혹은 메모리가 필요
* num_steps: 한 미니배치를 학습하는 총 스텝 수
* fine_tune_checkpoint_type: 사용하고자 하는 모델의 타입 / “classification”과 “detection”, “full”이 있음. 입력되는 것에 따라 pre_trained된 모델 가중치를 load하는 부분이 달라진다.
			  
  - “classification“ : backbone 부분의 가중치를 불러온다.			  
  - ”detection“ : backbone + neck 부분의 가중치를 불러온다			  
  -  ”full“ : backbone + neck + head 전체 모델의 가중치를 불러온다.
<br>

**train_input_reader: train_dataset 파일 경로를 지정**
* label_map_path: 학습하고자 하는 라벨이 담긴 pbxt 파일 경로를 지정
* input_path: 학습 데이터 TFRecord 파일 경로를 지정
<br>

**eval_input_reader: eval_dataset 파일 경로를 지정**
* label_map_path: 위와 동일
* input_path: eval 데이터 TFRecord 파일 경로를 지정
<br>



