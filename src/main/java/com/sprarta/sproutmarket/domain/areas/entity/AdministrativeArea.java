package com.sprarta.sproutmarket.domain.areas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.MultiPolygon;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "administrative_areas")
public class AdministrativeArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adm_nm", nullable = false)
    private String admNm;

    @Column(name = "adm_cd2")
    private String admCd2;

    @Column(name = "sgg")
    private String sgg;

    @Column(name = "sido")
    private String sido;

    @Column(name = "sidonm")
    private String sidonm;

    @Column(name = "sggnm")
    private String sggnm;

    @Column(name = "adm_cd")
    private String admCd;

    @Column(name = "geometry", columnDefinition = "MULTIPOLYGON NOT NULL")
    private MultiPolygon geometry;
}
