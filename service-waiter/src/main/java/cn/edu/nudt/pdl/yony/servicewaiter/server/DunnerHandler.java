package cn.edu.nudt.pdl.yony.servicewaiter.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.*;

import cn.edu.nudt.pdl.yony.servicewaiter.service.OuterDunnerService;
import com.iflytek.cloud.speech.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-25.
 * 146
 */
@Slf4j
@Component
@Scope("prototype")
public class DunnerHandler implements Runnable {
        // 外部软件ID码
        private static final String APPID = "5a4061d5";
        // 外部催收Chatbot服务
        private OuterDunnerService outerDunnerService;
        private SpeechRecognizer mIat;
        // 会话 id
        private String uuid;

        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        // 断句标志
        private volatile boolean stop = false;
        private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

        // Constructor
        public DunnerHandler(Socket socket, OuterDunnerService outerDunnerService) throws IOException {
                SpeechUtility.createUtility(SpeechConstant.APPID + "=" + APPID);
                this.socket = socket;
                this.outerDunnerService = outerDunnerService;
                this.inputStream = socket.getInputStream();
                this.outputStream = socket.getOutputStream();
        }

        //听写监听器
        private RecognizerListener mRecoListener = new RecognizerListener() {
                private StringBuffer buffer = new StringBuffer();
                private volatile int i = 0;

                //听写结果回调接口(返回Json格式结果，用户可参见附录)；
                //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
                //关于解析Json的代码可参见MscDemo中JsonParser类；
                //isLast等于true时会话结束。
                public void onResult(RecognizerResult results, boolean isLast) {
                        buffer.append(results.getResultString());
                        if (isLast) {
                                String receivedStr = buffer.toString();
                                buffer.setLength(0);
                                log.info(receivedStr.length() > 0 ? receivedStr : "receivedStr : no message");
                                if (StringUtils.isNotBlank(receivedStr)) {
                                        String sendStr = getRepStr(receivedStr);
                                        log.info("sendStr :" + sendStr);
                                        try {
                                                sendStr(sendStr);
                                        } catch (IOException e) {
                                                e.printStackTrace();
                                        }
                                }
                        }
                }

                //会话发生错误回调接口
                public void onError(SpeechError error) {
                        log.info(error.getErrorDescription(true)); //获取错误码描述
                }

                //开始录音
                public void onBeginOfSpeech() {
                        log.info("开始！");
                }

                //音量值0~30
                public void onVolumeChanged(int volume) {
//                        log.info("volume :" + volume);
                        /*if (volume == 0) {
                                i++;
                        } else {
                                i = 0;
                        }
                        if (i > 7) {
                                stop = true;
                                i = 0;
                        }
                        try {
                                cyclicBarrier.await(100, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        } catch (BrokenBarrierException e) {
                                e.printStackTrace();
                        } catch (TimeoutException e) {
                                e.printStackTrace();
                        } finally {

                        }*/
                }

                // 结束录音
                public void onEndOfSpeech() {
                        log.info("结束！");
                        mIat.stopListening();
                        stop = true;
                        while (mIat.isListening()){
                                try {
                                        Thread.sleep(100);
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                }
                        }
                        mIat.startListening(this);

                        stop = false;
                }

                // 扩展用接口
                public void onEvent(int eventType, int arg1, int arg2, String msg) {
                }
        };

        // 注册ChatBot
        private JSONObject regClient(String name) throws JSONException {
                return new JSONObject(this.outerDunnerService.reg(name));
        }

        // 获得应答用语
        private String getRepStr(String sourceStr) {
                return this.outerDunnerService.interact(uuid, sourceStr);
        }

        // 接收用户语音
        private int receiveStr(byte[] buffer) throws IOException {
                int readLength;
                readLength = inputStream.read(buffer, 0, buffer.length);
//                log.info("buffer length:" + readLength);
                return readLength;
        }

        // 发送用语
        private int sendStr(String sendSer) throws IOException {
                byte[] buffer = sendSer.getBytes();
//                outputStream.write(buffer, 0, buffer.length);
                return buffer.length;
        }

        @Override
        public void run() {
                //注册会话
                JSONObject jsonObject = null;
                String resp = null;
                try {
//                        jsonObject = new JSONObject(outerDunnerService.reg(""));
                        jsonObject = regClient("");
                        this.uuid = jsonObject.getString("uuid");
                        resp = jsonObject.getString("resp");
                        sendStr(resp);
                } catch (JSONException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }

                //开始交互
                try {
                        byte[] buffer = new byte[4800];
                        //1.创建SpeechRecognizer对象
                        mIat = SpeechRecognizer.createRecognizer();
                        //2.设置听写参数，详见《MSC Reference Manual》SpeechConstant类
                        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
                        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
                        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                        mIat.setParameter(SpeechConstant.RESULT_TYPE, "plain");
                        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                        mIat.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
//                        mIat.setParameter(SpeechConstant.VAD_BOS, "1000");
//                        mIat.setParameter(SpeechConstant.VAD_EOS,"1000");
//                        mIat.setParameter(SpeechConstant.)
                        //3.开始听写 mRecoListener
                        mIat.startListening(mRecoListener);
                        int readLength = 0;
                        while (true && readLength != -1) {
//                                readLength = socket.getInputStream().read(buffer, 0, 4800);
                                readLength = receiveStr(buffer);
//                                cyclicBarrier.reset();
                                if (readLength == -1) {
                                        break;
                                } else if (!stop) {
                                        mIat.writeAudio(buffer, 0, readLength);
                                } /*else {
                                        while (mIat.isListening()) {
                                                mIat.stopListening();
                                                try {
                                                        Thread.sleep(100);
                                                } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                }
                                        }
                                        stop = false;
                                        if (!mIat.isListening()) {
                                                mIat.startListening(mRecoListener);
                                        }
                                        mIat.writeAudio(buffer, 0, readLength);
                                }
                                try {
                                        cyclicBarrier.await(100, TimeUnit.MILLISECONDS);

                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                } catch (BrokenBarrierException e) {
                                        e.printStackTrace();
                                } catch (TimeoutException e) {
                                        e.printStackTrace();
                                }*/
                        }
                        mIat.stopListening();
                        try {
                                Thread.sleep(10000);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                } finally {
                        log.info("over!");
                        if (mIat.isListening()) {
                                mIat.stopListening();
                                mIat.destroy();
                        }
                        if (socket != null) {
                                try {
                                        socket.close();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        socket = null;
                                }
                        }
                        if (inputStream != null) {
                                try {
                                        inputStream.close();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        inputStream = null;
                                }
                        }
                        if (outputStream != null) {
                                try {
                                        outputStream.close();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        outputStream = null;
                                }
                        }
                }
        }
}
