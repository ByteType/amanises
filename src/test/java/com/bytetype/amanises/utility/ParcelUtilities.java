package com.bytetype.amanises.utility;

import com.bytetype.amanises.model.Parcel;
import com.bytetype.amanises.model.ParcelStatus;
import com.bytetype.amanises.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParcelUtilities {

    public record UserPair(User sender, User recipient) { }

    public static List<Parcel> createParcels(List<UserPair> pairs) {
        List<Parcel> parcels = new ArrayList<>();
        Random random = new Random();
        for (UserPair pair: pairs) {
            Parcel parcel = new Parcel();
            parcel.setSender(pair.sender);
            parcel.setRecipient(pair.recipient);
            parcel.setWidth(random.nextDouble() * 10.0);
            parcel.setHeight(random.nextDouble() * 5.0);
            parcel.setDepth(random.nextDouble() * 2.0);
            parcel.setMass(random.nextDouble() * 1.5);
            parcel.setStatus(ParcelStatus.READY_FOR_PICKUP);
            parcel.setReadyForPickupAt(LocalDateTime.now());
            parcel.setPickupCode(generateCode(4));
            parcels.add(parcel);
        }
        return parcels;
    }

    public static List<Parcel> createParcels(List<User> users, int parcelsPerUser) {
        List<UserPair> userPairs = new ArrayList<>();
        Random random = new Random();
        for (User user : users) {
            for (int i = 0; i < parcelsPerUser; i++) {
                User randomRecipient;
                do {
                    randomRecipient = users.get(random.nextInt(users.size()));
                } while (randomRecipient.equals(user));
                userPairs.add(new UserPair(user, randomRecipient));
            }
        }
        return createParcels(userPairs);
    }

    public static String generateCode(int length) {
        Random random = new Random();
        if (length <= 0) throw new IllegalArgumentException("Length must be positive");
        int max = (int)Math.pow(10, length);

        return String.format("%04d", random.nextInt(max));
    }
}
