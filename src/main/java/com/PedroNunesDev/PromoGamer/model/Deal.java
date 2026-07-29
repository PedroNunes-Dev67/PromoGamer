package com.PedroNunesDev.PromoGamer.model;

import com.PedroNunesDev.PromoGamer.enums.DealEnumStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal implements Serializable {

    @Id
    @Column(name = "deal_id", unique = true)
    private String dealId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, name = "steam_app_id")
    private String steamAppId;

    @Column(name = "steam_rating_percent")
    private String steamRatingPercent;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DealEnumStatus dealEnumStatus;
}