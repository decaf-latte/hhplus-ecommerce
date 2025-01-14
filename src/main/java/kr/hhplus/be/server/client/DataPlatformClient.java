package kr.hhplus.be.server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "dataPlatformClient", url = "http://localhost:8080/api")
public interface DataPlatformClient {

    @PostMapping("/orders")
    boolean sendData();
}
