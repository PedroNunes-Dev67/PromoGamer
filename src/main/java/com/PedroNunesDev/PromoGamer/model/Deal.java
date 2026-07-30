package com.PedroNunesDev.PromoGamer.model;

import com.PedroNunesDev.PromoGamer.enums.DealEnumStatus;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal implements Persistable<String>,Serializable {

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

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public @Nullable String getId() {
        return this.dealId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}