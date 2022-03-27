package org.ssau.privatechannel.firetasks;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.ssau.privatechannel.constants.FirewallRuleNames;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.service.IpService;
import org.ssau.privatechannel.service.NetworkAdapterService;
import org.ssau.privatechannel.utils.KeyHolder;
import org.ssau.privatechannel.utils.SystemContext;
import org.ssau.privatechannel.utils.ThreadsHolder;

import java.util.TimerTask;

@Component
@ComponentScan("org.ssau.privatechannel.config")
public class EndDataTransferringTask extends TimerTask {

    private final String NEIGHBOUR_ADDRESS = SystemContext.getProperty(SystemProperties.RECEIVER_IP);

    private final IpService ipService;
    private final NetworkAdapterService networkAdapterService;

    @Autowired
    public EndDataTransferringTask(IpService ipService, NetworkAdapterService networkAdapterService) {
        this.ipService = ipService;
        this.networkAdapterService = networkAdapterService;
    }

    @SneakyThrows
    @Override
    public void run() {
        ipService.enableFirewall();
        ipService.deleteRuleByName(FirewallRuleNames.UNBLOCK_HTTP_PORT);
        ipService.deleteRuleByName(FirewallRuleNames.UNBLOCK_IP);
        ipService.blockHttpPort(FirewallRuleNames.BLOCK_HTTP_PORT);
        ipService.blockIP(new IpService.IpAddress(NEIGHBOUR_ADDRESS), FirewallRuleNames.BLOCK_IP);
        ipService.blockIP(new IpService.IpAddress(NEIGHBOUR_ADDRESS), FirewallRuleNames.BLOCK_IP);

        String networkInterface = SystemContext.getProperty(SystemProperties.NETWORK);
        networkAdapterService.disableInterfaces(networkInterface);

        KeyHolder.dropKey();
        ThreadsHolder.removeAndStopById(StartDataTransferringTask.THREAD_NAME);
    }
}
