package cn.edu.nudt.pdl.yony.servicewaiter.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.nudt.pdl.yony.servicewaiter.service.OuterDunnerService;
import cn.edu.nudt.pdl.yony.servicewaiter.utils.MongoJdbcTemplate;
import com.iflytek.cloud.speech.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
        @Autowired
        private OuterDunnerService outerDunnerService;
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

        // 断句标志
//        private volatile boolean stop = false;
//        private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
//        private Object monitor = new Object();


        // Constructor
        public DunnerHandler(Socket socket/*, OuterDunnerService outerDunnerService*/) throws IOException {
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
                                String receivedStr = buffer.toString();
                                buffer.setLength(0);
                                log.info(receivedStr.length() > 0 ? receivedStr : "receivedStr : no message");
                                if (StringUtils.isNotBlank(receivedStr)) {
                                        try {
                                                long startTime = System.currentTimeMillis();
                                                String sendStr = null;
                                                sendStr = getRepStr(receivedStr);
                                                log.info((System.currentTimeMillis() - startTime) + " sendStr :" + sendStr);
                                                sendStr(sendStr);
                                        } catch (JSONException e) {
                                                e.printStackTrace();
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
                }

                // 结束录音
                public void onEndOfSpeech() {
//                        synchronized (monitor) {
                        if (log.isDebugEnabled()) {
                                log.info("结束！");
                        }
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
        private String getRepStr(String sourceStr) throws JSONException {
                String repStr = this.outerDunnerService.interact(uuid, sourceStr);
                repStr = new JSONObject(repStr).getString("resp");
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
        private int sendStr(String sendSer) throws IOException, JSONException {
//                byte[] buffer = sendSer.getBytes();
//                outputStream.write(buffer, 0, buffer.length);
//                MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
//                bodyMap.add("information", );

                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("information", sendSer);

                HttpEntity<String> formEntity = new HttpEntity<>(jsonObj.toString(), headers);
                JSONObject result = restTemplate.postForObject("http://192.168.3.243:5000/reply", formEntity, JSONObject.class);
                try {
                        log.info(result.getString("result"));
                } catch (JSONException e) {
                        e.printStackTrace();
                }
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

        @Override
        public void run() {
                log.info("start!");
                // 获得UUID
                this.uuid = receiveUuid(32);

                // 获得通话信息缓存
                this.callInfo = this.mongoJdbcTemplate.findObjectByParam(collectionName, "uuid", this.uuid);

                // 开始交互
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
//                        mIat.setParameter(SpeechConstant.VAD_EOS,"3000");
//                        mIat.setParameter(SpeechConstant.)
                        //3.开始听写 mRecoListener
                        mIat.startListening(mRecoListener);
                        int readLength = 0;
                        while (true && readLength != -1) {
//                                synchronized (monitor) {
                                readLength = receiveStr(buffer);
//                                cyclicBarrier.reset();
                                if (readLength == -1) {
                                        break;
                                } else {
                                        if (!mIat.isListening()) {
                                                mIat.startListening(mRecoListener);
                                        }
//                                        log.info("readLength :" + readLength);
                                        mIat.writeAudio(buffer, 0, readLength);
                                }
                        }
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
