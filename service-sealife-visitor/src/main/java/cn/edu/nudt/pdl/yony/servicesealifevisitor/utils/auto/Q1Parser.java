package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.auto;

import cn.yony.automaton.core.IArbiter;
import java.util.List;

public class Q1Parser implements IArbiter.IParser {
    public Q1Parser() {
        this.enquireWordList = java.util.Arrays.asList(this.enquireWordArray);
    }


    public String parser(String str) {
        String flag;
        CommonPosOrNegParser cpnp = new CommonPosOrNegParser();
        flag = cpnp.parser(str);
        if( flag.equals("3")) {
            for(Object enquireWord : enquireWordList) {
                if(str.contains((String)enquireWord)) {
                    flag = "3";
                } else {
                    flag = "4";
                }
            }
        }

        return flag;
    }

    private List enquireWordList;

    private String[] enquireWordArray = new String[]
            {
                    "是谁",
                    "是哪里",
                    "你是谁",
                    "你是哪里",
                    "你们是谁",
                    "你们是哪里",
                    "什么事",
                    "什么事情",
                    "什么问题",
                    "你有什么事",
                    "你有什么事情"
            };
}
