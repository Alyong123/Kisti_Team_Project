package org.tensorflow.lite.examples.detection;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
//애플리케이션 로딩화면
public class SplashActivity extends AppCompatActivity {
    @Override
    protected  void onCreate(Bundle savedlnstanceState){
        super.onCreate(savedlnstanceState);

        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,TutorialActivity.class));
        finish();

    }

}
