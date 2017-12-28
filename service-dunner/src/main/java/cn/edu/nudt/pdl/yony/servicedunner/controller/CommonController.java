package cn.edu.nudt.pdl.yony.servicedunner.controller;

import cn.edu.nudt.pdl.yony.servicedunner.model.User;
import cn.edu.nudt.pdl.yony.servicedunner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * email: yony228@163.com
 * Created by yony on 17-11-2.
 */
@Controller
@RequestMapping(value = "/common")
public class CommonController {

        @Autowired
        private UserService userService;

        @RequestMapping(value = "/login")
        @ResponseBody
        public Map login(HttpServletRequest request, HttpServletResponse response) {

                Map<String, Object> result = new HashMap<>();

                String username = request.getParameter("username");
                String password = request.getParameter("password");

                User user = userService.checkUser(username, password);
                if (user == null) {
                        result.put("success", "false");
                        result.put("message", "用户名或密码错");
                } else {
                        result.put("success", "true");
                        result.put("user", user);
                }
//
//                Map map = null;
//                List<Map<String, Object>> user = iUserService.findUserByNameAndPW(username, password, map);
//                if (user != null && user.size() != 0) {
//                        result.put("success", "true");
                HttpSession session = request.getSession();
                session.setAttribute("userInfo", user);
//                        session.setAttribute(ICommonConstant.SESSION_USER_NAME, user.get(0).get("name"));
//                        session.setAttribute(ICommonConstant.SESSION_USER_ID, user.get(0).get("user_id"));
//                        session.setAttribute(ICommonConstant.SESSION_USER_GROUP_NAME, user.get(0).get("group_name"));
//                        session.setAttribute(ICommonConstant.SESSION_USER_GROUP_ID, user.get(0).get("group"));
//                        Cookie ck = new Cookie("username", (String) user.get(0).get("name"));
//                        ck.setMaxAge(30*60);//30min;
//                        ck.setDomain(request.getHeader("host"));
//                        ck.setPath(request.getContextPath());
//                        response.addCookie(ck);
//                } else {
//                        result.put("success", "false");
//                }
//                result.put("success", "true");
                return result;
                //"welcome";
        }

        @RequestMapping(value = "/chat")
        public String chat(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
                String userName = request.getParameter("userName");
                User user = (User) request.getSession().getAttribute("userInfo");
                modelMap.put("userName", userName == null ? "XXX" : user.getUserName());
                String name = user.getName();
                String familyName = user.getFamilyName();
                double debt = user.getDebt();
                String initMessage = "";
                if(user.getDebt() > 0) {
                        initMessage = new StringBuilder("您好，请问您是").append(familyName).append(name).append(user.getSex() == User.Sex.MALE ? "先生" : "女士").append("吗?").toString();
                } else {
                        initMessage = new StringBuilder(familyName).append(name).append("你当前的信美分期信用情况良好。").toString();
                }
                modelMap.put("initMessage", initMessage);
                return "chat";
        }

        @RequestMapping(value = "/welcome")
        public String welcome(HttpServletRequest request, HttpServletResponse response) {
                return "welcome";
        }


        @RequestMapping(value = "/logout")
        public String logout(HttpServletRequest request, HttpServletResponse response) {
//        request.getSession().removeAttribute("userInfo");
                request.getSession().invalidate();
                return "/";
        }
}
