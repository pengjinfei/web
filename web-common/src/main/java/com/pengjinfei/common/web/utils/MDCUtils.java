package com.pengjinfei.common.web.utils;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Created by Pengjinfei on 16/6/26.
 * Description:
 */
public class MDCUtils {

    public static final String MDC_DATA_KEY = "MDC_Datas";

    public static String get() {
        String mdcData = MDC.get(MDC_DATA_KEY);
        if (!StringUtils.hasText(mdcData)) {
            set(null);
            mdcData = MDC.get(MDC_DATA_KEY);
        }
        return mdcData;
    }

    public static void set(String uid, String requestId) {
        MDCData datas = new MDCData();
        if(uid != null) {
            datas.setUid(uid);
        }

        if(requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        datas.setRequestId(requestId);
        MDC.put(MDC_DATA_KEY,datas.toString());
    }

    public static void set(String uid) {
        set(uid, null);
    }

    public static void clear() {
        MDC.clear();
    }
}
