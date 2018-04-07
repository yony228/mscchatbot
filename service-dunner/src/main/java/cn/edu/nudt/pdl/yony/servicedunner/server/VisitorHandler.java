package cn.edu.nudt.pdl.yony.servicedunner.server;

import cn.edu.nudt.pdl.yony.servicedunner.service.ChatService;
import cn.edu.nudt.pdl.yony.servicedunner.utils.MongoJdbcTemplate;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.cloud.speech.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-25.
 * 146
 */
@Slf4j
@Component
@Scope("prototype")
public class VisitorHandler implements Runnable {

        // 外部软件ID码
        private static final String APPID = "5a4061d5";
        // 外部催收Chatbot服务
        @Autowired
        private ChatService chatService;
        private SpeechRecognizer mIat;
        // 会话 id
        private String uuid;

        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        // 通话相关信息
        @Autowired
        MongoJdbcTemplate mongoJdbcTemplate;
        private Map callInfo;
        @Value("${self.mongodb.template.database}")
        private String collectionName;
        @Autowired
        RestTemplate restTemplate;

        private volatile boolean isStop = false;

        private ExecutorService executor = Executors.newCachedThreadPool();

        private Object lockObj = new Object();


        // Constructor
        public VisitorHandler(Socket socket) throws IOException {
                SpeechUtility.createUtility(SpeechConstant.APPID + "=" + APPID);
                this.socket = socket;
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
                                synchronized (lockObj) {
                                        isStop = true;
//                                      mIat.cancel();
//                                      mIat.stopListening();
                                }
                                executor.execute(() -> {
                                        String receivedStr = buffer.toString();
                                        buffer.setLength(0);
                                        log.info(receivedStr.length() > 0 ? receivedStr : "receivedStr : no message");
                                        if (StringUtils.isNotBlank(receivedStr)) {
                                                try {
                                                        long startTime = System.currentTimeMillis();
                                                        String sendStr = null;
                                                        sendStr = getRepStr(receivedStr);
                                                        log.info("处理时间:" + (System.currentTimeMillis() - startTime) + "; sendStr :" + sendStr);
                                                        sendStr(sendStr);
                                                } /*catch (JSONException e) {
                                                        e.printStackTrace();
                                                } */catch (IOException e) {
                                                        e.printStackTrace();
                                                }
                                        }
                                });
                        }
                }

                //会话发生错误回调接口
                public void onError(SpeechError error) {
                        synchronized (lockObj) {
                                isStop = true;
//                              mIat.cancel();
                        }
                        log.info(error.getErrorDescription(true)); //获取错误码描述
                }

                //开始录音
                public void onBeginOfSpeech() {
                        log.info("开始！");
                }

                //音量值0~30
                public void onVolumeChanged(int volume) {
                }

                // 结束录音
                public void onEndOfSpeech() {
                        /*synchronized (lockObj) {
                                isStop = true;
//                              mIat.cancel();
                        }*/
                        log.info("结束！");
                }

                // 扩展用接口
                public void onEvent(int eventType, int arg1, int arg2, String msg) {
                }
        };

        // 注册ChatBot
