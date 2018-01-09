package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.auto;

import cn.yony.automaton.core.IArbiter;
import java.util.List;

public class Q5Parser implements IArbiter.IParser {

    public Q5Parser() {
        this.notSignWordList = java.util.Arrays.asList(notSignWordArray);
        this.whatWordList = java.util.Arrays.asList(whatWordArray);
    }

    public String parser(String str) {
        String flag;
        CommonPosOrNegParser cpnp = new CommonPosOrNegParser();
        flag = cpnp.parser(str);
        if( flag.equals("3")) {
            for(Object whatWord : this.whatWordList) {
                if(str.contains((String)whatWord)) {
                    flag = "4";
                } else {
                    flag = "5";
                }
            }
        } else if( flag.equals("1")) {
            for(Object notSignWord : this.notSignWordList) {
                if(str.contains((String)notSignWord)) {
                    flag = "3";
                } else {
                    flag = "1";
                }
            }
        }

        return flag;
    }

    private List notSignWordList;
    private List whatWordList;

    private String[] notSignWordArray = new String[]
            {
                    "没有签名",
                    "不是本人",
                    "没有",
                    "不是"
            };

    private String[] whatWordArray = new String[]
            {
                    "什么",
                    "什么样",
                    "哪种",
                    "合同",
                    "什么合同",
                    "什么样的合同",
                    "哪种合同",
            };
}
