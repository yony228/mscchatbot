package cn.edu.nudt.pdl.yony.servicesealifevisitor.controller.api.v1.chat;

import cn.edu.nudt.pdl.yony.servicesealifevisitor.service.ChatService;
import cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.MongoJdbcTemplate;
import cn.yony.automaton.simple.SimpAutomaton;
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
@RestController
@RequestMapping(value = "/api/v1/chat/sealife/visitorgroup")
public class VisitorGroupController {

        @Autowired
        private ChatService chatService;

        @Autowired
        MongoJdbcTemplate mongoJdbcTemplate;
        @Value("${self.mongodb.template.database}")
        private String collectionName;

        @Value("${self.server.hostIp}")
        private String hostIp;

        @Value("${self.server.hostPort}")
        private String hostPort;

        //enroll
        @RequestMapping(value = "/seat", method = RequestMethod.POST)
        public Map seat(@RequestParam HashMap<String, Object> params) {
                Map<String, Object> rtVal = new HashMap<>();

                Map regMap = chatService.reg();
                String uuid = regMap.get("uuid").toString();
                String resp = regMap.get("resp").toString();

                // 替换占位符
                Pattern p = Pattern.compile("#\\w+#");
                Matcher m = p.matcher(resp);
                while (m.find()) {
                        if (params.containsKey(m.group().replace("#", ""))) {
                                resp.replaceAll(m.group(), params.get(m.group().replace("#", "")).toString());
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
                return rtVal;
        }

        //interact
        @RequestMapping(value = "/seat/{uuid}", method = RequestMethod.GET)
        public Map interact(@PathVariable String uuid, @RequestParam String req) {
                return chatService.interact(uuid, req);
        }

        //quit
        @RequestMapping(value = "/seat/{uuid}", method = RequestMethod.DELETE)
        public Map quit(@PathVariable String uuid) {
                return chatService.quit(uuid);
        }

}
