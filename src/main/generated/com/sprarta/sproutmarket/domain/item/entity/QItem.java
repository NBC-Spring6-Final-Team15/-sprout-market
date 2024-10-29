package com.sprarta.sproutmarket.domain.item.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = -653969732L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final com.sprarta.sproutmarket.domain.common.QTimestamped _super = new com.sprarta.sproutmarket.domain.common.QTimestamped(this);

    public final com.sprarta.sproutmarket.domain.category.entity.QCategory category;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.sprarta.sproutmarket.domain.image.entity.Image, com.sprarta.sproutmarket.domain.image.entity.QImage> images = this.<com.sprarta.sproutmarket.domain.image.entity.Image, com.sprarta.sproutmarket.domain.image.entity.QImage>createList("images", com.sprarta.sproutmarket.domain.image.entity.Image.class, com.sprarta.sproutmarket.domain.image.entity.QImage.class, PathInits.DIRECT2);

    public final EnumPath<ItemSaleStatus> itemSaleStatus = createEnum("itemSaleStatus", ItemSaleStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final com.sprarta.sproutmarket.domain.user.entity.QUser seller;

    public final EnumPath<com.sprarta.sproutmarket.domain.common.entity.Status> status = createEnum("status", com.sprarta.sproutmarket.domain.common.entity.Status.class);

    public final StringPath title = createString("title");

    public QItem(String variable) {
        this(Item.class, forVariable(variable), INITS);
    }

    public QItem(Path<? extends Item> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItem(PathMetadata metadata, PathInits inits) {
        this(Item.class, metadata, inits);
    }

    public QItem(Class<? extends Item> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.sprarta.sproutmarket.domain.category.entity.QCategory(forProperty("category")) : null;
        this.seller = inits.isInitialized("seller") ? new com.sprarta.sproutmarket.domain.user.entity.QUser(forProperty("seller")) : null;
    }

}

