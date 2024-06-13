package com.example;

// import java.sql.*;
// import java.lang.*;
// import com.yugabyte.copy.CopyManager;
// import com.yugabyte.core.BaseConnection;
// import java.io.BufferedReader; 
// import java.io.FileReader; 

import java.net.InetSocketAddress;
import java.util.List;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

/**
 * Hello world!
 *
 */

public class App extends Thread {
    public void run() {
                CqlSession session = CqlSession
                .builder()
                .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
                .withLocalDatacenter("datacenter1")
                .build();
        PreparedStatement preparedUpdate = session.prepare("UPDATE ybdemo.demo_table SET v = v + 1 WHERE k = ?");
        while (true) {
            session.execute(preparedUpdate.bind(1));
        }
    }

  public static void main(String[] args) throws Exception {
        try {
            // Create a YCQL client.
            CqlSession session = CqlSession
                .builder()
                .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
                .withLocalDatacenter("datacenter1")
                .build();

            // Create keyspace 'ybdemo' if it does not exist.
            String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS ybdemo;";
            session.execute(createKeyspace);

            session.execute("DROP TABLE IF EXISTS ybdemo.demo_table");

            // Create table 'employee', if it does not exist.
            String createTable = "CREATE TABLE ybdemo.demo_table (k INT PRIMARY KEY, v INT) with transactions = { 'enabled' : true };";
            session.execute(createTable);

            PreparedStatement preparedInsert = session.prepare("INSERT INTO ybdemo.demo_table (k, v) VALUES (?, ?)");

            // Insert a row.
            session.execute(preparedInsert.bind(1, 1));

            session.close();

            App[] threads;
            threads = new App[5];
            for (int i = 0; i < 5; ++i) {
                threads[i] = new App();
                threads[i].start();
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
