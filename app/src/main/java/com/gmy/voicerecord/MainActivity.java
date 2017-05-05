package com.gmy.voicerecord;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gmy.voicerecord.util.AudioRecorder2Mp3Util;
import com.gmy.voicerecord.view.IRecordButton;
import com.gmy.voicerecord.view.RecordButton;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 入口界面
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MainActivity extends Activity {
    //录音按钮
    private RecordButton voiceButton;
    private Button amrbtn,threegpbtn,mpthreebtn,wavbtn;
    //文件保存路径
    private String BasePath = Environment.getExternalStorageDirectory().toString() + "/voicerecord";
    private boolean isRecording = false;
    private MediaRecorder mMediaRecorder;
    private String mFileName;
    //底层采样率等设置
    private AudioRecord mRecorder;
    private short[] mBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        voiceButton = (RecordButton) findViewById(R.id.record);



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

        amrbtn = (Button)findViewById(R.id.amrbtn);
        amrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRecording){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            amrbtn.setText("录制amr");
                        }
                    });

                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;

                }else{

                    mFileName = BasePath+"/"+getCurrentDate() + ".amr";
                    File file = new File(mFileName);
                    if(!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    mMediaRecorder.setOutputFile(mFileName);
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mMediaRecorder.prepare();
                    } catch (IOException e) {
                        Log.e("error", "prepare() failed");
                        e.printStackTrace();
                    }

                    mMediaRecorder.start();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            amrbtn.setText("停止录制");
                        }
                    });
                }
                isRecording = !isRecording;
            }
        });

        threegpbtn = (Button)findViewById(R.id.threepgbtn);
        threegpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRecording){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            threegpbtn.setText("录制3gp");
                        }
                    });

                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;

                }else{

                    mFileName = BasePath+"/"+getCurrentDate() + ".3gp";
                    File file = new File(mFileName);
                    if(!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mMediaRecorder.setOutputFile(mFileName);
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mMediaRecorder.prepare();
                    } catch (IOException e) {
                        Log.e("error", "prepare() failed");
                    }

                    mMediaRecorder.start();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            threegpbtn.setText("停止录制");
                        }
                    });
                }
                isRecording = !isRecording;
            }
        });
        mpthreebtn = (Button)findViewById(R.id.mpthreebtn);
        mpthreebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRecording){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mpthreebtn.setText("录制mp3");
                        }
                    });

                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;

                }else{

                    mFileName = BasePath+"/"+getCurrentDate() + ".mp3";
                    File file = new File(mFileName);
                    if(!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mMediaRecorder.setOutputFile(mFileName);
                    /**AMR_NB、AAC、HE_AAC  支持windows mediaplayer*/
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mMediaRecorder.prepare();
                    } catch (IOException e) {
                        Log.e("error", "prepare() failed");
                    }

                    mMediaRecorder.start();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mpthreebtn.setText("停止录制");
                        }
                    });
                }
                isRecording = !isRecording;
            }
        });

        wavbtn = (Button)findViewById(R.id.wavbtn);
        wavbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRecording){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wavbtn.setText("录制wav");
                        }
                    });

                    isRecording = false;

                    if (mRecorder != null) {
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                    }

                }else{

                    mFileName = BasePath+"/"+getCurrentDate() + ".wav";
                    File file = new File(mFileName);
                    if(!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    int bufferSize = AudioRecord.getMinBufferSize(16000,
                            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                    mBuffer = new short[bufferSize];
                    mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000,
                            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                            bufferSize);

                    mRecorder.startRecording();
                    startBufferedWrite(file);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wavbtn.setText("停止录制");
                        }
                    });
                }
                isRecording = !isRecording;
            }
        });
    }

    /**
     * 写入到文件
     *
     * @param file
     */
    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                DataOutputStream output = null;
                long totalDataLen = 0;
                try {

                    WriteWaveFileHeader(new FileOutputStream(file),0,02,2,1,16000);


                    output = new DataOutputStream(new BufferedOutputStream(
                            new FileOutputStream(file)));
                    while (isRecording) {
                        int readSize = mRecorder.read(mBuffer, 0,
                                mBuffer.length);

                        totalDataLen += readSize;

                        for (int i = 0; i < readSize; i++) {

                            output.writeShort(mBuffer[i]);

                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
     * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
     * 自己特有的头文件。
     */
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
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
