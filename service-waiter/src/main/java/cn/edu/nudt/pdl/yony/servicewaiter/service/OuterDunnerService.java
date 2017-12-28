package cn.edu.nudt.pdl.yony.servicewaiter.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * email: yony228@163.com
 * Created by yony on 17-12-25.
 */
@FeignClient(value = "service-dunner")
public interface OuterDunnerService {
        @RequestMapping(value = "/rest/chat/reg", method = RequestMethod.GET)
        String reg(@RequestParam(value = "name") String name);

        @RequestMapping(value = "/rest/chat/interact/{uuid}", method = RequestMethod.GET)
        String interact(@PathVariable(value = "uuid") String uuid, @RequestParam(value = "req") String req);
}
