package com.hytcshare.springCloudConsumer.remote;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 熔断器备用类
 */
@Component
public class HelloRemoteHystrix implements HelloRemote{
    @Override
    public String hello(@RequestParam(value="name") String name) {
        return "hello,"+name+", this message is from hystrix!";
    }
}
