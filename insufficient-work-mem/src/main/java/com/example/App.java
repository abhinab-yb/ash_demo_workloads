package com.example;

import java.sql.*;
import java.lang.*;

public class App implements Runnable {

    private Statement s;

    public App(Statement s) {
        this.s = s;
    }

    public void run() {
        try {
            while (true) {
                this.s.execute("SELECT * FROM demo_table1, demo_table2 ORDER BY demo_table1.k ASC");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void init(Statement s) throws Exception {
        s.execute("DROP TABLE IF EXISTS demo_table2");
        s.execute("DROP TABLE IF EXISTS demo_table1");
        s.execute("CREATE TABLE demo_table1 (k INT, v int)");
        s.execute("CREATE TABLE demo_table2 (k INT, v int)");
        for (int i = 1; i <= 500; ++i) {
            s.execute(String.format("INSERT INTO demo_table1 VALUES (%d, %d)", i, i));
            s.execute(String.format("INSERT INTO demo_table2 VALUES (%d, %d)", i, i));
        }
    }

    public static void main( String[] args) throws Exception {
        Connection c = DriverManager.getConnection("jdbc:yugabytedb://localhost:5433/yugabyte","yugabyte","yugabyte");
        Statement s = c.createStatement();
        s.execute("DROP DATABASE IF EXISTS db1");
        s.execute("DROP DATABASE IF EXISTS db2");
        s.execute("CREATE DATABASE db1");
        s.execute("CREATE DATABASE db2");
        Connection c1 = DriverManager.getConnection("jdbc:yugabytedb://localhost:5433/db1","yugabyte","yugabyte");
        Statement s1 = c1.createStatement();
        init(s1);
        s1.execute("SET work_mem = 32768");
        Connection c2 = DriverManager.getConnection("jdbc:yugabytedb://localhost:5433/db2","yugabyte","yugabyte");
        Statement s2 = c2.createStatement();
        init(s2);
        Thread t1 = new Thread(new App(s1));
        Thread t2 = new Thread(new App(s2));
        t1.start();
        t2.start();
    }
}
