package com.sprarta.sproutmarket.domain.category.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategory is a Querydsl query type for Category
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategory extends EntityPathBase<Category> {

    private static final long serialVersionUID = -2095397998L;

    public static final QCategory category = new QCategory("category");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory, com.sprarta.sproutmarket.domain.interestedCategory.entity.QInterestedCategory> interestedCategories = this.<com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory, com.sprarta.sproutmarket.domain.interestedCategory.entity.QInterestedCategory>createList("interestedCategories", com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory.class, com.sprarta.sproutmarket.domain.interestedCategory.entity.QInterestedCategory.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final EnumPath<com.sprarta.sproutmarket.domain.common.entity.Status> status = createEnum("status", com.sprarta.sproutmarket.domain.common.entity.Status.class);

    public QCategory(String variable) {
        super(Category.class, forVariable(variable));
    }

    public QCategory(Path<? extends Category> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCategory(PathMetadata metadata) {
        super(Category.class, metadata);
    }

}

