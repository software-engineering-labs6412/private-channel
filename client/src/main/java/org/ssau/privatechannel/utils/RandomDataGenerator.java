package org.ssau.privatechannel.utils;

import org.springframework.stereotype.Component;
import org.ssau.privatechannel.model.ConfidentialInfo;

import java.util.*;

@Component
public class RandomDataGenerator {

    private final Random RANDOMIZER = new Random();

    public List<ConfidentialInfo> generate(int count) {
        List<ConfidentialInfo> info = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            ConfidentialInfo currentRecord = ConfidentialInfo.builder()
                    .id(null)
                    .data(generateRandomData())
                    .build();
            info.add(currentRecord);
        }
        return info;
    }

    private Map<String, Object> generateRandomData() {
        int MAX_DATA_ROWS_COUNT = 10;
        int rowsCount = 1 + (Math.abs(RANDOMIZER.nextInt()) % (MAX_DATA_ROWS_COUNT));

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < rowsCount; ++i) {
            result.put(UUID.randomUUID().toString(), UUID.randomUUID() + UUID.randomUUID().toString());
        }
        return result;
    }
}
