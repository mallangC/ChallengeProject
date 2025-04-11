package com.zerobase.challengeproject.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccountDetail is a Querydsl query type for AccountDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountDetail extends EntityPathBase<AccountDetail> {

    private static final long serialVersionUID = 476690436L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccountDetail accountDetail = new QAccountDetail("accountDetail");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final EnumPath<com.zerobase.challengeproject.type.AccountType> accountType = createEnum("accountType", com.zerobase.challengeproject.type.AccountType.class);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> curAmount = createNumber("curAmount", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isRefunded = createBoolean("isRefunded");

    public final com.zerobase.challengeproject.member.entity.QMember member;

    public final NumberPath<Long> preAmount = createNumber("preAmount", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QAccountDetail(String variable) {
        this(AccountDetail.class, forVariable(variable), INITS);
    }

    public QAccountDetail(Path<? extends AccountDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccountDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccountDetail(PathMetadata metadata, PathInits inits) {
        this(AccountDetail.class, metadata, inits);
    }

    public QAccountDetail(Class<? extends AccountDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.zerobase.challengeproject.member.entity.QMember(forProperty("member")) : null;
    }

}

