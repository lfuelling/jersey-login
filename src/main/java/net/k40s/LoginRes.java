package net.k40s;

import javax.ws.rs.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.sql.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("login")
public class LoginRes {
  private SecureRandom random = new SecureRandom();
  private final String DATABASE = "jdbc:sqlite:src/main/resources/users.db";

  /**
   * Method handling HTTP GET requests. The returned object will be sent
   * to the client as "text/plain" media type.
   *
   * @return String that will be returned as a text/plain response.
   */
  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public Response login(String input){

    String email;
    String pass;
    int id = -1;
    int num_rows = 0;

    String[] parsedInput = input.split("&");
    email = parsedInput[0].replaceAll("email=", "");
    email = email.replaceAll("%40", "@"); //Fix for the 'unrecognized token: "40"' error.
    pass = parsedInput[1].replaceAll("pass=", "");

    Connection c = null;
    Statement stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection(DATABASE);
      c.setAutoCommit(false);

      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS WHERE PASSE = '"+ pass + "' AND NAME = '" + email + "';" );
      while ( rs.next() ) {
        num_rows++;
        id = rs.getInt("id");
        System.out.println("User logged in: " + id);
        System.out.println("Number of rows returned: " + num_rows);
      }
      rs.close();
      stmt.close();
      c.close();
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    }
    if(num_rows == 1 && id != -1){
      String token = nextSessionId();
      java.util.Date date = new java.util.Date();
      try {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection(DATABASE);
        c.setAutoCommit(false);
        System.out.println("Opened database successfully");

        stmt = c.createStatement();
        String sql = "UPDATE USERS set LASTT = '" + token + "' where ID="+ id +";";
        stmt.executeUpdate(sql);
        sql = "UPDATE USERS set LASTL = '" + date.getTime() + "' where ID="+ id +";";
        stmt.executeUpdate(sql);
        c.commit();
        stmt.close();
        c.close();
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      }

      return Response.ok(token).build();

    } else {
      return Response.ok("false").build();
    }


}

  @Path("restore")
  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public String restoreSession(String token) {
    int num_rows = 0;
    long lastlog = 0;
    java.util.Date date = new java.util.Date();
    boolean restore = false;

    Connection c = null;
    Statement stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection(DATABASE);
      c.setAutoCommit(false);

      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS WHERE LASTT = '" + token + "';" );
      while ( rs.next() ) {
        num_rows++;
        lastlog = rs.getLong("lastl");
        if(lastlog > date.getTime() - 3600000) {
          System.out.println("Session restored.");
          System.out.println("Number of rows returned: " + num_rows);
          restore = true;
        }
      }
      rs.close();
      stmt.close();
      c.close();
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    }
    if(restore){
      return "true";
    } else {
      return "false";
    }
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String youShouldntBeHere() {

    return "You shouldn't be here.";
  }

  public String nextSessionId() {
    return new BigInteger(130, random).toString(32);
  }
}
