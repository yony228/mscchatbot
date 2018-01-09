package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.auto;

import cn.yony.automaton.core.IArbiter;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

public class CommonPosOrNegParser implements IArbiter.IParser {
    public CommonPosOrNegParser() {
        this.posWordList = java.util.Arrays.asList(this.posWordArray);
        this.negWordList = java.util.Arrays.asList(this.negWordArray);
    }

    public String parser(String str) {
        String flag = "3";
        List<Term> termList = HanLP.segment(str);
//        int score = scoring(termList);

        for (Term t : termList) {
            if (this.posWordList.contains(t.word)) {
                flag = "1";
                break;
            } else if (this.negWordList.contains(t.word)){
                flag = "2";
                break;
            }
        }

        return flag;
    }

    private List posWordList;
    private List negWordList;

    private String[] posWordArray = new String[]
            {
                    "是",
                    "对",
                    "好",
                    "会",
                    "我会",
                    "是的",
                    "不错",
                    "没错",
                    "好的",
                    "对的",
                    "有",
                    "认识",
                    "那好吧",
                    "OK",
                    "Yes",
                    "嗯",
                    "对的",
                    "了解",
                    "有了解",
                    "明白",
                    "清楚",
                    "知道",
                    "晓得",
                    "理解",
                    "可以",
                    "知悉",
                    "没问题",
                    "算了解",
                    "应该了解",
                    "好像了解",
                    "正确",
                    "签了",
                    "是本人",
                    "好像是的",
                    "应该是的",
                    "签的",
                    "收到",
                    "受到",
                    "拿到"
            };

    private String[] negWordArray = new String[]
            {
                    "不是",
                    "不对",
                    "不好",
                    "不行",
                    "不会",
                    "是的",
                    "没",
                    "没有",
                    "错",
                    "错了",
                    "对不起",
                    "不好意思",
                    "No",
                    "不是",
                    "不对",
                    "不了解",
                    "不明白",
                    "不清楚",
                    "不知道",
                    "不晓得",
                    "不理解",
                    "不正确",
                    "没收到",
                    "没受到"
            };
}
