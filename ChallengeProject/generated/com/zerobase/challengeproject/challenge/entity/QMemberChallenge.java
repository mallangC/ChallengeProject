package com.zerobase.challengeproject.challenge.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberChallenge is a Querydsl query type for MemberChallenge
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberChallenge extends EntityPathBase<MemberChallenge> {

    private static final long serialVersionUID = -1298669159L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberChallenge memberChallenge = new QMemberChallenge("memberChallenge");

    public final QChallenge challenge;

    public final DateTimePath<java.time.LocalDateTime> entered_at = createDateTime("entered_at", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.zerobase.challengeproject.member.entity.QMember member;

    public final NumberPath<Long> memberDeposit = createNumber("memberDeposit", Long.class);

    public QMemberChallenge(String variable) {
        this(MemberChallenge.class, forVariable(variable), INITS);
    }

    public QMemberChallenge(Path<? extends MemberChallenge> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberChallenge(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberChallenge(PathMetadata metadata, PathInits inits) {
        this(MemberChallenge.class, metadata, inits);
    }

    public QMemberChallenge(Class<? extends MemberChallenge> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.challenge = inits.isInitialized("challenge") ? new QChallenge(forProperty("challenge"), inits.get("challenge")) : null;
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
    }

}

