package org.ssau.privatechannel.firetasks;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.FirewallRuleNames;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.service.ConfidentialInfoService;
import org.ssau.privatechannel.service.IpService;
import org.ssau.privatechannel.utils.KeyHolder;
import org.ssau.privatechannel.utils.ThreadsHolder;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;

@Slf4j
@Component
@ComponentScan("org.ssau.privatechannel.config")
public class StartDataTransferringTask extends TimerTask {

    private static final String SEND_DATA_ENDPOINT = "/api/v1/server/upload-data";
    private static final String SCHEMA = "http://";

    private final ConfidentialInfoService infoService;
    private final IpService ipService;
    private final RestTemplate restTemplate;

    public static final String THREAD_NAME = "DataTransferringFromClient";

    private static abstract class Headers {
        public static final String KEY = "X-Request-Key";
    }

    private String receiverIp;

    @Autowired
    public StartDataTransferringTask(ConfidentialInfoService infoService,
                                     RestTemplate restTemplate,
                                     IpService ipService) {
        this.infoService = infoService;
        this.restTemplate = restTemplate;
        this.ipService = ipService;
    }

    @SneakyThrows
    @Override
    public void run() {
        ipService.enableFirewall();
        ipService.deleteRuleByName(FirewallRuleNames.BLOCK_HTTP_PORT);
        ipService.deleteRuleByName(FirewallRuleNames.BLOCK_IP);

        String senderIp = System.getProperty(SystemProperties.CURRENT_IP);
        Thread thread = new Thread(() -> {
            while(true)
            {
                Collection<ConfidentialInfo> batch = infoService.nextBatch();

                for (ConfidentialInfo next : batch) {
                    next.setSenderIP(senderIp);
                    next.setReceiverIP(receiverIp);
                }

                String serverIp = System.getProperty(SystemProperties.SERVER_IP);
                String serverAddress = SCHEMA + serverIp + SEND_DATA_ENDPOINT;
                HttpHeaders headers = new HttpHeaders();
                headers.add(Headers.KEY, KeyHolder.getKey());

                HttpEntity<Collection<ConfidentialInfo>> entity = new HttpEntity<>(batch, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(serverAddress, entity, String.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    log.error("Could not send data to server: server returned status {}", response.getStatusCode());
                    break;
                }
            }
        });
        ThreadsHolder.addAndRunThread(THREAD_NAME, thread);
    }

    public String getReceiverIp() {
        return receiverIp;
    }

    public void setReceiverIp(String receiverIp) {
        this.receiverIp = receiverIp;
    }

}
