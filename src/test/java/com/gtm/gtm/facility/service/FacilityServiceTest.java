package com.gtm.gtm.facility.service;

import com.gtm.gtm.common.error.ConflictException;
import com.gtm.gtm.facility.domain.Facility;
import com.gtm.gtm.facility.dto.FacilityCreateDto;
import com.gtm.gtm.facility.dto.FacilityTreeDto;
import com.gtm.gtm.facility.repository.FacilityRepository;
import com.gtm.gtm.point.repository.PointRepository;
import com.gtm.gtm.site.domain.Site;
import com.gtm.gtm.site.repository.SiteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class FacilityServiceTest {

    @Test
    void listTree_buildsChildren() {
        var repo = Mockito.mock(FacilityRepository.class);
        var siteRepo = Mockito.mock(SiteRepository.class);
        var pointRepo = Mockito.mock(PointRepository.class);

        var service = new FacilityService(repo, siteRepo, pointRepo);

        // Root
        var root = new Facility();
        root.setId(1L);
        var site = new Site(); site.setId(10L); root.setSite(site);
        root.setName("Root"); root.setCode("R");

        Page<Facility> page = new PageImpl<>(List.of(root), PageRequest.of(0, 10), 1);
        when(repo.findAllByParentIsNull(any())).thenReturn(page);
        when(repo.findAllBySite_IdAndParentIsNull(anyLong(), any())).thenReturn(page);

        // Children of root
        var ch1 = new Facility(); ch1.setId(2L); ch1.setSite(site); ch1.setParent(root); ch1.setName("C1"); ch1.setCode("C1");
        var ch2 = new Facility(); ch2.setId(3L); ch2.setSite(site); ch2.setParent(root); ch2.setName("C2"); ch2.setCode("C2");
        when(repo.findAllByParent_Id(1L)).thenReturn(List.of(ch1, ch2));
        when(repo.findAllByParent_Id(2L)).thenReturn(List.of());
        when(repo.findAllByParent_Id(3L)).thenReturn(List.of());

        // No points → empty counts
        when(pointRepo.countByTypeForFacilities(any())).thenReturn(List.of());

        Page<FacilityTreeDto> res = service.listTree(null, null, PageRequest.of(0, 10));
        assertEquals(1, res.getTotalElements());
        var dto = res.getContent().get(0);
        assertEquals(2, dto.children().size());
    }

    @Test
    void create_parentMustBelongToSameSite() {
        var repo = Mockito.mock(FacilityRepository.class);
        var siteRepo = Mockito.mock(SiteRepository.class);
        var pointRepo = Mockito.mock(PointRepository.class);
        var service = new FacilityService(repo, siteRepo, pointRepo);

        when(repo.existsBySite_IdAndCodeIgnoreCase(anyLong(), anyString())).thenReturn(false);

        var site1 = new Site(); site1.setId(1L);
        var site2 = new Site(); site2.setId(2L);
        when(siteRepo.getReferenceById(1L)).thenReturn(site1);

        var parent = new Facility(); parent.setId(100L); parent.setSite(site2);
        when(repo.findById(100L)).thenReturn(Optional.of(parent));

        var dto = new FacilityCreateDto(1L, "Name", "Code", 100L);

        assertThrows(ConflictException.class, () -> service.create(dto));
    }
}
