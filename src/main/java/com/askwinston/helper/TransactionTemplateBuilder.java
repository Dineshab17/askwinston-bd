package com.askwinston.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class TransactionTemplateBuilder {

    private final PlatformTransactionManager transactionManager;
    private final Integer timeout;
    private final Boolean readOnly;
    private final Integer isolationLevel;
    private final Integer propagationBehavior;

    @Autowired
    protected TransactionTemplateBuilder(PlatformTransactionManager transactionManager) {
        this(transactionManager, null, null, null, null);
    }

    private TransactionTemplateBuilder(PlatformTransactionManager transactionManager, Integer timeout, Boolean readOnly,
                                       Integer isolationLevel, Integer propagationBehavior) {
        super();
        this.transactionManager = transactionManager;
        this.timeout = timeout;
        this.readOnly = readOnly;
        this.isolationLevel = isolationLevel;
        this.propagationBehavior = propagationBehavior;
    }

    public TransactionTemplateBuilder withTimeout(int timeout) {
        return new TransactionTemplateBuilder(transactionManager, timeout, readOnly, isolationLevel, propagationBehavior);
    }

    public TransactionTemplateBuilder withReadOnly(boolean readOnly) {
        return new TransactionTemplateBuilder(transactionManager, timeout, readOnly, isolationLevel, propagationBehavior);
    }

    public TransactionTemplateBuilder withIsolationLevel(int isolationLevel) {
        return new TransactionTemplateBuilder(transactionManager, timeout, readOnly, isolationLevel, propagationBehavior);
    }

    public TransactionTemplateBuilder withPropagationBehavior(int propagationBehavior) {
        return new TransactionTemplateBuilder(transactionManager, timeout, readOnly, isolationLevel, propagationBehavior);
    }

    public TransactionTemplate build() {
        TransactionTemplate template = new TransactionTemplate();
        template.setTransactionManager(transactionManager);
        if (timeout != null) template.setTimeout(timeout);
        if (readOnly != null) template.setReadOnly(readOnly);
        if (isolationLevel != null) template.setIsolationLevel(isolationLevel);
        if (propagationBehavior != null) template.setPropagationBehavior(propagationBehavior);
        return template;
    }

    public TransactionTemplate requiresNew() {
        return withPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW).build();
    }

}
