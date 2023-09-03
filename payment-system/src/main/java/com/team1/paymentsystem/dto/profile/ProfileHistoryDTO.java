package com.team1.paymentsystem.dto.profile;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileHistoryDTO extends AbstractProfileDTO {
    private LocalDateTime timeStamp;
    private String stringTimeStamp;
}
