package com.zerobase.challengeproject.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDietChallenge is a Querydsl query type for DietChallenge
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDietChallenge extends EntityPathBase<DietChallenge> {

    private static final long serialVersionUID = -743796477L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDietChallenge dietChallenge = new QDietChallenge("dietChallenge");

    public final com.zerobase.challengeproject.account.entity.QBaseEntity _super = new com.zerobase.challengeproject.account.entity.QBaseEntity(this);

    public final com.zerobase.challengeproject.challenge.entity.QChallenge challenge;

    public final ListPath<DietComment, QDietComment> comments = this.<DietComment, QDietComment>createList("comments", DietComment.class, QDietComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Float> currentWeight = createNumber("currentWeight", Float.class);

    public final NumberPath<Float> goalWeight = createNumber("goalWeight", Float.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.zerobase.challengeproject.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDietChallenge(String variable) {
        this(DietChallenge.class, forVariable(variable), INITS);
    }

    public QDietChallenge(Path<? extends DietChallenge> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDietChallenge(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDietChallenge(PathMetadata metadata, PathInits inits) {
        this(DietChallenge.class, metadata, inits);
    }

    public QDietChallenge(Class<? extends DietChallenge> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.challenge = inits.isInitialized("challenge") ? new com.zerobase.challengeproject.challenge.entity.QChallenge(forProperty("challenge"), inits.get("challenge")) : null;
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
    }

}

