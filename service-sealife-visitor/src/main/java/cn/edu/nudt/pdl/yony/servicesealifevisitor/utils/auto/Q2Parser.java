package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.auto;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import cn.yony.automaton.core.IArbiter;
import java.util.List;

public class Q2Parser implements IArbiter.IParser {
    public Q2Parser() {
        this.agreeWordList = java.util.Arrays.asList(this.agreeWordArray);
        this.disagreeWordList = java.util.Arrays.asList(this.disagreeWordArray);
        this.drivingWordList = java.util.Arrays.asList(this.drivingWordArray);
        this.noNeedWordList = java.util.Arrays.asList(this.noNeedWordArray);
        this.doNotCallWordList = java.util.Arrays.asList(this.doNotCallWordArray);
        this.notAwareWordList = java.util.Arrays.asList(this.notAwareWordArray);
    }

    public String parser(String str) {
        String flag = "0";
        List<Term> termList = HanLP.segment(str);

        for (Term t : termList) {
            if (this.agreeWordList.contains(t.word)) {
                flag = "1";
                break;
            } else if (this.disagreeWordList.contains(t.word)){
                flag = "2";
                break;
            } else if (this.drivingWordList.contains(t.word)) {
                flag = "3";
                break;
            } else if (this.noNeedWordList.contains(t.word)) {
                flag = "4";
            } else if (this.doNotCallWordList.contains(t.word)) {
                flag = "5";
            } else if (this.notAwareWordList.contains(t.word)) {
                flag = "6";
            }
        }

        if(flag.equals("0")) {
            flag = "7";
        }

        return flag;
    }

    private List agreeWordList;
    private List disagreeWordList;
    private List drivingWordList;
    private List noNeedWordList;
    private List doNotCallWordList;
    private List notAwareWordList;

    private String[] agreeWordArray = new String[]
            {
                    "好的",
                    "方便",
                    "可以",
                    "行",
                    "好吧",
                    "行吧",
                    "行的",
                    "可以的",
                    "好",
                    "问吧",
                    "请便"
            };

    private String[] disagreeWordArray = new String[]
            {
                    "不方便",
                    "没空",
                    "在忙",
                    "正在忙",
                    "没时间",
                    "我不方便",
                    "我没空",
                    "我在忙",
                    "我正在忙",
                    "我没时间",
                    "不好意思"
            };

    private String [] drivingWordArray = new String[]
            {
                    "开车",
                    "在开车",
                    "我在开车",
                    "我正在开车"
            };

    private String [] noNeedWordArray = new String[]
             {
                     "不必",
                     "不需要",
                     "不用",
                     "不必回访",
                     "不需要回访",
                     "不用回访"
             };

    private String[] doNotCallWordArray = new String[]
            {
                    "打电话",
                    "不要打电话",
                    "不要再打电话",
                    "不要在打电话",
            };

    private String[] notAwareWordArray = new String[]
            {
                    "不知道",
                    "不清楚",
                    "不晓得",
                    "不小的",
                    "不了解",
                    "搞不清",
            };
}
