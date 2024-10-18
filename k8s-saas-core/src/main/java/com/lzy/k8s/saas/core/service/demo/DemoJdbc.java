//package com.lzy.k8s.saas.core.service.demo;
//
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.ConnectionCallback;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//@Service
//public class DemoJdbc {
//
//    @Resource
//    private JdbcTemplate jdbcTemplate;
//
//    public void testUpdate() {
//        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS users (" //
//                + "id BIGINT IDENTITY NOT NULL PRIMARY KEY, " //
//                + "email VARCHAR(100) NOT NULL, " //
//                + "password VARCHAR(100) NOT NULL, " //
//                + "name VARCHAR(100) NOT NULL, " //
//                + "UNIQUE (email))");
//    }
//
//    public void insertUser() {
//        ConnectionCallback<Integer> connectionCallback = new ConnectionCallback<Integer>() {
//
//            @Override
//            public Integer doInConnection(Connection connection) throws SQLException, DataAccessException {
//                // 可以直接使用conn实例，不要释放它，回调结束后JdbcTemplate自动释放:
//                // 在内部手动创建的PreparedStatement、ResultSet必须用try(...)释放:
//                try {
//                    PreparedStatement ps = connection.prepareStatement("insert into users(email, password, name) (?, ?, ?)");
//                    ps.setString(0, "test");
//                    ps.setString(1, "test");
//                    ps.setString(2, "test");
//
//                    int executed = ps.executeUpdate();
//                    ps.close();
//                    return executed;
//                } catch (Throwable throwable) {
//                    return null;
//                }
//            }
//        };
//        jdbcTemplate.execute(connectionCallback);
//    }
//
//}
