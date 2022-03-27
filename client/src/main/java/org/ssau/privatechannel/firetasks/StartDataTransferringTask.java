package org.ssau.privatechannel.firetasks;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.constants.FirewallRuleNames;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.service.ConfidentialInfoService;
import org.ssau.privatechannel.service.IpService;
import org.ssau.privatechannel.service.NetworkAdapterService;
import org.ssau.privatechannel.utils.KeyHolder;
import org.ssau.privatechannel.utils.SystemContext;
import org.ssau.privatechannel.utils.ThreadsHolder;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StartDataTransferringTask extends TimerTask {

    public static final String THREAD_NAME = "DataTransferringFromClient";
    private static final String SEND_DATA_ENDPOINT = Endpoints.API_V1_SERVER + Endpoints.UPLOAD_DATA;
    private static final String SCHEMA = "http://";

    private final ConfidentialInfoService infoService;
    private final IpService ipService;
    private final NetworkAdapterService networkAdapterService;
    private final RestTemplate restTemplate;

    private String receiverIp;

    private static final Integer WAIT_TIME_FOR_NEW_INFO_SECONDS = 10;
    private static final Integer WAIT_TIMEOUT_SECONDS = 60;

    public StartDataTransferringTask(ConfidentialInfoService infoService,
                                     RestTemplate restTemplate,
                                     IpService ipService,
                                     NetworkAdapterService networkAdapterService) {
        this.infoService = infoService;
        this.restTemplate = restTemplate;
        this.ipService = ipService;
        this.networkAdapterService = networkAdapterService;
    }

    @SneakyThrows
    @Override
    public void run() {
        ipService.deleteRuleByName(FirewallRuleNames.BLOCK_IP);
        String currentInterface = SystemContext.getProperty(SystemProperties.NETWORK);
        networkAdapterService.disableInterfaces(currentInterface);

        String senderIp = SystemContext.getProperty(SystemProperties.CURRENT_IP);
        Thread thread = new Thread(() -> {
            log.info("Data transferring started between clients with ips {} and {}", senderIp, receiverIp);
            int currentWaitTime = 0;
            while (true) {
                Collection<ConfidentialInfo> batch = infoService.nextBatch();

                if (batch.isEmpty()) {

                    if (currentWaitTime >= WAIT_TIMEOUT_SECONDS) {
                        log.info("No more information from client with IP={}. Exiting program", senderIp);
                        log.info("Transferring data between clients with ips {} and {} completed", senderIp, receiverIp);
                        System.exit(0);
                    }

                    log.info("All information sent. Waiting for new info...");
                    currentWaitTime += WAIT_TIME_FOR_NEW_INFO_SECONDS;
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(WAIT_TIME_FOR_NEW_INFO_SECONDS));
                        continue;
                    } catch (InterruptedException e) {
                        log.error("Interrupting thread. Reason: {}", e.getMessage());
                        break;
                    }
                }

                currentWaitTime = 0;

                for (ConfidentialInfo next : batch) {
                    next.setSenderIP(senderIp);
                    next.setReceiverIP(receiverIp);
                }

                String serverIp = SystemContext.getProperty(SystemProperties.SERVER_IP);
                String serverAddress = SCHEMA + serverIp + SEND_DATA_ENDPOINT;
                HttpHeaders headers = new HttpHeaders();
                headers.add(Headers.KEY, KeyHolder.getKey());

                HttpEntity<Collection<ConfidentialInfo>> entity = new HttpEntity<>(batch, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(serverAddress, entity, String.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    log.error("Could not send data to server: server returned status {}", response.getStatusCode());
                    break;
                }
                infoService.deleteBatch(batch);
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

    private static abstract class Headers {
        public static final String KEY = "X-Request-Key";
    }

}
