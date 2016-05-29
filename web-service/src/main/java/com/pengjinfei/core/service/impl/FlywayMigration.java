package com.pengjinfei.core.service.impl;

import com.pengjinfei.common.lock.Lock;
import com.pengjinfei.core.service.DBMigration;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by Pengjinfei on 16/5/29.
 * Description:
 */

@Service
public class FlywayMigration implements DBMigration{

    @Autowired
    Flyway flyway;

    @PostConstruct
    @Lock("flyway")
    @Override
    public void migrate() throws Exception {
        flyway.migrate();
    }
}
