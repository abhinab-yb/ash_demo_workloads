package com.example;

import java.sql.*;
import java.lang.*;
import com.yugabyte.copy.CopyManager;
import com.yugabyte.core.BaseConnection;
import java.io.BufferedReader; 
import java.io.FileReader; 

// import java.net.InetSocketAddress;
// import java.util.List;
// import com.datastax.oss.driver.api.core.CqlSession;
// import com.datastax.oss.driver.api.core.cql.ResultSet;
// import com.datastax.oss.driver.api.core.cql.Row;
// import com.datastax.oss.driver.api.core.cql.BoundStatement;
// import com.datastax.oss.driver.api.core.cql.PreparedStatement;

/**
 * Hello world!
 *
 */

public class App {
    public static void main(String[] args) throws Exception {
        try {
            Connection c = DriverManager.getConnection("jdbc:yugabytedb://localhost:5433/yugabyte","yugabyte","yugabyte");
            Statement s = c.createStatement();
            s.execute("DROP TABLE IF EXISTS n");
            s.execute("create table n(a text, b int, c float)");
            s.execute("insert into n values('abc',1,1.1)");
            s.execute("insert into n values('abc',2,2.2)");
            s.execute("prepare st(text, int, float) as select * from n where a = $1 and b = $2 and c = $3");
            s.execute("execute st('abc',1,1.1)");
            for (int i = 0; i < 300; ++i) {
                System.out.println(String.format("Run %d...", i));
                s.execute("select * from yb_query_diagnostics(-7085925952809948456,10,100,true,true,false,0)");
                s.execute("execute st('abc',1,1.1)");
                System.out.println(String.format("going to sleep..."));
                Thread.sleep(15000);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
