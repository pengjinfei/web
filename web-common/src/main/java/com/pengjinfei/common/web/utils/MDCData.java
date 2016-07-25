package com.pengjinfei.common.web.utils;

import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * Created by Pengjinfei on 16/6/28.
 * Description:
 */
public class MDCData implements Serializable {
    private String requestId;
    private String uid;

    public MDCData() {
    }

    public MDCData(String requestId, String uid) {
        this.requestId = requestId;
        this.uid = uid;
    }

    @Override
    public String toString() {
        String res = "";
        if (StringUtils.hasText(uid)) {
            res+="U="+uid;
        }
        if (StringUtils.hasText(requestId)) {
            if (!res.equals("")) {
                res += ",";
            }
            res+="T="+requestId;
        }
        if (!res.equals("")) {
            res = "{" + res + "}";
        }
        return res;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
