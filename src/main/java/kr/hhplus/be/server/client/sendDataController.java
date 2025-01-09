package kr.hhplus.be.server.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class sendDataController {

    @PostMapping("/orders")
    public boolean sendData() {
        return true;
    }
}
