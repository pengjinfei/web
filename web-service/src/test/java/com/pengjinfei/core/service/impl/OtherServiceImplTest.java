package com.pengjinfei.core.service.impl;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-31
 * Description:
 */
public class OtherServiceImplTest {

    @Test
    public void testPattern() {
        Pattern pattern=Pattern.compile("update(\\s+\\w+)*\\s+set(\\s*\\w=\\*\\w)(,\\s*\\w=\\s*\\w)*");
        String input = "update test set a=1,b=2 where ";
//        String input = "UPDATE custom SET age=age+100 WHERE name=#{name}";
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()){
            String table=matcher.group(1);
            System.out.println(table);
            String endSet = matcher.group(3);
            if (endSet == null) {
                endSet=matcher.group(2);
            }
            String added=" ,"+table+".create_date=sysdate";
            String out=input.substring(0,matcher.end())+added+input.substring(matcher.end());
            System.out.println(out);
        }
    }

    @Test
    public void testPatternInsert() {
        Pattern pattern=Pattern.compile("\\)\\s*values\\s+.*(?=\\))",Pattern.CASE_INSENSITIVE);
        String input = "insert into test ( a , b, c) values (1,2,3)";
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()){
            String group = matcher.group();
            String out = input.substring(0, matcher.start()) + ",d" + matcher.group() + ",sysdate" + input.substring(matcher.end());
            System.out.println(out);

        }
    }

    @Test
    public void testInsert(){
        String originSql = "UPDATE custom SET age=age+100 WHERE name=#{name}";
        Pattern UPDATE_PATTERN = Pattern.compile("update(\\s+\\w+)*\\s+set(\\s*\\w+=\\s*\\S+)(,\\s*\\w+=\\s*\\S+)*", Pattern.CASE_INSENSITIVE);
        Matcher matcherUpdate = UPDATE_PATTERN.matcher(originSql);
        while (matcherUpdate.find()) {
            System.out.println("OtherServiceImplTest.testInsert");
            String table = matcherUpdate.group(1);
            String added = " ," + table + ".update_date"  + "=now()" ;
            String newSql = originSql.substring(0, matcherUpdate.end()) + added + originSql.substring(matcherUpdate.end());
            System.out.println(newSql);
        }
    }

}