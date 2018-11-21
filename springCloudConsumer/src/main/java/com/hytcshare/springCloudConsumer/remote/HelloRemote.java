package com.hytcshare.springCloudConsumer.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//fallback即为熔断扩展所需类
@FeignClient(name="spring-cloud-producer",fallback = HelloRemoteHystrix.class)
public interface HelloRemote {
    @RequestMapping(value="/hello")
    public String hello(@RequestParam(value="name") String name);
}
