/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.detection;

import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Size;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.tensorflow.lite.examples.detection.customview.OverlayView;
import org.tensorflow.lite.examples.detection.customview.OverlayView.DrawCallback;
import org.tensorflow.lite.examples.detection.env.BorderedText;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.tflite.Detector;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;
import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;


/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();
  // Configuration for the Text2Speech.
  private final Bundle params = new Bundle();
  private TextToSpeech mTts;
  private final Locale mLocale = Locale.KOREAN;
  private final float mPitch = 1.0f;
  private final float mRate = 1.5f;
  private final int mQueue = TextToSpeech.QUEUE_ADD; // TextToSpeech.QUEUE_FLUSH;
  private static char flag = 'G';
//  private static int cnt = 0;
  private static long last_tts_timestamp = SystemClock.uptimeMillis();
  private static long last_green_timestamp = 0;
  private static long Last_null_timestamp=0;
  private TextView red_signal;
  private TextView green_signal;
  private TextView crosswalk_signal;
  private TextView caution_car_signal;


  // Configuration values for the prepackaged SSD model.
  private static final int TF_OD_API_INPUT_SIZE = 640;
  private static final boolean TF_OD_API_IS_QUANTIZED = false;
  private static final String TF_OD_API_MODEL_FILE = "model_fpnlite640.tflite"; //새로 학습한 모델
  private static final String TF_OD_API_LABELS_FILE = "labelmap.txt"; //새로 한 라벨링
  private static final DetectorMode MODE = DetectorMode.TF_OD_API;
  // Minimum detection confidence to track a detection.
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.25f;
  private static final boolean MAINTAIN_ASPECT = false;
  private static final Size DESIRED_PREVIEW_SIZE = new Size(1088, 1088); // 640, 480
  private static final boolean SAVE_PREVIEW_BITMAP = false;
  private static final float TEXT_SIZE_DIP = 10;
  OverlayView trackingOverlay;
  private Integer sensorOrientation;

  private Detector detector;

  private long lastProcessingTimeMs;
  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Bitmap cropCopyBitmap = null;

  private boolean computingDetection = false;

  private long timestamp = 0;

  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;

  private MultiBoxTracker tracker;

  private BorderedText borderedText;

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
        }else {
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
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    tracker = new MultiBoxTracker(this);

    int cropSize = TF_OD_API_INPUT_SIZE;

    try {
      detector =
          TFLiteObjectDetectionAPIModel.create(
              this,
              TF_OD_API_MODEL_FILE,
              TF_OD_API_LABELS_FILE,
              TF_OD_API_INPUT_SIZE,
              TF_OD_API_IS_QUANTIZED);
      cropSize = TF_OD_API_INPUT_SIZE;
    } catch (final IOException e) {
      e.printStackTrace();
      LOGGER.e(e, "Exception initializing Detector!");
      Toast toast =
          Toast.makeText(
              getApplicationContext(), "Detector could not be initialized", Toast.LENGTH_SHORT);
      toast.show();
      finish();
    }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

    frameToCropTransform =
        ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            cropSize, cropSize,
            sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
    trackingOverlay.addCallback(
        new DrawCallback() {
          @Override
          public void drawCallback(final Canvas canvas) {
            tracker.draw(canvas);
            if (isDebug()) {
              tracker.drawDebug(canvas);
            }
          }
        });

    tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
  }

  public void showsignal (ArrayList<String> classes) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (classes.contains("Crosswalk")) {
              crosswalk_signal.setTextColor(Color.WHITE);
            } else {
              crosswalk_signal.setTextColor(Color.BLACK);
            }
            if (classes.contains("Traffic_Light_Red")) {
              red_signal.setTextColor(Color.RED);
            } else {
              red_signal.setTextColor(Color.BLACK);
            }
            if (classes.contains("Traffic_Light_Green")) {
              green_signal.setTextColor(Color.GREEN);
            } else {
              green_signal.setTextColor(Color.BLACK);
            }
            if (classes.contains("Car")) {
              caution_car_signal.setTextColor(Color.YELLOW);
            } else {
              caution_car_signal.setTextColor(Color.BLACK);
            }
          }
        });
      }
    }).start();
  }

  public void speaksignal (ArrayList<String> classes) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        if (SystemClock.uptimeMillis() - last_tts_timestamp > 4000) {
          last_tts_timestamp = SystemClock.uptimeMillis();
          if (classes.contains("Crosswalk")) {
            mTts.speak("횡단보도", mQueue, params, "Crosswalk");
            if (classes.contains("Traffic_Light_Green")) {
              Last_null_timestamp = 0;
              if (flag == 'R') {
                mTts.speak("파란불입니다", mQueue, params, "Green");
                mTts.speak("주의하시면서 건너세요", mQueue, params, "Caution");
                if (classes.contains("Car")) {
                  mTts.speak("전방에 차량이 존재합니다. 주의해서 건너주세요", TextToSpeech.QUEUE_FLUSH, params, "Car");
                }
                if (last_green_timestamp == 0) {
                  last_green_timestamp = SystemClock.uptimeMillis();
                } else if (SystemClock.uptimeMillis() - last_green_timestamp > 30000) {
                  last_green_timestamp = 0;
                  flag = 'G';
                }
              } else {
                mTts.speak("잠시 대기해 주세요", mQueue, params, "wait");
              }

            } else if (classes.contains("Traffic_Light_Red")) {
              mTts.speak("빨간불입니다. 잠시 대기해 주세요", mQueue, params, "Red");
              flag = 'R';
              Last_null_timestamp=0;
            } else {
              if(Last_null_timestamp==0){
                Last_null_timestamp = SystemClock.uptimeMillis();
              } else if (SystemClock.uptimeMillis() - Last_null_timestamp > 10000){
                mTts.speak("신호등이 없거나 잡히지 않습니다. 도움을 요청하세요", mQueue, params, "null");
                Last_null_timestamp=0;
              }
            }
          }
        }
      }
    }).start();
  }

  @Override
  protected void processImage() {
    ++timestamp;
    final long currTimestamp = timestamp;
    trackingOverlay.postInvalidate();

    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }
    computingDetection = true;
    LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    readyForNextImage();

    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }
    red_signal = findViewById(R.id.text_red_signal);
    green_signal = findViewById(R.id.text_green_signal);
    crosswalk_signal = findViewById(R.id.text_crosswalk);
    caution_car_signal = findViewById(R.id.text_caution_car);



    initTTS();
    runInBackground(
        new Runnable() {
          @Override
          public void run() {
            LOGGER.i("Running detection on image " + currTimestamp);
            final long startTime = SystemClock.uptimeMillis();
            final List<Detector.Recognition> results = detector.recognizeImage(croppedBitmap);
            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

            cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
            final Canvas canvas = new Canvas(cropCopyBitmap);
            final Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(2.0f);

            float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
            switch (MODE) {
              case TF_OD_API:
                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                break;
            }

            final List<Detector.Recognition> mappedRecognitions =
                new ArrayList<Detector.Recognition>();
            final ArrayList<String> classes = new ArrayList<String>();

            for (final Detector.Recognition result : results) {
              final RectF location = result.getLocation();
              if (location != null && result.getConfidence() >= minimumConfidence) {
                canvas.drawRect(location, paint);

                cropToFrameTransform.mapRect(location);

                result.setLocation(location);
                mappedRecognitions.add(result);
                classes.add(result.getTitle());
              }
            }

            showsignal(classes);
            speaksignal(classes);

            tracker.trackResults(mappedRecognitions, currTimestamp);
            trackingOverlay.postInvalidate();

            computingDetection = false;

            runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    showFrameInfo(previewWidth + "x" + previewHeight);
                    showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                    showInference(lastProcessingTimeMs + "ms");
                  }
                });
          }
        });
  }

  @Override
  protected int getLayoutId() {
    return R.layout.tfe_od_camera_connection_fragment_tracking;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  // Which detection model to use: by default uses Tensorflow Object Detection API frozen
  // checkpoints.
  private enum DetectorMode {
    TF_OD_API;
  }

  @Override
  protected void setUseNNAPI(final boolean isChecked) {
    runInBackground(
        () -> {
          try {
            detector.setUseNNAPI(isChecked);
          } catch (UnsupportedOperationException e) {
            LOGGER.e(e, "Failed to set \"Use NNAPI\".");
            runOnUiThread(
                () -> {
                  Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
          }
        });
  }

  @Override
  protected void setNumThreads(final int numThreads) {
    runInBackground(() -> detector.setNumThreads(numThreads));
  }
/*  @Override
  public void onDestroy() {
    mTts.stop();
    mTts.shutdown();
    super.onDestroy();
  }*/

}
