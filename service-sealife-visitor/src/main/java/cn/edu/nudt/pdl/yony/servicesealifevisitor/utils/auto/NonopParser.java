package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.auto;

import cn.yony.automaton.core.IArbiter;

/**
 * email: yony228@163.com
 * Created by yony on 17-11-17.
 */
public class NonopParser implements IArbiter.IParser {
        @Override
        public String parser(String source) {
                return "1";
        }
}
