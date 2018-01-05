package cn.edu.nudt.pdl.yony.servicewaiter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-25.
 */
public class Client {
        Socket socket = null;

        @Test
        public void start() throws Exception {
                byte[] buffer = new byte[4800];
                socket = new Socket("localhost", 14800);
                OutputStream os = socket.getOutputStream();
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost("http://localhost:8004/api/v1/chat/dunner/reg");
                UrlEncodedFormEntity uefEntity;
                List formparams = new ArrayList();
                formparams.add(new BasicNameValuePair("id", "jjjj11111"));
                uefEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
                httppost.setEntity(uefEntity);
                CloseableHttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                JSONObject obj = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
                String uuid = obj.getString("uuid");
                byte[] tmp = Hex.decodeHex(uuid.toCharArray());
                os.write(tmp, 0, tmp.length);
                System.out.println(tmp.length + "====================================");
                os.flush();


                FileInputStream fis = new FileInputStream("/home/yony/Downloads/hebing.wav");//hebing,8k-15
                int length = 0;
                while ((length = fis.read(buffer)) > 0) {
                        Thread.sleep(100);
                        os.write(buffer, 0, length);
                        os.flush();
                }
        }
}