//        private JSONObject regClient(String name) throws JSONException {
//                return new JSONObject(this.outerDunnerService.reg(name));
//        }


        // 接收用户语音
        private int receiveStr(byte[] buffer) throws IOException {
                int readLength;
                readLength = inputStream.read(buffer);
//                log.info("buffer length:" + readLength);
                return readLength;
        }

        // 获得应答用语
        private String getRepStr(String sourceStr) {
                Map repMap = this.chatService.interact(uuid, sourceStr);
                String repStr = repMap.get("resp").toString();
                Pattern p = Pattern.compile("#\\w+#");
                Matcher m = p.matcher(repStr);
                while (m.find()) {
                        if (this.callInfo.containsKey(m.group().replace("#", ""))) {
                                repStr = repStr.replaceAll(m.group(), this.callInfo.get(m.group().replace("#", "")).toString());
                        }
                }
                return repStr;
        }

        // 发送用语
        private int sendStr(String sendSer) throws IOException {
                //entity
                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("text", sendSer);
                jsonObj.put("uuid", uuid);
                HttpEntity<String> formEntity = new HttpEntity<>(jsonObj.toString(), headers);
                //uri
                String uri = callInfo.get("volUri").toString();
                log.info("URI:" + uri);

                JSONObject result = restTemplate.postForObject(uri, formEntity, JSONObject.class);
//                try {
//                        log.info(result.toString());
//                } catch (JSONException e) {
//                        e.printStackTrace();
//                }
                return sendSer.length();//buffer.length;
        }

        // 接收UUID
        private String receiveUuid(int bufferLength) {
                byte[] buffer = new byte[bufferLength];
                int index = 0;
                while (index < bufferLength) {
                        try {
                                index += inputStream.read(buffer, index, bufferLength - index);
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
                String tmp = new String(buffer);// Hex.encodeHexString(buffer);
                return tmp;
        }

//        public boolean isNeededStopFun(Object lockObj) {
//                synchronized (lockObj) {
//                        return mIat.isListening() && isStop;
//                }
//        }
//
//        public boolean isStartFun(Object lockObj) {
//                synchronized (lockObj) {
//                        return mIat.isListening() && !isStop;
//                }
//        }

        class StartListenerHandler implements Runnable {

                @Override
                public void run() {
                        while (!Thread.interrupted()) {
                                synchronized (lockObj) {
                                        if (!mIat.isListening()) {
                                                log.info("--start listening begin!");
                                                mIat = SpeechRecognizer.createRecognizer();
                                                //2.设置听写参数，详见《MSC Reference Manual》SpeechConstant类
                                                mIat.setParameter(SpeechConstant.DOMAIN, "iat");
                                                mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                                                mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
                                                mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                                                mIat.setParameter(SpeechConstant.RESULT_TYPE, "plain");
                                                mIat.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
                                                //3.开始听写 mRecoListener
                                                mIat.startListening(mRecoListener);
                                                isStop = false;
                                                log.info("--start listening end!");
                                        }
                                }
                        }
                }
        }

        class StopListenerHandler implements Runnable {
                @Override
                public void run() {
                        while (!Thread.interrupted()) {
                                synchronized (lockObj) {
                                        if (mIat.isListening() && isStop) {
                                                log.info("--stop listening begin!");
                                                mIat.cancel();
                                                mIat.stopListening();
                                                mIat.destroy();
                                                log.info("--stop listening end!");
                                        }
                                }
                        }
                }
        }

        private byte[] buffer = new byte[4800];

        private int readLength = 0;

        @Override
        public void run() {
                log.info("start!");
                // 获得UUID
                this.uuid = receiveUuid(32);

                // 获得通话信息缓存
                this.callInfo = this.mongoJdbcTemplate.findObjectByParam(collectionName, "uuid", this.uuid);
                this.callInfo.put("insurerName", "张三");
                this.callInfo.put("insurerTitle", "先生");
                this.callInfo.put("chatbotNum", "001");
                this.callInfo.put("insuranceType", "好生活年金保险");
                this.callInfo.put("insurePeriod", "十");
                this.callInfo.put("feePeriod", "二十");
                this.callInfo.put("feePerYear", "一千五百元整");
                this.callInfo.put("insuranceLiability", "好生活年金");
                this.callInfo.put("insurerPhoneNumber", "13333333333");
                this.callInfo.put("insurerAddress", "湖南长沙");
                this.callInfo.put("insurerZipCode", "421000");

                // 开始交互
                try {
                        //1.创建SpeechRecognizer对象
                        mIat = SpeechRecognizer.createRecognizer();
                        //2.设置听写参数，详见《MSC Reference Manual》SpeechConstant类
                        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
                        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
                        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                        mIat.setParameter(SpeechConstant.RESULT_TYPE, "plain");
                        mIat.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
//                        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
//                        mIat.setParameter(SpeechConstant.VAD_EOS,"3000");
//                        mIat.setParameter(SpeechConstant.)
                        //3.开始听写 mRecoListener
                        mIat.startListening(mRecoListener);

                        Thread startListenerHandler = new Thread(new StartListenerHandler());
                        Thread stopListenerHandler = new Thread(new StopListenerHandler());
                        startListenerHandler.start();
                        stopListenerHandler.start();

//                        int readLength = 0;
                        while ((readLength = receiveStr(buffer)) != -1) {
                                synchronized (lockObj) {
//                                        log.info("--write begin");
                                        if (mIat.isListening() && !isStop) {
                                                mIat.writeAudio(buffer, 0, readLength);
//                                                log.info("--write end");
                                        }
                                }
                        }
                        startListenerHandler.interrupt();
                        stopListenerHandler.interrupt();
                        mIat.stopListening();
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
