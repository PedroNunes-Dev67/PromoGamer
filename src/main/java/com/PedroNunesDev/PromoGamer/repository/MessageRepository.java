package com.PedroNunesDev.PromoGamer.repository;

import com.PedroNunesDev.PromoGamer.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
