package cn.edu.nudt.pdl.yony.servicedunner.controller;

import cn.edu.nudt.pdl.yony.servicedunner.model.User;
import cn.edu.nudt.pdl.yony.servicedunner.utils.MessageParser;
import cn.yony.automaton.simple.SimpAutomaton;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

//import com.deepvision.nlp.intelagent.Chatbot;
//import com.deepvision.nlp.intelagent.commons.State;

/**
 *
 */
@Controller
@RequestMapping(value = "/chat")
public class ChatController {

    @Autowired
    private SimpAutomaton sa;


    @RequestMapping(value = "/message")
    @ResponseBody
    public Map login(HttpServletRequest request, HttpServletResponse response) {
        /*Map<String, Object> result = new HashMap<>();
        String message = request.getParameter("message");
        Chatbot cc = (Chatbot) request.getSession().getAttribute("customerCare");
        User user = (User)request.getSession().getAttribute("userInfo");
        if(null == cc) {
            cc = new Chatbot(State.STAT_IDCONF);
            request.getSession().setAttribute("customerCare", cc);
        }
        System.out.println("Send message: " + message);
        String rpMsg = cc.doResponse(message);

        rpMsg = MessageParser.Parser(rpMsg, user);
        System.out.println("Response message: " + rpMsg);
        result.put("msg", rpMsg);
        return result;*/
        Map<String, Object> result = new HashMap<>();
        String message = request.getParameter("message");

        String uuid = (String)request.getSession().getAttribute("cbRegId");
        User user = (User)request.getSession().getAttribute("userInfo");
        if(StringUtils.isEmpty(uuid)) {
            uuid = sa.reg();
            request.getSession().setAttribute("cbRegId", uuid);
        }
        String rpMsg = sa.interact(uuid, message);
        rpMsg = MessageParser.Parser(rpMsg, user);
        result.put("msg", rpMsg);
        return result;
    }
}
