package com.bytetype.amanises.executer;

import com.bytetype.amanises.service.ParcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GiftRobotScheduler {
    private static final Logger logger = LoggerFactory.getLogger(GiftRobotScheduler.class);


    @Value("${app.schedules.enable}")
    private boolean isScheduleEnabled;

    @Autowired
    private ParcelService parcelService;

    @Scheduled(cron = "${app.schedules.gift-rate}")
    public void sendParcel() {
        if (!isScheduleEnabled) return;

        //parcelService.deliveryParcel();

        logger.info("Gift robot start, send gift!");
    }
}
