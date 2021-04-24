### generate_tfrecord.py 는 label과 image를 tfrecord로 변환해주는 파일

* **파일을 열어 30번째 줄에 존재하는 함수 부분**을 `row_label == “csv 내에 존재하는 class name” `형식으로 학습하고자 하는 label에 맞게 수정
* 해당 .py는 csv 파일만 지원하며 ,csv 파일 내에 첫 행으로 header가 존재해야 한다.
* `class / xmin / xmax / ymin / ymax / filename /width / height `가 파일의 `첫 행 header`로 존재해야 하며,해당하는` row에 각 header들을 추가`

예시: **[Crosswalk, 24, 367, 756, 466, john_IMG_0680.jpg, 768, 576]**

* 존재한다면 위와 같이 맞추어 header를 바꿔라
* 이미지의 width / height는 존재하지 않아도 되지만, `annotation된 box의 좌표는 min / max 형식`이어야 한다.
*  min / width, height 형식은` min / max로` 바꿔라

#### 실행 방법
`python generate_tfrecord.py --csv_input=라벨링 한 csv 파일 경로 --output_path=생성할 경로/이름.tfrecord--image_dir=이미지 데이터가 포함된 폴더 경로`
