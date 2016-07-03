package com.pengjinfei.common.BeanPostProcessor;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pengjinfei on 16/7/2.
 * Description:
 */
public class ParameterizedProperties extends Properties {

    private static final Pattern PATTERN = Pattern
            .compile("\\$\\{([^\\}]+)\\}");

    @Override
    public String getProperty(String key) {
        String property = super.getProperty(key);
        Matcher matcher = PATTERN.matcher(property);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String matcherKey = matcher.group(1);
            String matchervalue = super.getProperty(matcherKey);
            if (matchervalue != null) {
                matcher.appendReplacement(buffer, matchervalue);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
