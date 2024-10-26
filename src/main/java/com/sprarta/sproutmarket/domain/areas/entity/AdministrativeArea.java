package com.sprarta.sproutmarket.domain.areas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "adm_center", columnDefinition = "POINT NOT NULL")
    private Point admCenter;

    @Builder
    public AdministrativeArea(String admNm, String admCd2, String sgg, String sido, String sidonm,
                              String sggnm, String admCd, MultiPolygon geometry, Point admCenter) {
        this.admNm = admNm;
        this.admCd2 = admCd2;
        this.sgg = sgg;
        this.sido = sido;
        this.sidonm = sidonm;
        this.sggnm = sggnm;
        this.admCd = admCd;
        this.geometry = geometry;
        this.admCenter = admCenter;
    }
}
