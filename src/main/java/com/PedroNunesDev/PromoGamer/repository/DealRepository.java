package com.PedroNunesDev.PromoGamer.repository;

import com.PedroNunesDev.PromoGamer.enums.DealEnumStatus;
import com.PedroNunesDev.PromoGamer.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface DealRepository extends JpaRepository<Deal,String> {

    @Query("""
    SELECT d.dealId FROM Deal d
    WHERE d.dealId IN :ids
""")
    Set<String> getExistingDealsId(@Param("ids") Set<String> dealsIdForSearching);

    @Query("""
    SELECT d FROM Deal d
    WHERE d.dealEnumStatus = :enum
""")
    List<Deal> getAllDealsByDealEnumStatus(@Param("enum") DealEnumStatus dealEnumStatus);
}
