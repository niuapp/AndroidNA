package com.xxx.base.utils;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public final static String UTF_8 = "utf-8";

	/** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
	public static boolean isEmpty(String value) {
		if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/** 判断多个字符串是否相等，如果其中有一个为空字符串或者null，则返回false，只有全相等才返回true */
	public static boolean isEquals(String... agrs) {
		String last = null;
		for (int i = 0; i < agrs.length; i++) {
			String str = agrs[i];
			if (isEmpty(str)) {
				return false;
			}
			if (last != null && !str.equalsIgnoreCase(last)) {
				return false;
			}
			last = str;
		}
		return true;
	}

	/**
	 * 返回一个高亮spannable
	 * @param content 文本内容
	 * @param color   高亮颜色
	 * @param start   起始位置
	 * @param end     结束位置
	 * @return 高亮spannable
	 */
	public static CharSequence getHighLightText(String content, int color, int start, int end) {
		if (TextUtils.isEmpty(content)) {
			return "";
		}
		start = start >= 0 ? start : 0;
		end = end <= content.length() ? end : content.length();
		SpannableString spannable = new SpannableString(content);
		CharacterStyle span = new ForegroundColorSpan(color);
		spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	/**
	 * 获取链接样式的字符串，即字符串下面有下划线
	 * @param resId 文字资源
	 * @return 返回链接样式的字符串
	 */
	public static Spanned getHtmlStyleString(int resId) {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"\"><u><b>").append(AppUtils.getString(resId)).append(" </b></u></a>");
		return Html.fromHtml(sb.toString());
	}

	/** 格式化文件大小，不保留末尾的0 */
	public static String formatFileSize(long len) {
		return formatFileSize(len, false);
	}

	/** 格式化文件大小，保留末尾的0，达到长度一致 */
	public static String formatFileSize(long len, boolean keepZero) {
		String size;
		DecimalFormat formatKeepTwoZero = new DecimalFormat("#.00");
		DecimalFormat formatKeepOneZero = new DecimalFormat("#.0");
		if (len < 1024) {
			size = String.valueOf(len + "B");
		} else if (len < 10 * 1024) {
			// [0, 10KB)，保留两位小数
			size = String.valueOf(len * 100 / 1024 / (float) 100) + "KB";
		} else if (len < 100 * 1024) {
			// [10KB, 100KB)，保留一位小数
			size = String.valueOf(len * 10 / 1024 / (float) 10) + "KB";
		} else if (len < 1024 * 1024) {
			// [100KB, 1MB)，个位四舍五入
			size = String.valueOf(len / 1024) + "KB";
		} else if (len < 10 * 1024 * 1024) {
			// [1MB, 10MB)，保留两位小数
			if (keepZero) {
				size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024 / 1024 / (float) 100)) + "MB";
			} else {
				size = String.valueOf(len * 100 / 1024 / 1024 / (float) 100) + "MB";
			}
		} else if (len < 100 * 1024 * 1024) {
			// [10MB, 100MB)，保留一位小数
			if (keepZero) {
				size = String.valueOf(formatKeepOneZero.format(len * 10 / 1024 / 1024 / (float) 10)) + "MB";
			} else {
				size = String.valueOf(len * 10 / 1024 / 1024 / (float) 10) + "MB";
			}
		} else if (len < 1024 * 1024 * 1024) {
			// [100MB, 1GB)，个位四舍五入
			size = String.valueOf(len / 1024 / 1024) + "MB";
		} else {
			// [1GB, ...)，保留两位小数
			size = String.valueOf(len * 100 / 1024 / 1024 / 1024 / (float) 100) + "GB";
		}
		return size;
	}

	/**
	 *  1 - 7 把数字变大写 周几
	 * @param number
	 * @return
	 */
	public static String numberToUpperCase_week(int number){
		switch (number){
			case 1: return "一";
			case 2: return "二";
			case 3: return "三";
			case 4: return "四";
			case 5: return "五";
			case 6: return "六";
			case 7: return "日";
		}
		return "";
	}

	/**
	 * 把字符串两端的 "" 去掉
	 * @param string
	 * @return
	 */
	public static String getString_naked(String string){
		if (string == null) return "";
		if (string.startsWith("\"") && string.endsWith("\"")){
			try {
				string = getString_naked(string.substring(1, string.length() - 1));//截去前后引号，继续判断
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (string.length() == 0) return "";
		return string;
	}

	/**
	 * @param str  母字符串
	 * @param rgx  正则
	 */
	public static List<String> getString_matcher(String str, String rgx){
		Pattern pattern = Pattern.compile(rgx);
		Matcher matcher = pattern.matcher(str);

		List<String> strings = new ArrayList<>();
		while(matcher.find()){
			//这里会得到每个符合规则的字符串
			strings.add(matcher.group());
		}

		return strings;
	}


	/**
	 * 判断String 过滤null
	 * @param string
	 * @return
	 */
	public static String checkStr_empty(String string){
		return TextUtils.isEmpty(string) ? "" : string;
	}

	public static boolean rTemp(String r, String s){
		return checkStr_empty(r).matches(checkStr_empty(s));
	}

	/**
	 * 校验手机号是否合理
	 *
	 * @param inputNums    输入的数字
	 * @param numLength		数字长度
	 * @param errorMsg		输入错误提示
	 * @return
	 */
	public static boolean judgeNums(String inputNums, int numLength, String errorMsg) {
		if (isMatchLength(inputNums, numLength)
				&& isMobileNO(inputNums,numLength)) {
			return true;
		}
		if (inputNums.isEmpty()&&numLength==11){
			errorMsg="请输入手机号";
		}
		if (inputNums.isEmpty()&&numLength==6){
			errorMsg="请输入验证码";
		}
		AppUtils.showToastSafe(errorMsg);
		return false;
	}

	/**
	 * 判断一个字符串的位数
	 * @param str
	 * @param length
	 * @return
	 */
	public static boolean isMatchLength(String str, int length) {
		if (str.isEmpty()) {
			return false;
		} else {
			return str.length() == length ? true : false;
		}
	}

	/**
	 * 验证手机格式
	 * @param inputNums
	 * @param numLength
	 * @return
	 */
	public static boolean isMobileNO(String inputNums, int numLength) {
    /*
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
		String checkPhoneNum = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		//验证码正则
		String checkCode ="^[0-9]*$";
		if (TextUtils.isEmpty(inputNums)){
			return false;
		} else {
			if (numLength==11){
				return inputNums.matches(checkPhoneNum);
			}else if (numLength==6){
				return inputNums.matches(checkCode);
			}
			return false;
		}

	}

	/**
	 * 校验密码合理性
	 *
	 * @param inputNums		输入的数字
	 * @param minLength		限制的最小字数
	 * @param maxLength		限制的最大字数
	 * @param errorMsg		错误提示
	 * @return
	 */
	public static boolean judgePwd(String inputNums, int minLength, int maxLength, String errorMsg) {
		if (isPwdMatchLength(inputNums, minLength,maxLength)
				&& isPwdNO(inputNums,minLength,maxLength)) {
			return true;
		}
		if (inputNums.isEmpty()){
			errorMsg="请输入密码";
		}
		AppUtils.showToastSafe(errorMsg);
		return false;
	}

	/**
	 * 判空
	 *
	 * @param str
	 * @param minLength
	 * @param maxLength
	 * @return
	 */
	public static boolean isPwdMatchLength(String str, int minLength, int maxLength) {
		if (str.isEmpty()) {
			return false;
		} else {
			return (str.length() >=minLength&&str.length()<=maxLength) ? true : false;
		}
	}

	/**
	 * 判断密码格式
	 *
	 * @param inputNums
	 * @param minLength
	 * @param maxLength
	 * @return
	 */
	public static boolean isPwdNO(String inputNums, int minLength, int maxLength) {
//		密码正则
		String checkPsw = "^[a-zA-Z0-9]{6,12}$";
		if (TextUtils.isEmpty(inputNums)){
			return false;
		} else {
			if (inputNums.length()<=maxLength&&inputNums.length()>=minLength){
				return inputNums.matches(checkPsw);
			}
			return false;
		}
	}
}
