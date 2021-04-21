package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;
//듀토리얼 화면
public class TutorialActivity extends AppCompatActivity {
    private TextToSpeech tts;              // TTS 변수 선언
    private Button tutorial, start;
    private final Bundle params = new Bundle();
    private TextToSpeech mTts;
    private final Locale mLocale = Locale.KOREAN;
    private final float mPitch = 1.0f;
    private final float mRate = 1.0f;
    private final int mQueue = TextToSpeech.QUEUE_ADD; // TextToSpeech.QUEUE_FLUSH;

    // TTS 객체 초기화
    private void initTTS() {
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);
        mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTts.setLanguage(mLocale);
                    mTts.setPitch(mPitch);
                    mTts.setSpeechRate(mRate);
                } else {
                    showState("TTS 객체 초기화 중 문제가 발생했습니다.");
                }
            }


        });
        mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {

            }

            @Override
            public void onError(String s) {
                showState("재생 중 에러가 발생했습니다.");
            }
        });
    }

    private void showState(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorial = (Button) findViewById(R.id.tutorial); //튜토리얼 버튼
        start = (Button) findViewById(R.id.start); //다음으로 가기 버튼

        initTTS();
        startLoading();


        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editText에 있는 문장을 읽는다.
                mTts.speak("본 어플리케이션은 사용자의 카메라로부터 횡단보도와 신호등, 자동차를 인식하여 음성으로 정보를 안내하도록 설계되어 있습니다.", TextToSpeech.QUEUE_FLUSH, params, "tutorial");
                mTts.speak("안내는 다음과 같은 순서로 이루어져있습니다.", mQueue, params, "tutorial");
                mTts.speak(" 1.  횡단보도를 탐지합니다.", mQueue, params, "tutorial");
                mTts.speak(" 2.  횡단보도를 탐지한 상태에서 신호등을 탐지합니다.", mQueue, params, "tutorial");
                mTts.speak(" 3.  이때 신호등을 탐지하지 못한 경우. 주위에 도움을 요청하라는 안내를 시작합니다.", mQueue, params, "tutorial");
                mTts.speak(" 4.  신호등 색깔이 빨간 불일 때 탐지한 후. 초록불로 바뀐 겄을 탐지했을 때만 초록불입니다와 건너시라는 안내를 시작합니다.", mQueue, params, "tutorial");
                mTts.speak(" 5.  이때 자동차를 탐지한 경우. 전방에 자동차가 있으므로 주위하라는 안내를 시작합니다.", mQueue, params, "tutorial");
                mTts.speak(" 6.  신호등 색깔이 빨간 불일 경우. 빨간불입니다와 기다리라는 안내를 시작합니다.", mQueue, params, "tutorial");
                mTts.speak(" 7.  처음에 탐지한 신호등 색깔이 초록불일 경우. 기다리라는 안내를 시작합니다.", mQueue, params, "tutorial");
                mTts.speak("다시 설명을 듣고자 하신다면 튜토리얼 버튼을. 넘어가시려면 시작하기 버튼을 클릭하세요.", mQueue, params, "tutorial");
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다른 액티비티로 넘어갈때 밀려있는 tts가 나오는 문제 발생
                mTts.stop();
                mTts.shutdown(); //리셋
                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(intent);  //DetectorActivity 띄운다.
                finish();   //현재 액티비티 종료
            }
        });


    }

    //앱실행하면 듀토리얼 나오게함
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTts.speak("안녕하세요. 본 어플리케이션은 시각장애인분들의 횡단보도 보행을 보조하기 위한 어플리케이션입니다.", mQueue, params, "run");
                mTts.speak("사용하실 때는 집 근처 짧은 횡단보도와 낮 시간.  화창한 날에만 사용해 주세요", mQueue, params, "run");
                mTts.speak("모든 상황에서 완벽하게 작동하지 않으니. 주의하여 사용해 주세요", mQueue, params, "run");
                mTts.speak("보다 자세한 사항을 들으려면 튜토리얼 버튼을. 넘어가시려면 시작하기 버튼을 클릭하세요", mQueue, params, "run");
            }
        }, 1000);
    }


}