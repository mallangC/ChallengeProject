package com.zerobase.challengeproject.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDietComment is a Querydsl query type for DietComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDietComment extends EntityPathBase<DietComment> {

    private static final long serialVersionUID = -1728926977L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDietComment dietComment = new QDietComment("dietComment");

    public final com.zerobase.challengeproject.account.entity.QBaseEntity _super = new com.zerobase.challengeproject.account.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Float> currentWeight = createNumber("currentWeight", Float.class);

    public final QDietChallenge dietChallenge;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final com.zerobase.challengeproject.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDietComment(String variable) {
        this(DietComment.class, forVariable(variable), INITS);
    }

    public QDietComment(Path<? extends DietComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDietComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDietComment(PathMetadata metadata, PathInits inits) {
        this(DietComment.class, metadata, inits);
    }

    public QDietComment(Class<? extends DietComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dietChallenge = inits.isInitialized("dietChallenge") ? new QDietChallenge(forProperty("dietChallenge"), inits.get("dietChallenge")) : null;
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
    }

}

