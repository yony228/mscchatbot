package cn.edu.nudt.pdl.yony.servicedunner.controller.rest;

import cn.yony.automaton.simple.SimpAutomaton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-13.
 */
@RestController
@RequestMapping(value = "/rest/chat")
public class RestChatController {

        @Autowired
        private SimpAutomaton sa;

//        @RequestMapping(value = "/{uuid}",method = RequestMethod.GET,consumes="application/json")
//        public String getUser(@PathVariable String uuid, @RequestParam String pwd){
//                return "Welcome,"+uuid;
//        }

        //enroll
        @RequestMapping(value = "/reg", method = RequestMethod.GET)
        public Map reg() {
                String uuid = sa.reg();
                String resp = sa.interact(uuid, "sss");
                Map<String, String> rtVal = new HashMap<>();
                rtVal.put("result", Boolean.TRUE.toString());
                rtVal.put("uuid", uuid);
                rtVal.put("resp", resp);
                return rtVal;
        }

        //interact
        @RequestMapping(value = "/interact/{uuid}", method = RequestMethod.GET)
        public Map interact(@PathVariable String uuid, @RequestParam String req) {
                String resp = sa.interact(uuid, req);
                Map<String, String> rtVal = new HashMap<>();
                rtVal.put("result", Boolean.TRUE.toString());
                rtVal.put("resp", resp);
                return rtVal;
        }

        //quit
        @RequestMapping(value = "/quit/{uuid}", method = RequestMethod.GET)
        public Map quit(@PathVariable String uuid) {
                sa.leave(uuid);
                Map<String, String> rtVal = new HashMap<>();
                rtVal.put("result", Boolean.TRUE.toString());
                return rtVal;
        }

}
