package com.sprarta.sproutmarket.domain.interestedItem.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInterestedItem is a Querydsl query type for InterestedItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInterestedItem extends EntityPathBase<InterestedItem> {

    private static final long serialVersionUID = 732373326L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInterestedItem interestedItem = new QInterestedItem("interestedItem");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.sprarta.sproutmarket.domain.item.entity.QItem item;

    public final com.sprarta.sproutmarket.domain.user.entity.QUser user;

    public QInterestedItem(String variable) {
        this(InterestedItem.class, forVariable(variable), INITS);
    }

    public QInterestedItem(Path<? extends InterestedItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInterestedItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInterestedItem(PathMetadata metadata, PathInits inits) {
        this(InterestedItem.class, metadata, inits);
    }

    public QInterestedItem(Class<? extends InterestedItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.sprarta.sproutmarket.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.user = inits.isInitialized("user") ? new com.sprarta.sproutmarket.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

