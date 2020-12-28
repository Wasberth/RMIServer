/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmilibraryserver;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import rmilibraryserver.rmi.Book;
import willy.database.MySqlConnection;
import willy.gui.CustomConsole;

/**
 *
 * @author Willy
 */
public class Main extends willy.gui.Ventana {

    private final MySqlConnection con;
    private final RMI rmi;

    private final JTextArea text = new JTextArea();

    public Main(final String title, final int w, final int h, final boolean resizable) throws SQLException, ClassNotFoundException {
        super(title, w, h, resizable);
        super.getContentPane().setLayout(new BorderLayout());
        CustomConsole.replaceOutputConsole(text);

        this.con = new MySqlConnection(MySqlConnection.LOCALHOST + "LibraryBD", "root", "n0m3l0");
        this.rmi = new RMI(con);

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                try {
                    System.out.println("Desconecting...");
                    con.disconnect();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

    }

    @Override
    public void setComp() {        
        final JScrollPane jsp = new JScrollPane(text);
        this.text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        super.addComp(jsp, BorderLayout.CENTER);
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException, ClassNotFoundException, SQLException, RemoteException, UnknownHostException, AlreadyBoundException {
        final Main m = new Main("Servidor RMI de la biblioteca", 300, 300, true);
        final Thread t = new Thread(m::mostrar);
        SwingUtilities.invokeAndWait(t);
        m.rmi.startServices();
        System.out.println("Server succesfully started");
    }
}
