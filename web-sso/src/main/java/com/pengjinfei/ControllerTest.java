package com.pengjinfei;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pengjinfei on 16/2/16.
 * Description:
 */
@Controller
public class ControllerTest {

    @RequestMapping("/test/{name}")
    @ResponseBody
    public Object testController(@PathVariable String name) {
        Map<String, String> res = new HashMap<String, String>();
        res.put("name",name);
        return res;
    }
}
