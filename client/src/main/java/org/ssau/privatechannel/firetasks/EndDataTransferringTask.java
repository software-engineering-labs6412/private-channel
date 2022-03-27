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
        ipService.deleteRuleByName(FirewallRuleNames.UNBLOCK_IP);

        String serverIp = SystemContext.getProperty(SystemProperties.SERVER_IP);
        String receiverIp = SystemContext.getProperty(SystemProperties.RECEIVER_IP);

        ipService.blockIP(new IpService.IpAddress(serverIp), FirewallRuleNames.BLOCK_IP);
        ipService.blockIP(new IpService.IpAddress(receiverIp), FirewallRuleNames.BLOCK_IP);

        String networkInterface = SystemContext.getProperty(SystemProperties.NETWORK);
        networkAdapterService.disableInterfaces(networkInterface);

        KeyHolder.dropKey();
        ThreadsHolder.removeAndStopById(StartDataTransferringTask.THREAD_NAME);
    }
}
