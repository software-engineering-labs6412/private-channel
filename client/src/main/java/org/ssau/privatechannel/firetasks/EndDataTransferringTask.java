package org.ssau.privatechannel.firetasks;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.ssau.privatechannel.constants.FirewallRuleNames;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.service.IpService;
import org.ssau.privatechannel.utils.KeyHolder;
import org.ssau.privatechannel.utils.ThreadsHolder;

import java.util.TimerTask;

@Component
@ComponentScan("org.ssau.privatechannel.config")
public class EndDataTransferringTask extends TimerTask {

    private final String NEIGHBOUR_ADDRESS = System.getProperty(SystemProperties.NEIGHBOUR_IP);
    private static final String STANDARD_MASK = "255.255.255.0";

    private final IpService ipService;

    @Autowired
    public EndDataTransferringTask(IpService ipService) {
        this.ipService = ipService;
    }

    @SneakyThrows
    @Override
    public void run() {
        ipService.enableFirewall();
        ipService.deleteRuleByName(FirewallRuleNames.UNBLOCK_HTTP_PORT);
        ipService.deleteRuleByName(FirewallRuleNames.UNBLOCK_IP);
        ipService.blockHttpPort(FirewallRuleNames.BLOCK_HTTP_PORT);
        ipService.blockIP(new IpService.IpAddress(NEIGHBOUR_ADDRESS, STANDARD_MASK), FirewallRuleNames.BLOCK_IP);

        KeyHolder.dropKey();
        ThreadsHolder.removeAndStopById(StartDataTransferringTask.THREAD_NAME);
    }
}
