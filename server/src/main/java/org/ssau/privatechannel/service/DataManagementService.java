package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.constants.UrlSchemas;
import org.ssau.privatechannel.exception.BadRequestException;
import org.ssau.privatechannel.exception.InternalServerErrorException;
import org.ssau.privatechannel.exception.NotFoundException;
import org.ssau.privatechannel.firetasks.NewTryToSendDataTask;
import org.ssau.privatechannel.firetasks.StartDataTransferringTask;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.utils.SystemContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DataManagementService {

    private static final String RECEIVER_URL = UrlSchemas.HTTP + "%s" + Endpoints.API_V1_CLIENT + Endpoints.UPLOAD_DATA;
    private static final String SERVER_URL = UrlSchemas.HTTP + "%s" + Endpoints.API_V1_SERVER + Endpoints.UPLOAD_DATA;
    private static final Integer WAIT_TIME_IN_MINUTES = 1;
    private final ConfidentialInfoService infoService;
    private final TimerService timerService;
    private final RestTemplate restTemplate;
    private final IpService ipService;
    private final NetworkAdapterService networkAdapterService;

    private static final Integer TRY_SEND_DATA_DELAY_SECONDS = 10;

    @Autowired
    public DataManagementService(ConfidentialInfoService infoService,
                                 TimerService timerService,
                                 RestTemplate restTemplate,
                                 IpService ipService,
                                 NetworkAdapterService networkAdapterService) {
        this.infoService = infoService;
        this.restTemplate = restTemplate;
        this.timerService = timerService;
        this.ipService = ipService;
        this.networkAdapterService = networkAdapterService;
    }

    public void tryToSendDataToReceiver(List<ConfidentialInfo> confidentialInfo)
            throws BadRequestException, NotFoundException, InternalServerErrorException {

        if (infoService.getInfoCount() > 0) {
            StartDataTransferringTask startTransferringTask =
                    new StartDataTransferringTask(infoService,
                            restTemplate,
                            ipService,
                            networkAdapterService);

            timerService.createTask(startTransferringTask, LocalDateTime.now().plusSeconds(TRY_SEND_DATA_DELAY_SECONDS));
            log.info("Trying to send remaining data from server");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(SystemProperties.HEADER_KEY, SystemContext.getProperty(SystemProperties.HEADER_KEY));

        HttpEntity<List<ConfidentialInfo>> confidentialInfoHttpEntity = new HttpEntity<>(confidentialInfo, headers);
        String receiverIP = confidentialInfo.get(0).getReceiverIP();
        String httpAddress = String.format(RECEIVER_URL, receiverIP);
        ResponseEntity<String> stringResponseEntity;
        try {
            stringResponseEntity =
                    restTemplate.postForEntity(httpAddress, confidentialInfoHttpEntity, String.class);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("Could not send data to client [ip = {}]: {}", receiverIP, e.getMessage());
            infoService.addAll(confidentialInfo);

            LocalDateTime nextTryTime = LocalDateTime.now().plusSeconds(WAIT_TIME_IN_MINUTES * 20);
            scheduleNewTransferringTry(nextTryTime, confidentialInfo);
            throw e;
        }
        boolean isStatusSuccessful = stringResponseEntity.getStatusCode().is2xxSuccessful();
        if (!isStatusSuccessful) {
            infoService.addAll(confidentialInfo);
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                log.error("Wrong request to receiver IP. May be body is incorrect");
                throw new BadRequestException("Wrong request to receiver IP. May be body is incorrect");
            }
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.error("Page not found");
                throw new NotFoundException("Page not found");
            }
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                log.error("Something wrong with client on other side");
                throw new InternalServerErrorException("Something wrong with client on other side");
            }
        }
    }

    private void scheduleNewTransferringTry(LocalDateTime timestamp,
                                            List<ConfidentialInfo> confidentialInfo) {
        NewTryToSendDataTask task = new NewTryToSendDataTask(restTemplate, infoService);
        List<Long> ids = new ArrayList<>();

        for (ConfidentialInfo currentRecord : confidentialInfo) {
            ids.add(currentRecord.getId());
        }

        task.setIds(ids);
        task.setServerAddress(SERVER_URL);
        timerService.createTask(task, timestamp);
    }
}
