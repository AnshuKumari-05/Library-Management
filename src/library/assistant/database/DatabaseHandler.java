package library.assistant.database;


import java.sql.*;
import library.assistant.ui.listbook.BookListController.Book;
import library.assistant.ui.listmember.MemberListController.Member;

public class DatabaseHandler {

    private static DatabaseHandler handler = null;
    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

    private DatabaseHandler(){
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
    }

    public static DatabaseHandler getInstance(){

        if(handler == null)
        {
            handler = new DatabaseHandler();
        }

        return handler;
    }

    private void setupMemberTable() {
        String TABLE_NAME= "MEMBER";
        try{
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);

            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + "already exists, Ready to go!");
            }else{

                String Query = "CREATE TABLE " + TABLE_NAME + "("
                        +  "id varchar(200) primary key,\n"
                        +  "name varchar(200),\n"
                        +  "mobile varchar(200),\n"
                        +  "email varchar(100)\n"
                        +  ")";
                System.out.println(Query);
                stmt.execute(Query);
                System.out.println("Table created\n");
            }

        } catch (SQLException e){
            System.err.println(e.getMessage() + "....setupDatabase");
        }
    }

    private static void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setupBookTable(){
        String TABLE_NAME= "BOOK";
        try{
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);

            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + "already exists, Ready to go!");
            }else{

                String Query = "CREATE TABLE " + TABLE_NAME + "("
                        +  "id varchar(200) primary key,\n"
                        +  "title varchar(200),\n"
                        +  "author varchar(200),\n"
                        +  "publisher varchar(100),\n"
                        +  "isAvail boolean default true"
                        +  ")";
                System.out.println(Query);
                stmt.execute(Query);
                System.out.println("Table created\n");
            }

        } catch (SQLException e){
            System.err.println(e.getMessage() + "....setupDatabase");
        }
    }

    void setupIssueTable(){
        String TABLE_NAME = "ISSUE";
        try{

            stmt =conn.createStatement();
            DatabaseMetaData dbm =conn.getMetaData();

            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);

            if(tables.next()){
                System.out.println("Table "+TABLE_NAME +"already exists!");
            }
            else
            {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + " bookID varchar(200) primary key,\n"
                        + " memberID varchar(200),\n"
                        + " issueTime timestamp default CURRENT_TIMESTAMP,\n"
                        + " renew_count integer default 0,\n"
                        + " FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                        + " FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                        +")");
            }

        }catch ( SQLException e){
            e.printStackTrace();
        }

    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        }
        catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        }
        finally {
        }
        return result;
    }

    public boolean execAction(String qu) {
        try{
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        }catch(SQLException e)
        {
            System.out.println("Exception occured");
            return false;
        }
    }

    public boolean deleteBook(Book book)
    {
        try {
            String deleteStatement = "DELETE FROM BOOK WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, book.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isBookAlreadyIssued(Book book)
    {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE bookid=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, book.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        }
        catch (SQLException ex) {
           // LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }
    public boolean updateBook(Book book) {
        try {
            String update = "UPDATE BOOK SET TITLE=?, AUTHOR=?, PUBLISHER=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());
            int res = stmt.executeUpdate();
            return (res > 0);
        }
        catch (SQLException ex) {
          //  LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }
    public boolean updateMember(Member member){
        try{
            String update = "UPDATE MEMBER SET NAME=?, EMAIL=?, MOBILE=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getMobile());
            stmt.setString(4,member.getId());
            int res = stmt.executeUpdate();
            //System.out.println(res);
            return (res > 0);

        }catch (SQLException ex){
                ex.printStackTrace();
        }
        return false;
    }
}