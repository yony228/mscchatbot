package cn.edu.nudt.pdl.yony.servicedunner.service;

import cn.edu.nudt.pdl.yony.servicedunner.model.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * email: yony228@163.com
 * Created by yony on 17-11-2.
 */

@Service
public class UserService {

        Map<String, User> users = new HashMap();

        public UserService() {
                Date dt = new Date();
                users.put("gj", new User(){
                        {
                                setId("1");
                                setName("靖");
                                setFamilyName("郭");
                                setAge(31);
                                setPasswd("123");
                                setSex(User.Sex.MALE);
                                setUserName("gj");
                                setAddress("湖南省长沙市开福区1号");
                                setDebt(123123.45d);
                                setDebeFromTime(new Date(dt.getTime() - 30*24*60*60*1000));
                                setDebeToTime(new Date(dt.getTime() -15*24*60*60*1000));
                                setDebtTime(34);
                                setCardNum("3487326473677788");
                        }
                });
                users.put("hr", new User(){
                        {
                                setId("1");
                                setName("蓉");
                                setFamilyName("黄");
                                setAge(31);
                                setPasswd("123");
                                setSex(Sex.FEMALE);
                                setUserName("hr");
                                setAddress("湖南省长沙市开福区2号");
                                setDebt(600000d);
                                setDebeFromTime(new Date(dt.getTime() - 130*24*60*60*1000));
                                setDebeToTime(new Date(dt.getTime() - 78*24*60*60*1000));
                                setDebtTime(12);
                                setCardNum("3487326473677080");
                        }
                });
                users.put("zbt", new User(){
                        {
                                setId("1");
                                setName("伯通");
                                setFamilyName("周");
                                setAge(31);
                                setPasswd("123");
                                setSex(Sex.MALE);
                                setUserName("zbt");
                                setAddress("湖南省长沙市开福区3号");
                                setDebt(1256789.35d);
                                setDebeFromTime(new Date(dt.getTime() - 197*24*60*60*1000));
                                setDebeToTime(new Date(dt.getTime() -17*24*60*60*1000));
                                setDebtTime(56);
                                setCardNum("3487326473671100");
                        }
                });
                users.put("fqy", new User(){
                        {
                                setId("1");
                                setName("轻扬");
                                setFamilyName("风");
                                setAge(31);
                                setPasswd("123");
                                setSex(Sex.MALE);
                                setUserName("fqy");
                                setAddress("湖南省长沙市开福区4号");
                                setDebt(0d);
                                setCardNum("3487326473677685");
                        }
                });

        }

        public User checkUser(String userName, String passwd) {
                User user = users.get(userName);
                if(null != user && user.getPasswd().equals(passwd)) {
                        return user;
                } else {
                        return null;
                }
        }

}
