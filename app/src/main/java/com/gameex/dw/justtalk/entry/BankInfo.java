package com.gameex.dw.justtalk.entry;

import java.io.Serializable;

/**
 * 银行卡bean
 */
public class BankInfo implements Serializable {
    /**
     * 银行卡id
     */
    private String bankId;
    /**
     * 银行卡名称
     */
    private String bankName;
    /**
     * 银行卡尾号
     */
    private String bankEndNum;
    /**
     * 签约序列号
     */
    private String signSn;

    public BankInfo() {
    }

    public BankInfo(String bankId, String bankName, String bankEndNum) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.bankEndNum = bankEndNum;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankEndNum() {
        return bankEndNum;
    }

    public void setBankEndNum(String bankEndNum) {
        this.bankEndNum = bankEndNum;
    }

    public String getSignSn() {
        return signSn;
    }

    public void setSignSn(String signSn) {
        this.signSn = signSn;
    }

    @Override
    public String toString() {
        return "BankInfo{" +
                "bankId='" + bankId + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankEndNum='" + bankEndNum + '\'' +
                ", signSn='" + signSn + '\'' +
                '}';
    }
}
