package com.sprarta.sproutmarket.domain.trade.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrade is a Querydsl query type for Trade
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrade extends EntityPathBase<Trade> {

    private static final long serialVersionUID = -1126253508L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrade trade = new QTrade("trade");

    public final com.sprarta.sproutmarket.domain.common.QTimestamped _super = new com.sprarta.sproutmarket.domain.common.QTimestamped(this);

    public final com.sprarta.sproutmarket.domain.user.entity.QUser buyer;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.sprarta.sproutmarket.domain.item.entity.QItem item;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.sprarta.sproutmarket.domain.user.entity.QUser seller;

    public final EnumPath<com.sprarta.sproutmarket.domain.trade.enums.TradeStatus> tradeStatus = createEnum("tradeStatus", com.sprarta.sproutmarket.domain.trade.enums.TradeStatus.class);

    public QTrade(String variable) {
        this(Trade.class, forVariable(variable), INITS);
    }

    public QTrade(Path<? extends Trade> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrade(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrade(PathMetadata metadata, PathInits inits) {
        this(Trade.class, metadata, inits);
    }

    public QTrade(Class<? extends Trade> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.buyer = inits.isInitialized("buyer") ? new com.sprarta.sproutmarket.domain.user.entity.QUser(forProperty("buyer")) : null;
        this.item = inits.isInitialized("item") ? new com.sprarta.sproutmarket.domain.item.entity.QItem(forProperty("item"), inits.get("item")) : null;
        this.seller = inits.isInitialized("seller") ? new com.sprarta.sproutmarket.domain.user.entity.QUser(forProperty("seller")) : null;
    }

}

