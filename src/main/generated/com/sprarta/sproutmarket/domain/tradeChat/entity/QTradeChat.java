package com.sprarta.sproutmarket.domain.tradeChat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTradeChat is a Querydsl query type for TradeChat
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradeChat extends EntityPathBase<TradeChat> {

    private static final long serialVersionUID = -1726117188L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTradeChat tradeChat = new QTradeChat("tradeChat");

    public final com.sprarta.sproutmarket.domain.common.QTimestamped _super = new com.sprarta.sproutmarket.domain.common.QTimestamped(this);

    public final EnumPath<ChatReadStatus> chatReadStatus = createEnum("chatReadStatus", ChatReadStatus.class);

    public final QChatRoom chatRoom;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public QTradeChat(String variable) {
        this(TradeChat.class, forVariable(variable), INITS);
    }

    public QTradeChat(Path<? extends TradeChat> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTradeChat(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTradeChat(PathMetadata metadata, PathInits inits) {
        this(TradeChat.class, metadata, inits);
    }

    public QTradeChat(Class<? extends TradeChat> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
    }

}

