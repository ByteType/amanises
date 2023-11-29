package com.bytetype.amanises.utility;

import com.bytetype.amanises.model.Cabinet;
import com.bytetype.amanises.model.CabinetType;
import com.bytetype.amanises.model.Locker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LockerUtilities {

    public record LockerData(List<Locker> lockers, List<Cabinet> cabinets) { }

    public static LockerData createLockers(List<String> locations) {
        List<Locker> lockers = new ArrayList<>();
        List<Cabinet> cabinets = new ArrayList<>();
        Random random = new Random();
        for (String location : locations) {
            Locker locker = new Locker();
            locker.setLocation(location);
            for (int i = 0; i < random.nextInt(5, 10); i++) {
                Cabinet cabinet = new Cabinet();
                cabinet.setLocker(locker);
                cabinet.setType(CabinetType.OPEN);
                cabinets.add(cabinet);
            }
            lockers.add(locker);
        }
        return new LockerData(lockers, cabinets);
    }
}
