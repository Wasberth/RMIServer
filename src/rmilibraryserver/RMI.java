/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmilibraryserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import rmilibraryserver.rmi.Author;
import rmilibraryserver.rmi.AuthorService;
import rmilibraryserver.rmi.Book;
import rmilibraryserver.rmi.BookAuthoryService;
import rmilibraryserver.rmi.BookService;
import rmilibraryserver.rmi.InstancedBook;
import rmilibraryserver.rmi.LibraryBook;
import rmilibraryserver.rmi.LibraryBookService;
import willy.database.MySqlConnection;
import willy.database.MySqlParam;

/**
 *
 * @author Willy
 */
public class RMI {

    private final MySqlConnection con;

    public RMI(MySqlConnection con) {
        this.con = con;
    }

    public void startServices() throws RemoteException {
        final Registry registry = LocateRegistry.createRegistry(25565);
        registry.rebind("AuthorService", new AuthorDatabase());
        registry.rebind("AuthoryService", new AuthoryDatabase());
        registry.rebind("BookService", new BookDatabase());
        registry.rebind("LibraryBookService", new LibraryBookDatabase());
    }

    private class AuthorDatabase extends UnicastRemoteObject implements AuthorService {

        public AuthorDatabase() throws RemoteException {
            super();
        }

        @Override
        public void addAuthor(Author author) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "INSERT INTO `Author` (`ath_nam`) VALUES\n"
                        + "	(?);",
                        new MySqlParam(JDBCType.VARCHAR, author.getName())
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public void modifyAuthor(Author author) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "UPDATE `Author` SET\n"
                        + "	`ath_nam` = ?,\n"
                        + "WHERE `ath_id` = ?;",
                        new MySqlParam(JDBCType.VARCHAR, author.getName()),
                        new MySqlParam(JDBCType.INTEGER, author.getId())
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public void deleteAuthor(int id) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "DELETE FROM `Author` WHERE\n"
                        + "	`ath_id` = ?",
                        new MySqlParam(JDBCType.INTEGER, id)
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public Author[] getAuthors() throws RemoteException, SQLException {
            ResultSet rs = con.executeQuery(""
                    + "SELECT\n"
                    + "	`Author`.`ath_id`,\n"
                    + "	`Author`.`ath_nam`\n"
                    + "FROM `Author`;"
            );
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            System.out.println("size: " + size);

            Author[] authors = new Author[size];
            int i = 0;
            while (rs.next()) {
                authors[i] = new Author(rs.getInt("ath_id"), rs.getString("ath_nam"));
                i++;
            }
            return authors;
        }

    }

    private class AuthoryDatabase extends UnicastRemoteObject implements BookAuthoryService {

        public AuthoryDatabase() throws RemoteException {
            super();
        }

        @Override
        public void setAuthory(int bookId, int... authorsIds) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "DELETE FROM `BookAuthor` WHERE\n"
                        + "	`bok_id` = ?",
                        new MySqlParam(JDBCType.INTEGER, bookId)
                );

                String insertValues = "";
                MySqlParam[] params = new MySqlParam[authorsIds.length * 2];

                for (int i = 0; i < authorsIds.length; i++) {
                    int j = 2 * i;
                    insertValues = insertValues + "(?, ?)" + (i != (authorsIds.length - 1) ? "," : ";");
                    params[j] = new MySqlParam(JDBCType.INTEGER, bookId);
                    params[j + 1] = new MySqlParam(JDBCType.INTEGER, authorsIds[i]);
                }
                con.executeUpdate(""
                        + "INSERT INTO `BookAuthor` (`bok_id`, `ath_id`) VALUES"
                        + insertValues,
                        params
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

    }

    private class BookDatabase extends UnicastRemoteObject implements BookService {

