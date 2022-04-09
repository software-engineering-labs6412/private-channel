package org.ssau.privatechannel.firetasks;

import lombok.SneakyThrows;
import org.ssau.privatechannel.constants.FirewallRuleNames;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.service.IpService;
import org.ssau.privatechannel.service.NetworkAdapterService;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.utils.KeyHolder;
import org.ssau.privatechannel.utils.SystemContext;
import org.ssau.privatechannel.utils.ThreadsHolder;

import java.util.TimerTask;

public class EndDataTransferringTask extends TimerTask {

    private final IpService ipService;
    private final NetworkAdapterService networkAdapterService;
    private final ScheduleService scheduleService;

    private final Schedule schedule;

    public EndDataTransferringTask(IpService ipService,
                                   NetworkAdapterService networkAdapterService,
                                   ScheduleService scheduleService,
                                   Schedule schedule) {
        this.ipService = ipService;
        this.networkAdapterService = networkAdapterService;
        this.scheduleService = scheduleService;
        this.schedule = schedule;
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
        scheduleService.delete(schedule);
        ThreadsHolder.removeAndStopById(StartDataTransferringTask.THREAD_NAME);
    }
}
