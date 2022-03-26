package org.ssau.privatechannel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.service.ConfidentialInfoService;

// get data from client
@RestController
@RequestMapping(value = ClientController.Endpoints.API_V1)
public class ClientController {

    public static abstract class Endpoints {
        public static final String API_V1 = "/api/v1";
        private static final String UPLOAD_DATA = "/upload-data";
    }

    private static final String RECEIVER_URL = "http://%s/api/v1/upload-data";

    private final ConfidentialInfoService confidentialInfoService;
    private final RestTemplate restTemplate;

    @Autowired
    public ClientController(ConfidentialInfoService confidentialInfoService, RestTemplate restTemplate) {
        this.confidentialInfoService = confidentialInfoService;
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = Endpoints.UPLOAD_DATA, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void uploadData(@RequestBody ConfidentialInfo confidentialInfo) {

        HttpEntity<ConfidentialInfo> confidentialInfoHttpEntity = new HttpEntity<>(confidentialInfo);
        String ipReceiverPort = confidentialInfo.getReceiverIP();
        String httpAddress = String.format(RECEIVER_URL, ipReceiverPort);
        ResponseEntity<String> stringResponseEntity;
        try {
            stringResponseEntity =
                    restTemplate.postForEntity(httpAddress, confidentialInfoHttpEntity, String.class);
        }
        catch (ResourceAccessException e){
            e.printStackTrace();
            confidentialInfoService.add(confidentialInfo);
            // TODO заскедулить попытку передать сообщение снова ( Димасик)
            throw e;
        }
        boolean isStatusSuccessful = stringResponseEntity.getStatusCode().is2xxSuccessful();
        if (!isStatusSuccessful) {
            confidentialInfoService.add(confidentialInfo);
            // TODO кидать исключения
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) // 400
            {
                System.out.println("400");
            }
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                System.out.println("not found");
            }
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                System.out.println("INTERNAL_SERVER_ERROR");
            }

        }

    }

}
