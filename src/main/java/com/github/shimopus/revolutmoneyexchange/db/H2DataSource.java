package com.github.shimopus.revolutmoneyexchange.db;

import com.github.shimopus.revolutmoneyexchange.exceptions.ImpossibleOperationExecution;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class H2DataSource {
    private static final HikariDataSource ds;

    static {
        ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'classpath:db_schema/schema.sql'\\;RUNSCRIPT FROM 'classpath:db_schema/init_data.sql'");
        ds.setUsername("sa");
        ds.setPassword("sa");
        ds.setAutoCommit(false);

        System.out.println("The database has been initialized");
    }

    private H2DataSource() {}

    public static Connection getConnection() throws ImpossibleOperationExecution {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new ImpossibleOperationExecution(e);
        }

    }
}
