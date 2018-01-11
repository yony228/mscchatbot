package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.auto;

import cn.yony.automaton.core.IArbiter;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonPosOrNegParser implements IArbiter.IParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonPosOrNegParser.class);

    public CommonPosOrNegParser() {
        this.posWordList = java.util.Arrays.asList(this.posWordArray);
        this.negWordList = java.util.Arrays.asList(this.negWordArray);
    }

    public String parser(String str) {
        String flag;
        List<Term> termList = HanLP.segment(str);
        LOGGER.info("Segmentation of " + str + " is " + termList.toString());
        int score = scoring(termList);
        LOGGER.info("Positive score of " + str + " is " + score);

        if (score > 0) {
            flag = "1";
        } else if(score < 0){
            flag = "2";
        } else {
            flag = "3";
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
                    "拿到",
                    "方便"
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
                    "没受到",
                    "不方便"
            };
}
