package kr.hhplus.be.server.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataPlatformService {

    public void sendData(Long orderId) {
        log.info("DataPlatform 전송 완료 -> OrderId = {}", orderId);
    }
}
