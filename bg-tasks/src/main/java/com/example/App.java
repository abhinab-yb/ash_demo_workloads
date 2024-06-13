package com.example;

import java.sql.*;
import java.lang.*;

public class App implements Runnable {

    private Connection c;

    public App(Connection c) {
        this.c = c;
    }

    public void run() {
        try {
            Statement s = c.createStatement();
            while (true) {
                s.execute("INSERT INTO demo_bg_tasks SELECT i, i FROM generate_series(1, 100000) AS i");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        Connection c = DriverManager.getConnection("jdbc:yugabytedb://localhost:5433/yugabyte","yugabyte","yugabyte");
        Statement s = c.createStatement();
        s.execute("DROP TABLE IF EXISTS demo_bg_tasks");
        s.execute("CREATE TABLE demo_bg_tasks(k INT, v INT);");
        Thread[] threads;
        threads = new Thread[5];
        for (int i = 0; i < 5; ++i) {
            threads[i] = new Thread(new App(c));
            threads[i].start();
        }
    }
}
