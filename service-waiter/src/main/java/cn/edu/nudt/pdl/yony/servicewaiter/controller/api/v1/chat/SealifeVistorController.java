package cn.edu.nudt.pdl.yony.servicewaiter.controller.api.v1.chat;

import cn.edu.nudt.pdl.yony.servicewaiter.service.OuterDunnerService;
import cn.edu.nudt.pdl.yony.servicewaiter.utils.MongoJdbcTemplate;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * email: yony228@163.com
 * Created by yony on 18-1-9.
 */
@RequestMapping("/api/v1/chat/sealife/visitorgroup")
@RestController
public class SealifeVistorController {
        @Autowired
        MongoJdbcTemplate mongoJdbcTemplate;
        @Autowired
        OuterDunnerService outerDunnerService;
        @Value("${self.mongodb.template.database}")
        private String collectionName;

        @Value("${self.server.hostIp}")
        private String hostIp;

        @Value("${self.server.hostPort}")
        private String hostPort;

        @RequestMapping(value = "/seat", method = RequestMethod.POST)
        public Map seat(@RequestParam HashMap<String, Object> params) {
                Map<String, Object> rtVal = new HashMap();
                //outer service id
                String id = params.get("id").toString();
                try {
                        JSONObject jsonObject = new JSONObject(this.outerDunnerService.reg(""));
                        String uuid = jsonObject.getString("uuid");
                        String resp = jsonObject.getString("resp");

                        Pattern p = Pattern.compile("#\\w+#");
                        Matcher m = p.matcher(resp);
                        while (m.find()) {
                                if (jsonObject.has(m.group().replace("#", ""))) {
                                        resp.replaceAll(m.group(), jsonObject.getString(m.group().replace("#", "")));
                                }
                        }


                        params.put("uuid", uuid);
                        //client info send to mongo
                        mongoJdbcTemplate.addMapObject(collectionName, params);

                        rtVal.put("result", Boolean.TRUE);
                        rtVal.put("uuid", uuid);
                        rtVal.put("resp", resp);
                        rtVal.put("hostIp", hostIp);
                        rtVal.put("hostPort", hostPort);
                } catch (JSONException e) {
                        e.printStackTrace();
                        rtVal.put("result", Boolean.FALSE);
                } catch (Exception e) {
                        e.printStackTrace();
                        rtVal.put("result", Boolean.FALSE);
                } finally {
                        return rtVal;
                }
        }

        @RequestMapping(value = "/seat/{uuid}", method = RequestMethod.DELETE)
        public Map hungUp(@PathVariable String uuid) {
                Map<String, Object> rtVal = new HashMap();
                rtVal.put("result", Boolean.TRUE);
                return rtVal;
        }
}
