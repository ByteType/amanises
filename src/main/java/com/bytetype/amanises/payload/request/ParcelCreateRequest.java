package com.bytetype.amanises.payload.request;

import com.bytetype.amanises.payload.common.ParcelDetailPayload;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public class ParcelCreateRequest extends ParcelDetailPayload {

    private LocalDateTime readyForPickupAt; // Expected arrive time.

    @NotEmpty
    private List<Long> expectedSenderLockers;

    @NotEmpty
    private List<Long> expectedRecipientLockers;

    public LocalDateTime getReadyForPickupAt() {
        return readyForPickupAt;
    }

    public void setReadyForPickupAt(LocalDateTime readyForPickupAt) {
        this.readyForPickupAt = readyForPickupAt;
    }

    public List<Long> getExpectedSenderLockers() {
        return expectedSenderLockers;
    }

    public void setExpectedSenderLockers(List<Long> expectedSenderLockers) {
        this.expectedSenderLockers = expectedSenderLockers;
    }

    public List<Long> getExpectedRecipientLockers() {
        return expectedRecipientLockers;
    }

    public void setExpectedRecipientLockers(List<Long> expectedRecipientLockers) {
        this.expectedRecipientLockers = expectedRecipientLockers;
    }
}
