/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package defi.calculatrice.domaine.parser;


public class Parser {
    /*
    Cette classe implémente les outils nécessaire (en conjonction avec Grammar) pour lire l'équation mathématique qu'est le texte 
    et calculer cette équation.
    
    position = position courrante dans le string
    character = nombre ascii
    
    On utilise la logique d'un "Recursive descent parser" avec la grammaire EBNF de la classe Grammar.
    */
    private static Parser single_instance = null; //singleton pattern, juste 1 parser pour l'unique controller
    private Grammar grammar;
    private int position;
    private int character; //voir isNumber et isFunction pour la raison de pourquoi c'est un entier.
    private String equation;
    
    
    private Parser(Grammar grammar){
        this.grammar = grammar;
    }
    
    public synchronized static Parser getInstance(Grammar grammar) {
        /*
        Permet d'obtenir l'unique instance du Parser pour pouvoir effectuer nos calcules.
        */
        if (single_instance == null) {
            single_instance = new Parser(grammar);
        }
        return single_instance;
    }
    
    protected synchronized static Parser getInstance() {
        /*
        TODO: trouver une meuilleur solution de l'interdépendance de Parser et Grammar. (probablement dans l'architecture des package)
        
        Version spéciale de getInstance qui est seulement utilisé par grammaire. Ceci règle un problème où
        Parser et Grammar sont interdépendant. Ce n'est pas une solution élégante.
        */
        if (single_instance != null) {
            return single_instance;
        }
        else{
            throw new RuntimeException("Parseur n'a pas encore été généré.");
        }
    }
    
    public double calculate(String equation){
        /*
        input:
        equation = String qui représente l'équation a calculer
        
        output:
        le résultat de l'équation sous forme de float.
        
        Fonction principale. Permet de lancer le processus récursif qui permet de calculer l'application. instancie d'abord les valeurs essentiel avant 
        d'appeler expression().
        */
        this.position= -1;
        this.equation = equation;
        
        this.forward();
        double val = this.expression();
        return val;
    }
    
    protected void forward(){
        /*
        Fonction qui permet d'avancer dans la position du string et de changer la varuable character en conséquence.
        Si character deviens -1, on est arrivé au bout de l'équation.
        */
        this.position += 1;
        if (this.position >= this.equation.length()){
            this.character = -1;
        }
        else{
            this.character = this.equation.charAt(this.position);
        }
    }
    
    protected int checkChar(int char_to_test){
        /*
        input: 
        char_to_test = le caractère au quel le caractère courant est comparé.
        
        output: 0 en cas d'erreur, 1 en cas de succès
        
        De façons vorance en cas de succès, reguarde si le caractère courant (this.character) est le bon caractère (char_to_test).
        */
        if (this.character != ' '){  //permet la traverser de n'importe quel nombre d'escpace
            if (char_to_test == this.character){
                this.forward();
                return 1;
            }
            return 0; 
        }
        else{
            this.forward();
            return this.checkChar(char_to_test);
        }
    }
    
    protected void forwardPastSpace(){
        /*
        Fonction qui saute tous les caractères d'espacement.
        */
        while (this.character == ' '){  //permet la traverser de n'importe quel nombre d'escpace
            this.forward();
        }
    }
    
    protected int nextCharCheck(int char_to_test){ 
        /*
        input: 
        char_to_test = le caractère au quel le caractère courant est comparé.
        
        output: 1 en cas de succès, si non erreur.
        
        Même fonctionalité que checkChar, mais erreur si le teste de réussi pas.
        */
        if (this.checkChar(char_to_test) == 1){
                return 1;
            }
        else{
            throw new RuntimeException("Erreur de syntaxe, il manque:" + (char)char_to_test + ".");
        }
    }
    
    protected boolean isNumber(){
    /*
    Fonction utilitaire qui permet de reguarder si le caratère ascii courant est un nombre par comparaison. 
    C'est la raison pour laquel this.character est un entier.
    */
        return (this.getCharacter() <= '9' && this.getCharacter() >= '0') || this.getCharacter() == '.';
    }
    
    protected boolean isLetter(){
    /*
    Fonction utilitaire qui permet de reguarder si le caratère ascii courant est une lettre par comparaison. 
    C'est la raison pour laquel this.character est un entier.
    */
        return this.getCharacter() <= 'z' && this.getCharacter() >= 'a';
    }
    
    protected double factor(){
    /*
    Fonction qui calculer les élément de plus haute priorité qui seront des facteur dans les multiplications et division
    de la fonction terme().
        
    3 possibilité: un nombre, une combinaison de lettre, un caratère qui représente une "fonction".
    */
        String sym = "";
        
        if (this.isNumber()){
            //ajouter le premier élément du chiffre et ensuite continuer de le faire tant que le prochain symbol est toujours un chiffre
            sym =  sym + String.valueOf((char)this.getCharacter());
            this.forward();
            
            while (this.isNumber()){
                sym =  sym + String.valueOf((char)this.getCharacter());
                this.forward();
            }
            
            return Double.parseDouble(sym);
        }
        
        else if (this.isLetter()){
            sym =  sym + String.valueOf((char)this.getCharacter());
            this.forward();
            
            while (this.isLetter()){
                sym =  sym + String.valueOf((char)this.getCharacter());
                this.forward();
            }
            
            for (var entry : this.grammar.getFactors().entrySet()){
                if (sym.equals(entry.getKey())){
                    return entry.getValue().get();
                }
            }
            throw new RuntimeException("Fonction non valide.");
        }
        
        else{
            for (var entry : this.grammar.getFactors().entrySet()){
                if (this.checkChar((int)entry.getKey().charAt(0)) == 1){
                    return entry.getValue().get();
                }
            }
            throw new RuntimeException("\"Lettre\" de l'alphabet(factor) non valide.");
        }
    }
    
    protected double term(){
        /*
        Fonction qui calculer les multiplication et division avant de calculer les additions et soustraction de la fonction expressions.
        */
        double val = this.factor();
        int is_key = 0;
        String key = "None";
        
        while (true){
            is_key = 0;
            for (var entry : this.grammar.getTerms().entrySet())
            {
                if (this.checkChar((int)entry.getKey().charAt(0)) == 1){
                    is_key = 1;
                    key = entry.getKey();
                    break;
                }
            }
            if (is_key == 1)
            {
                this.forwardPastSpace();
                double right = this.factor();
                val = this.grammar.getTerms().get(key).apply(val, right);
            }
            else{
                return val;
            }
        }
    }

    
    protected double expression(){
        /*
        Fonction qui commence la descente itérative. elle calcule d'abord le terme a gauche et ensuite cherche les additions a faire entre les termes.
        */
        double val = this.term();
        int is_key = 0;
        String key = "None";
        
        while (true){
            is_key = 0;
            for (var entry : this.grammar.getExpressions().entrySet())
            {
                if (this.checkChar((int)entry.getKey().charAt(0)) == 1){
                    is_key = 1;
                    key = entry.getKey();
                    break;
                }
            }
            if (is_key == 1)
                {
                    this.forwardPastSpace();
                    double right = this.term();
                    val = this.grammar.getExpressions().get(key).apply(val, right);
                }
                else{
                    return val;
                }
        }
    }

    protected int getPosition() {
        return position;
    }

    protected int getCharacter() {
        return character;
    }

    protected String getEquation() {
        return equation;
    }
    
    
}
