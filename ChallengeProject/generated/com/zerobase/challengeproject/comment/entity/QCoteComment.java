package com.zerobase.challengeproject.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoteComment is a Querydsl query type for CoteComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoteComment extends EntityPathBase<CoteComment> {

    private static final long serialVersionUID = 1469546230L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCoteComment coteComment = new QCoteComment("coteComment");

    public final com.zerobase.challengeproject.account.entity.QBaseEntity _super = new com.zerobase.challengeproject.account.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

    public final QCoteChallenge coteChallenge;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final com.zerobase.challengeproject.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCoteComment(String variable) {
        this(CoteComment.class, forVariable(variable), INITS);
    }

    public QCoteComment(Path<? extends CoteComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCoteComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCoteComment(PathMetadata metadata, PathInits inits) {
        this(CoteComment.class, metadata, inits);
    }

    public QCoteComment(Class<? extends CoteComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coteChallenge = inits.isInitialized("coteChallenge") ? new QCoteChallenge(forProperty("coteChallenge"), inits.get("coteChallenge")) : null;
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
    }

}

