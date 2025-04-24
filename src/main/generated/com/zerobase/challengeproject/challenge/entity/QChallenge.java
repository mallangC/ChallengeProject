package com.zerobase.challengeproject.challenge.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChallenge is a Querydsl query type for Challenge
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChallenge extends EntityPathBase<Challenge> {

    private static final long serialVersionUID = 822264595L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChallenge challenge = new QChallenge("challenge");

    public final EnumPath<com.zerobase.challengeproject.type.CategoryType> categoryType = createEnum("categoryType", com.zerobase.challengeproject.type.CategoryType.class);

    public final ListPath<com.zerobase.challengeproject.comment.entity.CoteChallenge, com.zerobase.challengeproject.comment.entity.QCoteChallenge> coteChallenges = this.<com.zerobase.challengeproject.comment.entity.CoteChallenge, com.zerobase.challengeproject.comment.entity.QCoteChallenge>createList("coteChallenges", com.zerobase.challengeproject.comment.entity.CoteChallenge.class, com.zerobase.challengeproject.comment.entity.QCoteChallenge.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> currentParticipant = createNumber("currentParticipant", Long.class);

    public final StringPath description = createString("description");

    public final ListPath<com.zerobase.challengeproject.comment.entity.DietChallenge, com.zerobase.challengeproject.comment.entity.QDietChallenge> dietChallenges = this.<com.zerobase.challengeproject.comment.entity.DietChallenge, com.zerobase.challengeproject.comment.entity.QDietChallenge>createList("dietChallenges", com.zerobase.challengeproject.comment.entity.DietChallenge.class, com.zerobase.challengeproject.comment.entity.QDietChallenge.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Long> maxDeposit = createNumber("maxDeposit", Long.class);

    public final NumberPath<Long> maxParticipant = createNumber("maxParticipant", Long.class);

    public final com.zerobase.challengeproject.member.entity.QMember member;

    public final ListPath<MemberChallenge, QMemberChallenge> memberChallenges = this.<MemberChallenge, QMemberChallenge>createList("memberChallenges", MemberChallenge.class, QMemberChallenge.class, PathInits.DIRECT2);

    public final NumberPath<Long> minDeposit = createNumber("minDeposit", Long.class);

    public final StringPath standard = createString("standard");

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final ListPath<com.zerobase.challengeproject.comment.entity.WaterChallenge, com.zerobase.challengeproject.comment.entity.QWaterChallenge> waterChallenges = this.<com.zerobase.challengeproject.comment.entity.WaterChallenge, com.zerobase.challengeproject.comment.entity.QWaterChallenge>createList("waterChallenges", com.zerobase.challengeproject.comment.entity.WaterChallenge.class, com.zerobase.challengeproject.comment.entity.QWaterChallenge.class, PathInits.DIRECT2);

    public QChallenge(String variable) {
        this(Challenge.class, forVariable(variable), INITS);
    }

    public QChallenge(Path<? extends Challenge> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChallenge(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChallenge(PathMetadata metadata, PathInits inits) {
        this(Challenge.class, metadata, inits);
    }

    public QChallenge(Class<? extends Challenge> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
    }

}

