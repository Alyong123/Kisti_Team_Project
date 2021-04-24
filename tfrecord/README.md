### generate_tfrecord.py는 label과 image를 tfrecord로 변환해주는 파일

* 파일을 열어 30번째 줄에 존재하는 함수 부분을 `row_label == “csv 내에 존재하는 class name” `형식으로 학습하고자 하는 label에 맞게 수정
* 해당 .py는 csv 파일만 지원하며 ,csv 파일 내에 첫 행으로 header가 존재해야 한다.
* `class / xmin / xmax / ymin / ymax / filename /width / height `가 파일의 `첫 행 header`로 존재해야 하며,해당하는 row에 각 header들을 추가
* 존재한다면 위와 같이 맞추어 header를 바꿔라
* 이미지의 width / height는 존재하지 않아도 되지만, annotation된 box의 좌표는 min / max 형식이어야 한다.
*  min / width, height 형식은 min / max로 바꿔라
