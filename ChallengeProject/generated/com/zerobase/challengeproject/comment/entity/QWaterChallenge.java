package com.zerobase.challengeproject.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWaterChallenge is a Querydsl query type for WaterChallenge
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWaterChallenge extends EntityPathBase<WaterChallenge> {

    private static final long serialVersionUID = -1472095912L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWaterChallenge waterChallenge = new QWaterChallenge("waterChallenge");

    public final com.zerobase.challengeproject.account.entity.QBaseEntity _super = new com.zerobase.challengeproject.account.entity.QBaseEntity(this);

    public final com.zerobase.challengeproject.challenge.entity.QChallenge challenge;

    public final ListPath<WaterComment, QWaterComment> comments = this.<WaterComment, QWaterComment>createList("comments", WaterComment.class, QWaterComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> currentMl = createNumber("currentMl", Integer.class);

    public final NumberPath<Integer> goalMl = createNumber("goalMl", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.zerobase.challengeproject.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QWaterChallenge(String variable) {
        this(WaterChallenge.class, forVariable(variable), INITS);
    }

    public QWaterChallenge(Path<? extends WaterChallenge> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWaterChallenge(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWaterChallenge(PathMetadata metadata, PathInits inits) {
        this(WaterChallenge.class, metadata, inits);
    }

    public QWaterChallenge(Class<? extends WaterChallenge> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.challenge = inits.isInitialized("challenge") ? new com.zerobase.challengeproject.challenge.entity.QChallenge(forProperty("challenge"), inits.get("challenge")) : null;
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
    }

}

