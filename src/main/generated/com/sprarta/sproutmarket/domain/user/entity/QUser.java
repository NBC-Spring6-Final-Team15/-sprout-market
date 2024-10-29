package com.sprarta.sproutmarket.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1164655572L;

    public static final QUser user = new QUser("user");

    public final com.sprarta.sproutmarket.domain.common.QTimestamped _super = new com.sprarta.sproutmarket.domain.common.QTimestamped(this);

    public final StringPath address = createString("address");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory, com.sprarta.sproutmarket.domain.interestedCategory.entity.QInterestedCategory> interestedCategories = this.<com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory, com.sprarta.sproutmarket.domain.interestedCategory.entity.QInterestedCategory>createList("interestedCategories", com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory.class, com.sprarta.sproutmarket.domain.interestedCategory.entity.QInterestedCategory.class, PathInits.DIRECT2);

    public final ListPath<com.sprarta.sproutmarket.domain.interestedItem.entity.InterestedItem, com.sprarta.sproutmarket.domain.interestedItem.entity.QInterestedItem> interestedItems = this.<com.sprarta.sproutmarket.domain.interestedItem.entity.InterestedItem, com.sprarta.sproutmarket.domain.interestedItem.entity.QInterestedItem>createList("interestedItems", com.sprarta.sproutmarket.domain.interestedItem.entity.InterestedItem.class, com.sprarta.sproutmarket.domain.interestedItem.entity.QInterestedItem.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<Integer> rate = createNumber("rate", Integer.class);

    public final ListPath<com.sprarta.sproutmarket.domain.report.entity.Report, com.sprarta.sproutmarket.domain.report.entity.QReport> reports = this.<com.sprarta.sproutmarket.domain.report.entity.Report, com.sprarta.sproutmarket.domain.report.entity.QReport>createList("reports", com.sprarta.sproutmarket.domain.report.entity.Report.class, com.sprarta.sproutmarket.domain.report.entity.QReport.class, PathInits.DIRECT2);

    public final ListPath<com.sprarta.sproutmarket.domain.review.entity.Review, com.sprarta.sproutmarket.domain.review.entity.QReview> reviews = this.<com.sprarta.sproutmarket.domain.review.entity.Review, com.sprarta.sproutmarket.domain.review.entity.QReview>createList("reviews", com.sprarta.sproutmarket.domain.review.entity.Review.class, com.sprarta.sproutmarket.domain.review.entity.QReview.class, PathInits.DIRECT2);

    public final EnumPath<com.sprarta.sproutmarket.domain.common.entity.Status> status = createEnum("status", com.sprarta.sproutmarket.domain.common.entity.Status.class);

    public final StringPath username = createString("username");

    public final EnumPath<com.sprarta.sproutmarket.domain.user.enums.UserRole> userRole = createEnum("userRole", com.sprarta.sproutmarket.domain.user.enums.UserRole.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

