package cn.edu.nudt.pdl.yony.servicedunner.utils;


import cn.edu.nudt.pdl.yony.servicedunner.model.User;

/**
 * email: yony228@163.com
 * Created by yony on 17-11-3.
 */
public class MessageParser {

        public static String Parser(String message, User user) {
                String rtmsg = message.replaceAll("<name>", user.getFamilyName() + user.getName())
                        .replaceAll("<title>", user.getSex() == User.Sex.MALE ? "先生" : "女士")
                        .replaceAll("<familyname>", user.getFamilyName())
                        .replaceAll("<day>", "" + user.getDebtTime())
                        .replaceAll("<amount>", String.valueOf(user.getDebt() / 10000))
                        .replaceAll("<cardTail4>", user.getCardNum().substring(user.getCardNum().length()-4));
                return rtmsg;
        }
}
