/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package willy.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JTextArea;

/**
 *
 * @author Willy
 */
public final class CustomConsole extends OutputStream {

    private final JTextArea textArea;

    private CustomConsole(final JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        textArea.append(String.valueOf((char) b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.update(textArea.getGraphics());
    }

    public static void replaceOutputConsole(final JTextArea textArea) {
        textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomConsole(textArea));
        System.setOut(printStream);
        System.setErr(printStream);
    }

}
