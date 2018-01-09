package cn.edu.nudt.pdl.yony.servicesealifevisitor.service;

import cn.yony.automaton.simple.SimpAutomaton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * email: yony228@163.com
 * Created by yony on 18-1-9.
 */
@Service
public class ChatService {

        @Autowired
        private SimpAutomaton sa;

        public Map reg() {
                String uuid = sa.reg();
                String resp = sa.interact(uuid, "sss");
                Map<String, String> rtVal = new HashMap<>();
                rtVal.put("result", Boolean.TRUE.toString());
                rtVal.put("uuid", uuid);
                rtVal.put("resp", resp);
                return rtVal;
        }

        public Map interact(String uuid, String req) {
                String resp = sa.interact(uuid, req);
                Map<String, String> rtVal = new HashMap<>();
                rtVal.put("result", Boolean.TRUE.toString());
                rtVal.put("resp", resp);
                return rtVal;
        }

        public Map quit(String uuid) {
                sa.leave(uuid);
                Map<String, String> rtVal = new HashMap<>();
                rtVal.put("result", Boolean.TRUE.toString());
                return rtVal;
        }
}
