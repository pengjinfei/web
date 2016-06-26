package com.pengjinfei.common.utils;

/**
 * Author: EX-PENGJINFEI001
 * DATE: 2016-02-17
 * Description:
 */
public class SecurityUtils {

    /**
     * 处理特殊字符(Cross-site scripting (XSS))  ，将'<'替换成"&lt;"，等等。
     */
    public static String outputfilter(String value) {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(value.length());
        for (int i = 0; i < value.length(); ++i) {
            switch (value.charAt(i)) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '\'':
                    result.append("&#39;");
                    break;
                case '%':
                    result.append("&#37;");
                    break;
                case ';':
                    result.append("&#59;");
                    break;
                case '(':
                    result.append("&#40;");
                    break;
                case ')':
                    result.append("&#41;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '+':
                    result.append("&#43;");
                    break;
                default:
                    result.append(value.charAt(i));
                    break;
            }
        }
        return result.toString();
    }
}
