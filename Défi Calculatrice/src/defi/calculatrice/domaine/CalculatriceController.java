
package defi.calculatrice.domaine;

import defi.calculatrice.domaine.parser.Parser;
import defi.calculatrice.domaine.parser.Grammar;

import java.util.Map;
import java.util.function.Supplier; //pour stocker fonction dans hashmap des fonctions/opérateurs
import java.util.function.BiFunction; //pour stocker fonction dans hashmap des fonctions/opérateurs


public class CalculatriceController {
    /*
    Cette classe suit le principe du Controleur des principes GRASP de Larman. Elle sert d'unique point de communication entre la couche
    de présentation et la couche du domaine.
    */
    private static CalculatriceController single_instance = null; //singleton pattern, juste 1 controller pour le mainWindow
    private Map<String, BiFunction<Double, Double, Double>> functions; //map des fonctions/opérateurs supporter. Permet d'éttendre le design plus facilement sans modifier l'agorithme directement.
    private Grammar grammar;
    private Map<String, Supplier<Double>> factors;
    private Map<String, BiFunction<Double, Double, Double>> terms;
    private Map<String, BiFunction<Double, Double, Double>> expressions;
    private Parser parser;
    //private String equation;
    private double result = Double.NaN; //initialise le résultat a une valeur null
    
    
    private CalculatriceController(){
        this.grammar = Grammar.getInstance();//important set la grammaire avant le parser
        this.parser = Parser.getInstance(this.grammar); 
        this.functions = this.grammar.getFunctions();
        this.factors =  this.grammar.getFactors();
        this.terms = this.grammar.getTerms();
        this.expressions = this.grammar.getExpressions();
        
    }
    
    public synchronized static CalculatriceController getInstance() {
        /*
        Permet d'obtenir l'unique instance du controlleur pour que l'interface puisse communiquer avec l'application.
        */
        if (single_instance == null) {
            single_instance = new CalculatriceController();
        }
        return single_instance;
    }
    
    /*
    public String getEquation() {
        return equation;
    }
    */
    
    /*
    public void setEquation(String equation) {
    
         
    
        this.equation = equation;
    }
    */
    
    public double calculate(String equation){
        /*
        input:
        equation = String qui représente l'équation a calculer
        
        output:
        le résultat de l'équation sous forme de float.
        
        Cette fonction utilise le parser pour calculer la valaeur de l'équation en entré.
        */
        this.result = this.parser.calculate(equation);
        return this.result;
    }
    
    
}

