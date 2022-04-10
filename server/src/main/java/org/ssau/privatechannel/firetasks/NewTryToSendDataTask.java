package org.ssau.privatechannel.firetasks;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.service.ConfidentialInfoService;
import org.ssau.privatechannel.utils.SystemContext;

import java.util.List;
import java.util.TimerTask;

public class NewTryToSendDataTask extends TimerTask {

    private final RestTemplate restTemplate;
    private final ConfidentialInfoService infoService;

    private List<Long> ids;

    private String serverAddress;

    public NewTryToSendDataTask(RestTemplate restTemplate,
                                ConfidentialInfoService infoService) {
        this.restTemplate = restTemplate;
        this.infoService = infoService;
    }

    @Override
    public void run() {

        List<ConfidentialInfo> data = infoService.findAllByIds(ids);
        infoService.deleteBatch(data);

        HttpHeaders headers = new HttpHeaders();
        headers.add(SystemProperties.HEADER_KEY, SystemContext.getProperty(SystemProperties.HEADER_KEY));

        HttpEntity<?> entity = new HttpEntity<>(data, headers);
        String appPort = SystemContext.getProperty(SystemProperties.APP_PORT);
        serverAddress = String.format(serverAddress, "127.0.0.1:" + appPort);
        restTemplate.postForEntity(serverAddress, entity, String.class);
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

}
