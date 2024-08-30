package com.atguigu.cloud.entities;

import javax.persistence.*;

/**
 * 表名：agency
*/
@Table(name = "agency")
public class Agency {
    /**
     * 编号ano
     */
    @Id
    private String ano;

    /**
     * 姓名aname
     */
    private String aname;

    /**
     * 性别asex 男或女
     */
    private String asex;

    /**
     * 电话aphone
     */
    private String aphone;

    /**
     * 备注aremark
     */
    private String aremark;

    /**
     * 获取编号ano
     *
     * @return ano - 编号ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * 设置编号ano
     *
     * @param ano 编号ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * 获取姓名aname
     *
     * @return aname - 姓名aname
     */
    public String getAname() {
        return aname;
    }

    /**
     * 设置姓名aname
     *
     * @param aname 姓名aname
     */
    public void setAname(String aname) {
        this.aname = aname;
    }

    /**
     * 获取性别asex 男或女
     *
     * @return asex - 性别asex 男或女
     */
    public String getAsex() {
        return asex;
    }

    /**
     * 设置性别asex 男或女
     *
     * @param asex 性别asex 男或女
     */
    public void setAsex(String asex) {
        this.asex = asex;
    }

    /**
     * 获取电话aphone
     *
     * @return aphone - 电话aphone
     */
    public String getAphone() {
        return aphone;
    }

    /**
     * 设置电话aphone
     *
     * @param aphone 电话aphone
     */
    public void setAphone(String aphone) {
        this.aphone = aphone;
    }

    /**
     * 获取备注aremark
     *
     * @return aremark - 备注aremark
     */
    public String getAremark() {
        return aremark;
    }

    /**
     * 设置备注aremark
     *
     * @param aremark 备注aremark
     */
    public void setAremark(String aremark) {
        this.aremark = aremark;
    }
}