package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.service.ConfidentialInfoService;
import org.ssau.privatechannel.utils.RandomDataGenerator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Endpoints.API_V1_CLIENT)
public class GenerateDataController {

    private final ConfidentialInfoService infoService;
    private final RandomDataGenerator dataGenerator;

    @Autowired
    public GenerateDataController(ConfidentialInfoService infoService,
                                  RandomDataGenerator dataGenerator) {
        this.infoService = infoService;
        this.dataGenerator = dataGenerator;
    }

    @PostMapping(value = Endpoints.GENERATE_DATA)
    public void generate(@PathVariable("count") Integer count) {
        log.debug("Generate test data...");
        List<ConfidentialInfo> data = dataGenerator.generate(count);
        infoService.addAll(data);
    }
}
