package org.ssau.privatechannel.firetasks;

import lombok.SneakyThrows;
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
import java.util.TimerTask;

@Component
@ComponentScan("org.ssau.privatechannel.config")
public class StartDataTransferringTask extends TimerTask {

    private final String NEIGHBOUR_ADDRESS = System.getProperty(SystemProperties.NEIGHBOUR_IP);

    private static final String STANDARD_MASK = "255.255.255.0";
    private static final String SEND_DATA_ENDPOINT = "/api/v1/upload-data";
    private static final String SCHEMA = "http://";

    private final ConfidentialInfoService infoService;
    private final IpService ipService;
    private final RestTemplate restTemplate;

    public static final String THREAD_NAME = "DataTransferringFromClient";

    private static abstract class Headers {
        public static final String KEY = "X-Request-Key";
    }

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
        ipService.unblockHttpPort(FirewallRuleNames.UNBLOCK_HTTP_PORT);
        ipService.unblockIP(new IpService.IpAddress(NEIGHBOUR_ADDRESS, STANDARD_MASK), FirewallRuleNames.UNBLOCK_IP);
        Thread thread = new Thread(() -> {
            while(true)

            {
                Collection<ConfidentialInfo> batch = infoService.nextBatch();

                String neighbourUrl = SCHEMA + NEIGHBOUR_ADDRESS + SEND_DATA_ENDPOINT;
                HttpHeaders headers = new HttpHeaders();
                headers.add(Headers.KEY, KeyHolder.getKey());

                HttpEntity<Collection<ConfidentialInfo>> entity = new HttpEntity<>(batch, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(neighbourUrl, entity, String.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    break;
                }
            }
        });
        ThreadsHolder.addAndRunThread(THREAD_NAME, thread);
    }
}
