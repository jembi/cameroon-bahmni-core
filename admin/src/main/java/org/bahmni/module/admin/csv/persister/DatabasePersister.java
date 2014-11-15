package org.bahmni.module.admin.csv.persister;

import org.apache.log4j.Logger;
import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.EntityPersister;
import org.bahmni.csv.RowResult;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;

public class DatabasePersister<T extends CSVEntity> implements EntityPersister<T> {
    private final EntityPersister<T> persister;
    private final UserContext userContext;
    private static final org.apache.log4j.Logger log = Logger.getLogger(DatabasePersister.class);

    public DatabasePersister(EntityPersister<T> persister) {
        this.persister = persister;
        userContext = Context.getUserContext();
    }

    @Override
    public RowResult<T> persist(T csvEntity) {
        try {
            Context.openSession();
            Context.setUserContext(userContext);
            return persister.persist(csvEntity);
        } catch (Throwable e) {
            log.error(e);
            Context.clearSession();
            return new RowResult<>(csvEntity, e);
        } finally {
            Context.flushSession();
            Context.closeSession();
        }
    }

    @Override
    public RowResult<T> validate(T csvEntity) {
        return persister.validate(csvEntity);
    }
}