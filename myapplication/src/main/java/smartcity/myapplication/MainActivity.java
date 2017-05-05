package smartcity.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.gmy.voicerecord.util.AudioRecorder2Mp3Util;
import com.gmy.voicerecord.view.IRecordButton;
import com.gmy.voicerecord.view.RecordButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    //录音按钮
    private RecordButton voiceButton;
    //文件保存路径
    private String BasePath = Environment.getExternalStorageDirectory().toString() + "/voicerecord";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceButton = (RecordButton) findViewById(R.id.record1);



        // 录音事件监听
        voiceButton.setAudioRecord(new IRecordButton() {
            private String fileName;
            private AudioRecorder2Mp3Util audioRecoder;
            private boolean canClean = false;

            /**
             * 释放资源
             */
            @Override
            public void stop() {
                Log.d("gmyboy", "------------stop-------------");
                audioRecoder.stopRecordingAndConvertFile();
                audioRecoder.cleanFile(AudioRecorder2Mp3Util.RAW);
                audioRecoder.close();
                audioRecoder = null;
            }

            /**
             * 开始录音
             */
            @Override
            public void start() {
                Log.d("gmyboy", "------------start-------------");
                if (canClean) {
                    audioRecoder.cleanFile(AudioRecorder2Mp3Util.MP3
                            | AudioRecorder2Mp3Util.RAW);
                }
                audioRecoder.startRecording();
                canClean = true;
            }

            /**
             * 准备工作
             */
            @Override
            public void ready() {
                Log.d("gmyboy", "------------ready-------------");
                File file = new File(BasePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                fileName = getCurrentDate();
                if (audioRecoder == null) {
                    audioRecoder = new AudioRecorder2Mp3Util(null,
                            getFilePath() + fileName + ".raw", getFilePath()
                            + fileName + ".mp3");
                }

            }

            /**
             * 获取保存路径
             */
            @Override
            public String getFilePath() {
                return BasePath + "/";
            }

            @Override
            public double getAmplitude() {
                //这里就放了一个随机数
                return Math.random() * 20000;
            }

            /**
             * 删除本地保存文件
             */
            @Override
            public void deleteOldFile() {
                Log.d("gmyboy", "------------deleteOldFile-------------");
                File file = new File(getFilePath() + fileName + ".mp3");
                if (file.exists())
                    file.delete();
            }

            /**
             * 录音完成，执行后面操作（发送）
             */
            @Override
            public void complite(float time) {
                Log.d("gmyboy", "------------complite-------------");
                Toast.makeText(MainActivity.this, "voicePath = " + getFilePath() + fileName + ".mp3" + "\n" + "voiceTime = " + String.valueOf((int) time), Toast.LENGTH_LONG).show();
            }
        });
        // 以当前时间作为录音文件名
    }

    /**
     * 获取当前系统时间作为音频文件名
     * @return
     */
    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
}
