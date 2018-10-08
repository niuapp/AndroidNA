package com.xxx.base.app;

import android.text.TextUtils;

/**
 * Created by Administrator on 2015/12/29.
 * 用户信息
 */
public class UserInfo {

    /**
     * 昵称
     */
    private String NICKNAME;
    /**
     * 用户登录后的session_key·
     */
    private String SESSION_KEY;


    public void setNICKNAME(String NICKNAME) {
        this.NICKNAME = NICKNAME;
    }

    public String getSESSION_KEY() {
        return TextUtils.isEmpty(SESSION_KEY) ? "" : SESSION_KEY;
    }

    public void setSESSION_KEY(String SESSION_KEY) {
        this.SESSION_KEY = SESSION_KEY;
    }

    /**
     * 手机号
     */
    private String PHONENUM;

    public String getNICKNAME() {
        return NICKNAME;
    }

    /**

     * 头像url
     */
    private String HEADIMG;

    public String getPHONENUM() {
        return PHONENUM;
    }

    public void setPHONENUM(String PHONENUM) {
        this.PHONENUM = PHONENUM;
    }

    public String getHEADIMG() {
        return HEADIMG;
    }

    public void setHEADIMG(String HEADIMG) {
        this.HEADIMG = HEADIMG;
    }
}
