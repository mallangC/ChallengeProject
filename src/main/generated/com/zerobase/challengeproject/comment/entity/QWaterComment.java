package com.zerobase.challengeproject.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWaterComment is a Querydsl query type for WaterComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWaterComment extends EntityPathBase<WaterComment> {

    private static final long serialVersionUID = 250201236L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWaterComment waterComment = new QWaterComment("waterComment");

    public final com.zerobase.challengeproject.account.entity.QBaseEntity _super = new com.zerobase.challengeproject.account.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> drinkingIntake = createNumber("drinkingIntake", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final com.zerobase.challengeproject.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QWaterChallenge waterChallenge;

    public QWaterComment(String variable) {
        this(WaterComment.class, forVariable(variable), INITS);
    }

    public QWaterComment(Path<? extends WaterComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWaterComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWaterComment(PathMetadata metadata, PathInits inits) {
        this(WaterComment.class, metadata, inits);
    }

    public QWaterComment(Class<? extends WaterComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
        this.waterChallenge = inits.isInitialized("waterChallenge") ? new QWaterChallenge(forProperty("waterChallenge"), inits.get("waterChallenge")) : null;
    }

}

