package com.pengjinfei.common.BeanPostProcessor;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pengjinfei on 16/7/2.
 * Description:
 */
public class ParameterizedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static final Pattern PATTERN = Pattern
            .compile("\\$\\{([^\\}]+)\\}");

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props) {
        String property = props.getProperty(placeholder);
        Matcher matcher = PATTERN.matcher(property);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String matcherKey = matcher.group(1);
            String matchervalue = props.getProperty(matcherKey);
            if (matchervalue != null) {
                matcher.appendReplacement(buffer, matchervalue);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