        public BookDatabase() throws RemoteException {
            super();
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "INSERT INTO `Book` (`bok_nam`, `bok_psh`) VALUES\n"
                        + "	(?, ?);",
                        new MySqlParam(JDBCType.VARCHAR, book.getName()),
                        new MySqlParam(JDBCType.VARCHAR, book.getPublisher())
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public void modifyBook(Book book) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "UPDATE `Book` SET\n"
                        + "	`bok_nam` = ?,\n"
                        + "	`bok_psh` = ?\n"
                        + "WHERE `bok_id` = ?;",
                        new MySqlParam(JDBCType.VARCHAR, book.getName()),
                        new MySqlParam(JDBCType.VARCHAR, book.getPublisher()),
                        new MySqlParam(JDBCType.INTEGER, book.getId())
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public void deleteBook(int id) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "DELETE FROM `Book` WHERE\n"
                        + "	`bok_id` = ?",
                        new MySqlParam(JDBCType.INTEGER, id)
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public Book[] getBooks() throws RemoteException, SQLException {
            System.out.println("rmilibraryserver.RMI.BookDatabase.getBooks()");
            ResultSet rs = con.executeQuery(""
                    + "SELECT\n"
                    + "	`Book`.`bok_id`  AS `BookID`,\n"
                    + "	`Book`.`bok_nam` AS `Name`,\n"
                    + "	`Book`.`bok_psh` AS `Publisher`\n"
                    + "FROM `Book`;"
            );
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();

            Book[] books = new Book[size];
            int i = 0;
            while (rs.next()) {
                books[i] = new Book(rs.getInt("BookID"), rs.getString("Name"), rs.getString("Publisher"));
                i++;
            }
            return books;
        }

        @Override
        public Author[] getAuthors(int id) throws RemoteException, SQLException {
            System.out.println("\nrmilibraryserver.RMI.BookDatabase.getAuthors()");
            ResultSet rs = con.executeQuery(""
                    + "SELECT\n"
                    + "	`Author`.`ath_id`,\n"
                    + "	`Author`.`ath_nam`\n"
                    + "FROM \n"
                    + "	`Author`, `Book`, `BookAuthor`\n"
                    + "WHERE \n"
                    + "	`Author`.`ath_id` = `BookAuthor`.`ath_id` AND\n"
                    + "	`Book`.`bok_id` = `BookAuthor`.`bok_id` AND\n"
                    + "	`Book`.`bok_id` = ?;",
                    new MySqlParam(JDBCType.INTEGER, id)
            );
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            System.out.println("size: " + size);

            Author[] authors = new Author[size];
            int i = 0;
            while (rs.next()) {
                authors[i] = new Author(rs.getInt("ath_id"), rs.getString("ath_nam"));
                System.out.println(authors[i].getName());
                i++;
            }
            return authors;
        }

    }

    private class LibraryBookDatabase extends UnicastRemoteObject implements LibraryBookService {

        public LibraryBookDatabase() throws RemoteException {
            super();
        }

