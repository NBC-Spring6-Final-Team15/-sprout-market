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

    @Column(name = "geometry", columnDefinition = "MULTIPOLYGON NOT NULL")
    private MultiPolygon geometry;

    @Column(name = "adm_center", columnDefinition = "POINT NOT NULL")
    private Point admCenter;

    private AdministrativeArea(String admNm, MultiPolygon geometry, Point admCenter) {
        this.admNm = admNm;
        this.geometry = geometry;
        this.admCenter = admCenter;
    }

    public static AdministrativeArea of(String admNm, MultiPolygon geometry, Point admCenter) {
        return new AdministrativeArea(admNm, geometry, admCenter);
    }
}
