package net.k40s;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.security.*;
import java.util.Date;

/**
 * Created by lukas on 04.11.14.
 */
@Path("register")
public class RegRes {
  private SecureRandom random = new SecureRandom();

  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public String getIt(String input) {

    String email;
    String pass;
    boolean exists = false;

    String[] parsedInput = input.split("&");
    email = parsedInput[0].replaceAll("email=", "");
    email = email.replaceAll("%40", "@"); //Fix for the 'unrecognized token: "40"' error.
    pass = parsedInput[1].replaceAll("pass=", "");

    Connection c = null;
    Statement stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection(Consts.DATABASE);
    } catch(Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }

    try {
      stmt = c.createStatement();
      c.setAutoCommit(false);
      String sql = "CREATE TABLE IF NOT EXISTS USERS " +
          "(ID INTEGER PRIMARY KEY       ," +
          " NAME           VARCHAR   NOT NULL, " +
          " PASSE          VARCHAR   NOT NULL," +
          " LASTL          TIMESTAMP," +
          " LASTT          VARCHAR)";
      stmt.executeUpdate(sql);
      stmt.close();
      c.commit();
      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM USERS WHERE PASSE = '" + pass + "' AND NAME = '" + email + "';");
      while(rs.next()) {
        exists = true;
      }
      rs.close();
      stmt.close();
      c.close();
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection(Consts.DATABASE);
      c.setAutoCommit(false);
      stmt = c.createStatement();
      String token = nextSessionId();
      sql = "INSERT INTO USERS (NAME, PASSE) " +
          "VALUES ('" + email + "','" + pass + "');";
      stmt.executeUpdate(sql);
      System.out.println("New user: " + email);
      stmt.close();
      c.commit();
      c.close();
    } catch(Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    if(exists == false) {
      return "true";
    } else {
      return "false";
    }

  }

  public String nextSessionId() {
    return new BigInteger(130, random).toString(32);
  }
}