        @Override
        public void addLibraryBook(LibraryBook book) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "INSERT INTO `Library` (`lbk_bok`, `lbk_edt`, `lbk_tot`, `lbk_brd`) VALUES\n"
                        + "	(?, ?, ?, ?);",
                        new MySqlParam(JDBCType.INTEGER, book.getBookId()),
                        new MySqlParam(JDBCType.VARCHAR, book.getEdition()),
                        new MySqlParam(JDBCType.INTEGER, book.getTotalOnLibrary()),
                        new MySqlParam(JDBCType.INTEGER, book.getBorrowed())
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public void modifyLibraryBook(LibraryBook book) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "UPDATE `Library` SET\n"
                        + "	`lbk_bok` = ?,\n"
                        + "	`lbk_edt` = ?,\n"
                        + "	`lbk_tot` = ?,\n"
                        + "	`lbk_brd` = ?\n"
                        + "WHERE `lbk_id` = ?;",
                        new MySqlParam(JDBCType.INTEGER, book.getBookId()),
                        new MySqlParam(JDBCType.VARCHAR, book.getEdition()),
                        new MySqlParam(JDBCType.INTEGER, book.getTotalOnLibrary()),
                        new MySqlParam(JDBCType.INTEGER, book.getBorrowed()),
                        new MySqlParam(JDBCType.INTEGER, book.getLibraryId())
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public void deleteLibraryBook(int id) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "DELETE FROM `Library` WHERE\n"
                        + "	`lbk_id` = ?",
                        new MySqlParam(JDBCType.INTEGER, id)
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public void borrowBook(int quantity) throws RemoteException {
            try {
                con.executeUpdate(""
                        + "UPDATE `Library` SET\n"
                        + "	`Library`.`lbk_brd` = `lbk_brd` + ?\n"
                        + "WHERE `Library`.`lbk_id` = ?;",
                        new MySqlParam(JDBCType.INTEGER, quantity)
                );
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        @Override
        public InstancedBook[] getLibraryBooks() throws RemoteException, SQLException {
            System.out.println("rmilibraryserver.RMI.LibraryBookDatabase.getLibraryBooks()");
            ResultSet rs = con.executeQuery(""
                    + "SELECT \n"
                    + "    `Library`.`lbk_id`  AS `LibraryID`,\n"
                    + "    `Book`.`bok_id`     AS `BookID`,\n"
                    + "    `Book`.`bok_nam`    AS `Name`,\n"
                    + "    `Book`.`bok_psh`    AS `Publisher`,\n"
                    + "    group_concat(DISTINCT `Author`.`ath_id`) AS `AuthorsIds`,\n"
                    + "    group_concat(DISTINCT `Author`.`ath_nam`) AS `AuthorsNames`,\n"
                    + "    `Library`.`lbk_edt` AS `Edition`,\n"
                    + "    `Library`.`lbk_tot` AS `Total`,\n"
                    + "    `Library`.`lbk_brd` AS `Borrowed`\n"
                    + "FROM \n"
                    + "    `Library`, `Book`, `Author`, `BookAuthor`\n"
                    + "WHERE\n"
                    + "    `Library`.`lbk_bok` = `Book`.`bok_id` AND\n"
                    + "    `Author`.`ath_id` = `BookAuthor`.`ath_id` AND\n"
                    + "    `Book`.`bok_id` = `BookAuthor`.`bok_id`\n"
                    + "GROUP BY `Library`.`lbk_id`;"
            );
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            System.out.println("The size of the query is: " + size);
            InstancedBook[] iBooks = new InstancedBook[size];
            int i = 0;
            while (rs.next()) {
                iBooks[i] = new InstancedBook(
                        new LibraryBook(rs.getInt("LibraryID"), rs.getInt("BookID"),
                                rs.getString("Edition"), rs.getInt("Total"), rs.getInt("Borrowed")),
                        new Book(rs.getInt("BookID"), rs.getString("Name"), rs.getString("Publisher")),
                        createAuthors(rs.getString("AuthorsIds"), rs.getString("AuthorsNames"))
                );
                i++;
            }
            return iBooks;
        }

        @Override
        public InstancedBook[] searchBooks(String search) throws RemoteException, SQLException {
            ResultSet rs = con.executeQuery(""
                    + "SELECT \n"
                    + "    `Library`.`lbk_id`  AS `LibraryID`,\n"
                    + "    `Book`.`bok_id`     AS `BookID`,\n"
                    + "    `Book`.`bok_nam`    AS `Name`,\n"
                    + "    `Book`.`bok_psh`    AS `Publisher`,\n"
                    + "    group_concat(DISTINCT `Author`.`ath_id`) AS `AuthorsIds`,\n"
                    + "    group_concat(DISTINCT `Author`.`ath_nam`) AS `AuthorsNames`,\n"
                    + "    `Library`.`lbk_edt` AS `Edition`,\n"
                    + "    `Library`.`lbk_tot` AS `Total`,\n"
                    + "    `Library`.`lbk_brd` AS `Borrowed`\n"
                    + "FROM \n"
                    + "    `Library`, `Book`, `Author`, `BookAuthor`\n"
                    + "WHERE\n"
                    + "    `Library`.`lbk_bok` = `Book`.`bok_id` AND\n"
                    + "    `Author`.`ath_id` = `BookAuthor`.`ath_id` AND\n"
                    + "    `Book`.`bok_id` = `BookAuthor`.`bok_id` AND\n"
                    + "    (\n"
                    + "        LOCATE(?, `Book`.`bok_nam`) != 0 OR\n"
                    + "        LOCATE(?, `Author`.`ath_nam`) != 0 OR\n"
                    + "        LOCATE(?, `Book`.`bok_psh`) != 0\n"
                    + "    )\n"
                    + "GROUP BY `Library`.`lbk_id`;",
                    new MySqlParam(JDBCType.VARCHAR, search),
                    new MySqlParam(JDBCType.VARCHAR, search),
                    new MySqlParam(JDBCType.VARCHAR, search)
            );
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();

            InstancedBook[] iBooks = new InstancedBook[size];
            int i = 0;
            while (rs.next()) {
                iBooks[i] = new InstancedBook(
                        new LibraryBook(rs.getInt("LibraryID"), rs.getInt("BookID"),
                                rs.getString("Edition"), rs.getInt("Total"), rs.getInt("Borrowed")),
                        new Book(rs.getInt("BookID"), rs.getString("Name"), rs.getString("Publisher")),
                        createAuthors(rs.getString("AuthorsIds"), rs.getString("AuthorsNames"))
                );
                i++;
            }
            return iBooks;
        }

        private Author[] createAuthors(final String ids, final String names) {
            String[] idArray = ids.split(",");
            String[] nameArray = names.split(",");
            Author[] authors = new Author[idArray.length];
            for (int i = 0; i < idArray.length; i++) {
                authors[i] = new Author(Integer.valueOf(idArray[i]), nameArray[i]);
            }
            return authors;
        }

    }

}
