package com.sprarta.sproutmarket.domain.areas.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAdministrativeArea is a Querydsl query type for AdministrativeArea
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdministrativeArea extends EntityPathBase<AdministrativeArea> {

    private static final long serialVersionUID = 1660918949L;

    public static final QAdministrativeArea administrativeArea = new QAdministrativeArea("administrativeArea");

    public final StringPath admCd = createString("admCd");

    public final StringPath admCd2 = createString("admCd2");

    public final ComparablePath<org.locationtech.jts.geom.Point> admCenter = createComparable("admCenter", org.locationtech.jts.geom.Point.class);

    public final StringPath admNm = createString("admNm");

    public final ComparablePath<org.locationtech.jts.geom.MultiPolygon> geometry = createComparable("geometry", org.locationtech.jts.geom.MultiPolygon.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath sgg = createString("sgg");

    public final StringPath sggnm = createString("sggnm");

    public final StringPath sido = createString("sido");

    public final StringPath sidonm = createString("sidonm");

    public QAdministrativeArea(String variable) {
        super(AdministrativeArea.class, forVariable(variable));
    }

    public QAdministrativeArea(Path<? extends AdministrativeArea> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdministrativeArea(PathMetadata metadata) {
        super(AdministrativeArea.class, metadata);
    }

}

