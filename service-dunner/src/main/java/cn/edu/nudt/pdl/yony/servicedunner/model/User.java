package cn.edu.nudt.pdl.yony.servicedunner.model;

import java.util.Date;

/**
 * email: yony228@163.com
 * Created by yony on 17-11-2.
 */
public class User {
        public enum Sex{
                MALE,
                FEMALE
        }
        private String id;
        private String name;

        private String familyName;

        private String userName;
        private int age;
        private String passwd;
        private String address;
        private Sex sex;
        private double debt;

        public int getDebtTime() {
                return debtTime;
        }

        public void setDebtTime(int debtTime) {
                this.debtTime = debtTime;
        }

        private int debtTime;

        private Date debeFromTime;

        private Date debeToTime;

        public String getCardNum() {
                return cardNum;
        }

        public void setCardNum(String cardNum) {
                this.cardNum = cardNum;
        }

        private String cardNum;

        public String getFamilyName() {
                return familyName;
        }

        public void setFamilyName(String familyName) {
                this.familyName = familyName;
        }



        public Date getDebeFromTime() {
                return debeFromTime;
        }

        public void setDebeFromTime(Date debeFromTime) {
                this.debeFromTime = debeFromTime;
        }

        public Date getDebeToTime() {
                return debeToTime;
        }

        public void setDebeToTime(Date debeToTime) {
                this.debeToTime = debeToTime;
        }


        public double getDebt() {
                return debt;
        }

        public void setDebt(double debt) {
                this.debt = debt;
        }

        public String getUserName() {
                return userName;
        }

        public void setUserName(String userName) {
                this.userName = userName;
        }

        public void setAge(int age) {
                this.age = age;
        }

        public int getAge() {
                return age;
        }


        public String getPasswd() {
                return passwd;
        }

        public void setPasswd(String passwd) {
                this.passwd = passwd;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }


        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }

        public Sex getSex() {
                return sex;
        }

        public void setSex(Sex sex) {
                this.sex = sex;
        }


}
