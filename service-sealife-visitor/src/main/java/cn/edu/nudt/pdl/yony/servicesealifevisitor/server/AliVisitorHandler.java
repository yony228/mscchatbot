package cn.edu.nudt.pdl.yony.servicesealifevisitor.server;

import cn.edu.nudt.pdl.yony.servicesealifevisitor.service.ChatService;
import cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.MongoJdbcTemplate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.NlsFuture;
import com.alibaba.idst.nls.realtime.event.NlsEvent;
import com.alibaba.idst.nls.realtime.event.NlsListener;
import com.alibaba.idst.nls.realtime.protocol.NlsRequest;
import com.alibaba.idst.nls.realtime.protocol.NlsResponse;
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

import java.io.*;
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
public class AliVisitorHandler implements Runnable, NlsListener {

        // 外部催收Chatbot服务
        @Autowired
        private ChatService chatService;
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

        private ExecutorService executor = Executors.newCachedThreadPool();


        public AliVisitorHandler() {

        }

        // Constructor
        public AliVisitorHandler(Socket socket) throws IOException {
                this.socket = socket;
                this.inputStream = socket.getInputStream();
                this.outputStream = socket.getOutputStream();
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
                return sendSer.length();
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
        public void onMessageReceived(NlsEvent e) {
                NlsResponse response = e.getResponse();
                response.getFinish();
                if (response.result != null) {
                        System.out.println(response.getResult().getText());
                        executor.execute(() -> {
                                String receivedStr = response.getResult().getText();
//                                buffer.setLength(0);
                                log.info(receivedStr.length() > 0 ? receivedStr : "receivedStr : no message");
                                if (StringUtils.isNotBlank(receivedStr)) {
                                        try {
                                                long startTime = System.currentTimeMillis();
                                                String sendStr = null;
                                                sendStr = getRepStr(receivedStr);
                                                log.info("处理时间:" + (System.currentTimeMillis() - startTime) + "; sendStr :" + sendStr);
                                                sendStr(sendStr);
                                        } catch (IOException ex) {
                                                ex.printStackTrace();
                                        }
                                }
                        });
                        log.debug("status code = {},get finish is {},get recognize result: {}", response.getStatusCode(), response.getFinish(), response.getResult());
                        if (response.getQuality() != null) {
                                log.info("Sentence {} is over. Get ended sentence recognize result: {}, voice quality is {}",
                                        response.result.getSentence_id(), response.getResult(),
                                        JSON.toJSONString(response.getQuality()));
                        }
                } else {
                        log.info(JSON.toJSONString(response));
                }
        }

        @Override
        public void onOperationFailed(NlsEvent e) {
                log.error("status code is {}, on operation failed: {}", e.getResponse().getStatusCode(),
                        e.getErrorMessage());
        }

        @Override
        public void onChannelClosed(NlsEvent e) {log.debug("on websocket closed.");
        }

        @Override
        public void run() {
                log.info("start!");
                // 获得UUID
                this.uuid = receiveUuid(32);

                // 获得通话信息缓存
                this.callInfo = this.mongoJdbcTemplate.findObjectByParam(collectionName, "uuid", this.uuid);
                this.callInfo.put("insurerName", "胡达娇");
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
                        this.start();
                        this.process();
                        this.shutDown();
                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        log.info("over!");
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
                        if(client != null) {
                                client.close();
                        }
                }
        }

        protected String appKey = "nls-service-realtime-8k";
        protected String ak_id = "LTAIdRDPQOT4doZ7";
        protected String ak_secret = "dfh1XZnJQbbUKg5Ll2kzV9PI1YWG4G";
        protected String asrSC = "pcm";

        protected NlsClient client = new NlsClient();

        protected void start() {
                log.debug("init Nls client...");
                client.init();
        }

        public void shutDown() {
                log.debug("close NLS client manually!");
                client.close();
                log.debug("demo done");
        }

        public void process() throws Exception {
                NlsRequest req = buildRequest();
                NlsFuture future = client.createNlsFuture(req, this);
                log.debug("call NLS service");
                byte[] b = new byte[8000];
                int len = 0;
                while ((len = this.inputStream.read(b)) > 0) {
                        future.sendVoice(b, 0, len);
//                        Thread.sleep(200);
                }
                log.debug("send finish signal!");
                future.sendFinishSignal();

                log.debug("main thread enter waiting .");
                future.await(100000);
        }

        protected NlsRequest buildRequest() {
                NlsRequest req = new NlsRequest();
                req.setAppkey(this.appKey);
                req.setFormat(asrSC);
                req.setResponseMode("normal");
                req.setSampleRate(16000);
                // 用户根据[热词文档](~~49179~~) 设置自定义热词。
                // 通过设置VocabularyId调用热词。
                // req.setVocabularyId("");
                // 设置关键词库ID 使用时请修改为自定义的词库ID
                // req.setKeyWordListId("c1391f1c1f1b4002936893c6d97592f3");
                // the id and the id secret
                req.authorize(this.ak_id, this.ak_secret);
                return req;
        }
}
