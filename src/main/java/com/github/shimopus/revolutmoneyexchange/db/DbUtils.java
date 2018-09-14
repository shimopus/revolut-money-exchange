package com.github.shimopus.revolutmoneyexchange.db;

import com.github.shimopus.revolutmoneyexchange.exceptions.ImpossibleOperationExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utilities class contains a number of methods to manipulate with the data base
 *
 * @author Sergey Babinskiy
 */
public class DbUtils {
    private static final Logger log = LoggerFactory.getLogger(DbUtils.class);

    /**
     * The method executes the query passed into the method with the execute method provided
     * This method responds to handle work with the connection, transaction and prepared statement life cycles
     *
     * Example:
     * <PRE>
     *    DbUtils.executeQuery("select * from table", ps -> {
     *         ResultSet rs = ps.executeQuery();
     *         if (rs != null) {
     *             while (rs.next()) {
     *                  System.out.println(rs.getString(1));
     *             }
     *         }
     *    });
     * </PRE>
     *
     * @param query the query string which will be passed into <code>Connection.preparedStatement</code> method
     * @param queryExecutor the executor with only one method accepting <code>PreparedStatement</code> instance created
     *
     * @return query result object with the only method <code>getResult</code> returns the result of queryExecutor
     */
    public static <E> QueryResult<E> executeQuery(String query, QueryExecutor<E> queryExecutor) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = H2DataSource.getConnection();
            preparedStatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            QueryResult<E> qr = new QueryResult<>(queryExecutor.execute(preparedStatement));

            con.commit();

            return qr;
        } catch (Throwable th) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    log.error("Unexpected exception", e);
                }
            }
            log.error("Unexpected exception", th);
            throw new ImpossibleOperationExecution(th);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    log.error("Unexpected exception", e);
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    log.error("Unexpected exception", e);
                }
            }
        }
    }

    public interface QueryExecutor<T> {
        T execute(PreparedStatement preparedStatement) throws SQLException;
    }

    public static class QueryResult<T> {
        private T result;

        QueryResult(T result) {
            this.result = result;
        }

        public T getResult(){
            return result;
        };
    }
}
