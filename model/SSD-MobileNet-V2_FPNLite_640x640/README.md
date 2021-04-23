object detection api의 설치가 완료되었다면 이제 학습을 위해 COCO Dataset에 Pre-trained된 [model 파일을 다운로드 한다.](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md)

* 여기서 주의할점은 model zoo에 존재하는 모든 모델들이 TFLite로의 export를 지원하지 않는다는 점이다. Head가 SSD인 모델들만 지원한다고 하니, TFLite로 export하는 경우에는 SSD 모델만 선택한다.
