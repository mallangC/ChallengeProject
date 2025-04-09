package com.zerobase.challengeproject.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoteChallenge is a Querydsl query type for CoteChallenge
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoteChallenge extends EntityPathBase<CoteChallenge> {

    private static final long serialVersionUID = 2087338810L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCoteChallenge coteChallenge = new QCoteChallenge("coteChallenge");

    public final com.zerobase.challengeproject.challenge.entity.QChallenge challenge;

    public final ListPath<CoteComment, QCoteComment> comments = this.<CoteComment, QCoteComment>createList("comments", CoteComment.class, QCoteComment.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath question = createString("question");

    public final DateTimePath<java.time.LocalDateTime> startAt = createDateTime("startAt", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QCoteChallenge(String variable) {
        this(CoteChallenge.class, forVariable(variable), INITS);
    }

    public QCoteChallenge(Path<? extends CoteChallenge> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCoteChallenge(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCoteChallenge(PathMetadata metadata, PathInits inits) {
        this(CoteChallenge.class, metadata, inits);
    }

    public QCoteChallenge(Class<? extends CoteChallenge> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.challenge = inits.isInitialized("challenge") ? new com.zerobase.challengeproject.challenge.entity.QChallenge(forProperty("challenge"), inits.get("challenge")) : null;
    }

}

