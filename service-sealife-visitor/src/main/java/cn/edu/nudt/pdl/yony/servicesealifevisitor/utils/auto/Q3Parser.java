package cn.edu.nudt.pdl.yony.servicesealifevisitor.utils.auto;

import cn.yony.automaton.core.IArbiter;
public class Q3Parser implements IArbiter.IParser {
    private String idCardNumber;

    public Q3Parser() {
        idCardNumber = "********************2174";
    }

    public Q3Parser(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String parser(String str) {
        String flag = "0";
        if(str.equals(tailIdCardNumber(4))) {
            flag = "1";
        } else {
            flag = "2";
        }

        return "1";
    }

    private String tailIdCardNumber (int num) {
        return this.idCardNumber.substring(18 - num);
    }
}
