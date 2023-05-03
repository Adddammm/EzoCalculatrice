
package defi.calculatrice.domaine.parser;

import java.util.Map;
import java.util.function.Supplier;
import java.util.function.BiFunction;
import java.util.HashMap;


public class Grammar {
    /*
    Cette classe continne les règles de notre grammaire EBNF. Cette classe est facilement extenssible si l'on veut ajouter
    de nouvelles opérations grace au hasmaps qui contient les fonctions a exécuter.
    
    expression =  
        terme + expression  | terme - expression | terme ;

    terme = 
        facteur * terme | facteur / terme | facteur

    facteur =  
        pow(expression, expression) | sqrt(expression) | 
        root(expression, expression) | (expression)  | + facteur |
        - facteur | nombre;

    note: nombre étant des entiers et nombres a virgule flontante
    */
    
    private static Grammar single_instance = null;
    private Map<String, BiFunction<Double, Double, Double>> functions;
    private Map<String, Supplier<Double>> factors;
    private Map<String, BiFunction<Double, Double, Double>> terms;
    private Map<String, BiFunction<Double, Double, Double>> expressions;
    private Parser parser = null;
    
    
    
    
    private Grammar(){
        this.functions = new HashMap<String, BiFunction<Double, Double, Double>>();
        this.factors = new HashMap<String, Supplier<Double>>();
        this.terms = new HashMap<String, BiFunction<Double, Double, Double>>();
        this.expressions = new HashMap<String, BiFunction<Double, Double, Double>>();
        this.populateOperatorMap();
        this.populateFactorMap();
        this.populateTermMap();
        this.populateExpressionMap();
        
    }
    
    public synchronized static Grammar getInstance() {
        if (single_instance == null) {
            single_instance = new Grammar();
        }
        return single_instance;
    }
    
    private void generate_parser(){
        if (this.parser == null){
            this.parser = Parser.getInstance();
        }
    }

    public Map<String, BiFunction<Double, Double, Double>> getFunctions() {
        return functions;
    }

    public Map<String, Supplier<Double>> getFactors() {
        return factors;
    }

    public Map<String, BiFunction<Double, Double, Double>> getTerms() {
        return terms;
    }

    public Map<String, BiFunction<Double, Double, Double>>getExpressions() {
        return expressions;
    }
    
    public final void populateFactorMap(){
        this.factors.put("+", () -> positif());
        this.factors.put("-", () -> negatif());
        this.factors.put("(", () -> parentheses());       
        this.factors.put("pow", () -> pow());
        this.factors.put("root", () -> root());
        this.factors.put("sqrt", () -> sqrt());
        /*        
        ArrayList<String> nums = new ArrayList();
        for (int i = 0; i < 10; i++){
            nums.add(Integer.toString(i));
        }
        nums.add(".");
        this.factors.put(nums, () -> number());
        */
    }
    
    
    
    public final void populateTermMap(){
        this.terms.put("*", (left, right) -> mult(left,right));
        this.terms.put("/", (left, right) -> div(left,right));
    }
    
    public final void populateExpressionMap(){
        this.expressions.put("+", (left, right) -> add(left,right));
        this.expressions.put("-", (left, right) -> sub(left,right));
    }
    
    public final void populateOperatorMap(){
        
        this.functions.put("+", (left, right) -> add(left,right));
        this.functions.put("-", (left, right) -> sub(left,right));
        this.functions.put("*", (left, right) -> mult(left,right));
        this.functions.put("/", (left, right) -> div(left,right));
        //this.functions.put("^", (left, right) -> pow(left,right));
        //this.functions.put("root", (left, right) -> root(left,right));
        //this.functions.put("sqrt", (left, right) -> root(left,2));
    }
    
    private double add(double left, double right){
        return left + right;
    }
    
    private double sub(double left, double right){
        return left - right;
    }
    
    private double mult(double left, double right){
        return left * right;
    }
    
    private double div(double left, double right){
        if (right == 0.0){
            return Float.NaN;
        }
        return left / right;
    }
    
    private double pow(){
        this.generate_parser();
        this.parser.nextCharCheck('(');
        
        this.parser.forwardPastSpace();
        double left = this.parser.expression();
        
        this.parser.forwardPastSpace();
        this.parser.nextCharCheck(',');
        
        this.parser.forwardPastSpace();
        double right = this.parser.expression();
        
        this.parser.forwardPastSpace();
        this.parser.nextCharCheck(')');
        
       return Math.pow(left, right);
    }
    
    private double sqrt(){
        this.generate_parser();
        this.parser.nextCharCheck('(');
        
        this.parser.forwardPastSpace();
        double val = this.parser.expression();
        
        this.parser.forwardPastSpace();
        this.parser.nextCharCheck(')');
        
       return Math.sqrt(val);
    }
    
    private double root(){
        this.generate_parser();
        this.parser.nextCharCheck('(');
        
        this.parser.forwardPastSpace();
        double left = this.parser.expression();
        
        this.parser.forwardPastSpace();
        this.parser.nextCharCheck(',');
        
        this.parser.forwardPastSpace();
        double right = this.parser.expression();
        
        this.parser.forwardPastSpace();
        this.parser.nextCharCheck(')');
        
        return Math.pow(left, 1.0/right);
    }
    
    private double positif(){
        this.generate_parser();
        this.parser.forwardPastSpace();
        double val = this.parser.factor();
        
        return val;
    }
    
    private double negatif(){
        this.generate_parser();
        this.parser.forwardPastSpace();
        double val = this.parser.factor();
        
        return -val;
    }
    
    private double parentheses(){    
        this.generate_parser();
        this.parser.forwardPastSpace();
        double val = this.parser.expression();
        this.parser.nextCharCheck(')');
        
        return val;
        
    }
    /*
    private double number(){
        this.generate_parser();
        
        String sym = "";
        if (this.parser.isNumber()){
            //ajouter le premier élément du chiffre et ensuite continuer de le faire tant que le prochain symbol est toujours un chiffre
            sym =  sym + String.valueOf((char)this.parser.getCharacter());
            this.parser.forward();
            
            while (this.parser.isNumber()){
                sym =  sym + String.valueOf((char)this.parser.getCharacter());
                this.parser.forward();
            }
            
            return Double.parseDouble(sym);
        }
        
        throw new RuntimeException("Nombre non présent après fonction, signe ou parenthèse.");
    }
*/
}

