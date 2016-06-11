package com.pengjinfei.core.po;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
public class Customer  implements Serializable{
    private String id;
    private String name;
    private Integer age;

    private Date create_date;
    private Date update_date;

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
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

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
