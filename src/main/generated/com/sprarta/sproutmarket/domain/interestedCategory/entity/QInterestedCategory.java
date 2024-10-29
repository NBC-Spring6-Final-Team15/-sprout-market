package com.sprarta.sproutmarket.domain.interestedCategory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInterestedCategory is a Querydsl query type for InterestedCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInterestedCategory extends EntityPathBase<InterestedCategory> {

    private static final long serialVersionUID = -165105948L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInterestedCategory interestedCategory = new QInterestedCategory("interestedCategory");

    public final com.sprarta.sproutmarket.domain.category.entity.QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.sprarta.sproutmarket.domain.user.entity.QUser user;

    public QInterestedCategory(String variable) {
        this(InterestedCategory.class, forVariable(variable), INITS);
    }

    public QInterestedCategory(Path<? extends InterestedCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInterestedCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInterestedCategory(PathMetadata metadata, PathInits inits) {
        this(InterestedCategory.class, metadata, inits);
    }

    public QInterestedCategory(Class<? extends InterestedCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.sprarta.sproutmarket.domain.category.entity.QCategory(forProperty("category")) : null;
        this.user = inits.isInitialized("user") ? new com.sprarta.sproutmarket.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

