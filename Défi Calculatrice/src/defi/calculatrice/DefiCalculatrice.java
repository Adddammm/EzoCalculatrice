
package defi.calculatrice;

import defi.calculatrice.domaine.parser.Parser; //TODO: REMOVE

public class DefiCalculatrice {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        defi.calculatrice.gui.MainWindow mainWindow = new defi.calculatrice.gui.MainWindow();
        mainWindow.setVisible(true);
        
        //tests
        /*
        Parser p = Parser.getInstance(); //TODO: REMOVE ALL THIS
        double a = p.calculate("(1+2)");
        System.out.println(a);
        
        
        System.out.println(Math.pow(9,1.0/3.0));
        double c = Double.parseDouble("12.3".substring(0, 1)); 
        double b = Double.parseDouble("12.3".substring(1, 4));
        
        System.out.println(c + b);
        */
    }
    
}
