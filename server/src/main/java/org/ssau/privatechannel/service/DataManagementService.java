package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.constants.UrlSchemas;
import org.ssau.privatechannel.exception.BadRequestException;
import org.ssau.privatechannel.exception.InternalServerErrorException;
import org.ssau.privatechannel.exception.NotFoundException;
import org.ssau.privatechannel.firetasks.NewTryToSendDataTask;
import org.ssau.privatechannel.model.ConfidentialInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DataManagementService {

    private static final String RECEIVER_URL = UrlSchemas.HTTP + "%s" + Endpoints.API_V1_CLIENT + Endpoints.UPLOAD_DATA;
    private static final String SERVER_URL = UrlSchemas.HTTP + "%s" + Endpoints.API_V1_SERVER + Endpoints.UPLOAD_DATA;
    private static final Integer WAIT_TIME_IN_MINUTES = 1;
    private final ConfidentialInfoService confidentialInfoService;
    private final TimerService timerService;
    private final RestTemplate restTemplate;

    @Autowired
    public DataManagementService(ConfidentialInfoService confidentialInfoService, TimerService timerService,
                                 RestTemplate restTemplate) {
        this.confidentialInfoService = confidentialInfoService;
        this.restTemplate = restTemplate;
        this.timerService = timerService;
    }

    public void tryToSendDataToReceiver(List<ConfidentialInfo> confidentialInfo)
            throws BadRequestException, NotFoundException, InternalServerErrorException {
        HttpEntity<List<ConfidentialInfo>> confidentialInfoHttpEntity = new HttpEntity<>(confidentialInfo);
        String receiverIP = confidentialInfo.get(0).getReceiverIP();
        String httpAddress = String.format(RECEIVER_URL, receiverIP);
        ResponseEntity<String> stringResponseEntity;
        try {
            stringResponseEntity =
                    restTemplate.postForEntity(httpAddress, confidentialInfoHttpEntity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Could not send data to client [ip = {}]: {}", receiverIP, e.getMessage());
            confidentialInfoService.addAll(confidentialInfo);

            LocalDateTime nextTryTime = LocalDateTime.now().plusSeconds(WAIT_TIME_IN_MINUTES * 20);
            scheduleNewTransferringTry(nextTryTime, confidentialInfo);
            throw e;
        }
        boolean isStatusSuccessful = stringResponseEntity.getStatusCode().is2xxSuccessful();
        if (!isStatusSuccessful) {
            confidentialInfoService.addAll(confidentialInfo);
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
        NewTryToSendDataTask task = new NewTryToSendDataTask(restTemplate, confidentialInfoService);
        List<Long> ids = new ArrayList<>();

        for (ConfidentialInfo currentRecord : confidentialInfo) {
            ids.add(currentRecord.getId());
        }

        task.setIds(ids);
        task.setServerAddress(SERVER_URL);
        timerService.createTask(task, timestamp);
    }
}
