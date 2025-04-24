package com.zerobase.challengeproject.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRefund is a Querydsl query type for Refund
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefund extends EntityPathBase<Refund> {

    private static final long serialVersionUID = -2137669870L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRefund refund = new QRefund("refund");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QAccountDetail accountDetail;

    public final StringPath adminContent = createString("adminContent");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDone = createBoolean("isDone");

    public final BooleanPath isRefunded = createBoolean("isRefunded");

    public final com.zerobase.challengeproject.member.entity.QMember member;

    public final StringPath memberContent = createString("memberContent");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QRefund(String variable) {
        this(Refund.class, forVariable(variable), INITS);
    }

    public QRefund(Path<? extends Refund> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRefund(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRefund(PathMetadata metadata, PathInits inits) {
        this(Refund.class, metadata, inits);
    }

    public QRefund(Class<? extends Refund> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.accountDetail = inits.isInitialized("accountDetail") ? new QAccountDetail(forProperty("accountDetail"), inits.get("accountDetail")) : null;
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
    }

}

