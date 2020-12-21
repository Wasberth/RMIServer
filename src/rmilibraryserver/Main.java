/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmilibraryserver;

/**
 *
 * @author Willy
 */
public class Main extends willy.gui.Ventana {

    public Main(String title, int w, int h, boolean resizable) {
        super(title, w, h, resizable);
    }

    @Override
    public void setComp() {

    }

    public static void main(String[] args) {
        Main m = new Main("Servidor RMI de la biblioteca", 500, 500, false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                m.mostrar();
            }
        });
        t.start();
    }
}
