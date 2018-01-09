package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.auto;

import cn.yony.automaton.core.IArbiter;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

public class SegmentationParser implements IArbiter.IParser  {

    public SegmentationParser() {
        this.posWordList = java.util.Arrays.asList(this.posWordArray);
        this.negWordList = java.util.Arrays.asList(this.negWordArray);
    }

    public String parser(String str) {
        String flag;
        List<Term> termList = HanLP.segment(str);
        int score = scoring(termList);

        if (score > 0) {
            flag = "1";
        } else {
            flag = "2";
        }

        return flag;
    }

    private int scoring(List<Term> termList) {
        int score = 0;
        int partScore = 0;
        boolean willDoNeg = false;

        for(Term t : termList) {
            if (t.nature.name().equals("w")) {
                if (willDoNeg) {
                    if (partScore != 0) {
                        partScore = -partScore;
                    } else {
                        partScore = -1;
                    }
                    willDoNeg = false;
                }
                score += partScore;
                partScore = 0;
            } else {
                if (t.word.equals("不")) {
                    willDoNeg = true;
                } else if(posWordList.contains(t.word)) {
                    partScore += 1;
                } else if(negWordList.contains(t.word)) {
                    partScore += -1;
                }
            }
        }
        if (willDoNeg) {
            if (partScore != 0) {
                partScore = -partScore;
            } else {
                partScore = -1;
            }
        }
        score += partScore;
        return score;

    }

    @Override
    public String toString() {
        return "SegmentationJudger -- 基于分词后对文本内容进行匹配进行近似模糊的语义匹配来识别肯定与否定。";
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
            };

    private String[] negWordArray = new String[]
            {
                    "不是",
                    "不对",
                    "不好",
                    "不行",
                    "不会",
                    "是的",
                    "没有",
                    "错",
                    "错了",
                    "对不起",
                    "不好意思",
                    "No",
            };
}
