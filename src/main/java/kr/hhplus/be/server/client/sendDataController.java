package kr.hhplus.be.server.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class SendDataController {

    private final DataPlatformService dataplatformService;

    @PostMapping("/orders")
    public boolean sendData(Long orderId) {
        try {
            dataplatformService.sendData(orderId);
            return true;
        }catch (Exception e) {
            log.error("DataPlatform 전송 실패", e);
            return false;
        }

    }
}
