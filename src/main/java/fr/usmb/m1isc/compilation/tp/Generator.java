package fr.usmb.m1isc.compilation.tp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Generator {
	private FileWriter file;
	private ArrayList<String> variables;
	private int compteurPile;
	private int nb_lt, nb_lte, nb_gt, nb_gte, nb_zero, nb_nonzero, nb_wh, nb_if, nb_not, nb_and, nb_or;
	
	public Generator() {
		file = null;
		variables = new ArrayList<>();
		compteurPile = 0;
		nb_lt = nb_lte = nb_gt = nb_gte = nb_zero = nb_nonzero = nb_wh = nb_if = nb_not = nb_and = nb_or = 1;
	}
	
	public void generate(Noeud arbre, String filename) throws IOException {
		file = new FileWriter(filename, false);
		
		// Données
		file.write("DATA SEGMENT\n");
		initDonnees(arbre);
		file.write("DATA ENDS\n");
		
		// Code
		file.write("CODE SEGMENT\n");
		initCode(arbre);
		
		// Vidage de pile
		// while (compteurPile > 0) pop("eax");
		
        file.write("CODE ENDS\n");
		
		file.close();
	}
	
	public void initDonnees(Noeud arbre) throws IOException {
		// N'exécuter que si l'arbre n'est pas vide
		if (arbre != null) {
			// Si on trouve un LET, on ajoute la variable à la liste à la liste 
			if (arbre.getExpr().toUpperCase().equals("LET") && !variables.contains(arbre.getGauche().getExpr())) {
				file.write("\t" + arbre.getGauche().getExpr() + " DD\n");
				variables.add(arbre.getGauche().getExpr());
			}
			// Sinon on lit récursivement dans l'arbre
			initDonnees(arbre.getGauche());
			initDonnees(arbre.getDroite());			
		}
	}
	
	public void initCode(Noeud arbre) throws IOException {
		// Stop si l'arbre est vide
		if (arbre == null) return;
		
		// Exécuter tout ce qui est à gauche sauf si on est sur un WHILE ou sur un IF
		if (arbre.getGauche() != null
			&& arbre.getExpr().toUpperCase() != "WHILE"
			&& arbre.getExpr().toUpperCase() != "IF")
				initCode(arbre.getGauche());
		
		
		// Test de l'expression
		switch (arbre.getExpr().toUpperCase()) {
		case ";":
			if (arbre.getDroite() != null) initCode(arbre.getDroite());
			break;
			
		case "LET":
			getValeur(arbre.getDroite(), "eax"); // eax
			file.write("\t" + "mov " + arbre.getGauche().getExpr() + ", eax" + "\n");
			push("eax"); // résultat en pile
			break;
			
		case "OUTPUT":
			getValeur(arbre.getGauche(), "eax"); // eax
			file.write("\t" + "out eax" + "\n");
			break;
			
		case "+":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			getValeur(arbre.getGauche(), "eax"); // eax
			file.write("\t" + "add eax, ebx" + "\n"); // eax
			push("eax"); // résultat en pile
			break;
			
		case "-":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			
			if (arbre.getGauche() != null) {				
				getValeur(arbre.getGauche(), "eax"); // eax
				file.write("\t" + "sub eax, ebx" + "\n"); // eax
				push("eax"); // résultat en pile
			} else {
				file.write("\t" + "mul ebx, -1" + "\n"); // ebx
				push("ebx"); // résultat en pile
			}
			break;
			
		case "*":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			getValeur(arbre.getGauche(), "eax"); // eax
			file.write("\t" + "mul eax, ebx" + "\n"); // eax
			push("eax"); // résultat en pile
			break;
			
		case "/":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			getValeur(arbre.getGauche(), "eax"); // eax
			file.write("\t" + "div eax, ebx" + "\n"); // eax
			push("eax"); // résultat en pile
			break;
			
		case "%":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			getValeur(arbre.getGauche(), "eax"); // eax
			file.write("\t" + "mov ecx, eax" + "\n"); // ecx = valeur temporaire
			file.write("\t" + "div ecx, ebx" + "\n");
			file.write("\t" + "mul ecx, ebx" + "\n");
			file.write("\t" + "sub eax, ecx" + "\n"); // eax
			push("eax"); // résultat en pile
			break;
			
		case "<":
			int temp_lt = nb_lt;
			nb_lt++;
			
		    getValeur(arbre.getDroite(),"ebx");
		    getValeur(arbre.getGauche(),"eax");
		    file.write("\t"+ "sub eax, ebx" + "\n");
		    file.write("\t"+"jl vrai_lt_" + temp_lt +"\n");
		    file.write("\t"+"mov eax, 0"+"\n");
		    file.write("\t"+"jmp sortie_lt_" + temp_lt + "\n");
		    file.write("vrai_lt_" + temp_lt +":\n");
		    file.write("\t"+"mov eax, 1"+"\n");
		    file.write("sortie_lt_" + temp_lt +":\n");
		    break;
		    
		case "<=":
			int temp_lte = nb_lte;
			nb_lte++;

			getValeur(arbre.getDroite(),"ebx");
		    getValeur(arbre.getGauche(),"eax");
		    file.write("\t"+ "sub eax, ebx" + "\n");
		    file.write("\t"+"jle vrai_lte_" + temp_lte +"\n");
		    file.write("\t"+"mov eax, 0"+"\n");
		    file.write("\t"+"jmp sortie_lte_" + temp_lte + "\n");
		    file.write("vrai_lte_" + temp_lte +":\n");
		    file.write("\t"+"mov eax, 1"+"\n");
		    file.write("sortie_lte_" + temp_lte +":\n");
		    break;

		case "=":
			int temp_zero = nb_zero;
			nb_zero++;

			getValeur(arbre.getDroite(),"ebx");
			getValeur(arbre.getGauche(),"eax");
			file.write("\t"+ "sub eax, ebx" + "\n");
			file.write("\t"+"jz vrai_zero_" + temp_zero +"\n");
			file.write("\t"+"mov eax, 0"+"\n");
			file.write("\t"+"jmp sortie_zero_" + temp_zero + "\n");
			file.write("vrai_zero_" + temp_zero +":\n");
			file.write("\t"+"mov eax, 1"+"\n");
			file.write("sortie_zero_" + temp_zero +":\n");
			break;
			
		case "NOT":
			int temp_not = nb_not;
			nb_not++;
			
            getValeur(arbre.getDroite(),"eax");
            file.write("\t"+"jz zero_not_" + temp_not + "\n");
            file.write("\t"+"mov eax, 0");
            file.write("\t"+"jmp sortie_not_" + temp_not + "\n");
            
            file.write("zero_not_" + temp_not + ":\n");
            file.write("\t"+"mov eax, 1");
            file.write("sortie_not_" + temp_not + ":\n");
        break;
        
		case "AND":
			int temp_and = nb_and;
			nb_and++;
			
			getValeur(arbre.getGauche(),"eax");
            file.write("\t"+"jz false_and_" + temp_and + "\n");
            
            getValeur(arbre.getDroite(),"ebx");
            file.write("\t"+"jz false_and_" + temp_and + "\n");
            
            file.write("true_and_" + temp_and + ":\n");
            file.write("\t"+"mov eax, 1");
            file.write("\t"+"jmp sortie_and_" + temp_and + ":\n");
            
            file.write("false_and_" + temp_and + ":\n");
            file.write("\t"+"mov eax, 0");
            file.write("sortie_and_" + temp_and + ":\n");
        break;
        
		case "OR":
			int temp_or = nb_or;
			nb_or++;

			getValeur(arbre.getGauche(),"eax");
            file.write("\t"+"jnz true_or_" + temp_or + "\n");
            
            getValeur(arbre.getDroite(),"ebx");
            file.write("\t"+"jnz true_or_" + temp_or + "\n");
            
            file.write("false_or_" + temp_or + ":\n");
            file.write("\t"+"mov eax, 0");
            file.write("\t"+"jmp sortie_or_" + temp_or + ":\n");
            
            file.write("true_or_" + temp_or + ":\n");
            file.write("\t"+"mov eax, 1");
            file.write("sortie_or_" + temp_or + ":\n");
        break;
		    
		case "WHILE":
			int temp_wh = nb_wh;
			nb_wh++;
			
            file.write("debut_while_" + temp_wh + ":\n");
            initCode(arbre.getGauche());
            file.write("\t"+"jz sortie_while_" + temp_wh + "\n");
            
            initCode(arbre.getDroite().getGauche());
            file.write("\t"+"jmp debut_while_" + temp_wh + "\n");
            
            file.write("sortie_while_" + temp_wh + ":\n");
            break;
            
		case "IF":
			int temp_if = nb_if;
			nb_if++;
			
            initCode(arbre.getGauche());
            file.write("ite_si_" + temp_if + ":\n");
            file.write("\t"+"jz ite_sinon_" + temp_if + "\n");
            
            file.write("ite_alors_" + temp_if + ":\n");
            initCode(arbre.getDroite().getGauche());
            file.write("jmp ite_finsi_" + temp_if + "\n");
            
            file.write("ite_sinon_" + temp_if + ":\n");
            initCode(arbre.getDroite().getDroite().getDroite());
            file.write("ite_finsi_" + temp_if + ":\n");
            break; 
		
		// non utilisé
		case ">":
			int temp_gt = nb_gt;
			nb_gt++;

			getValeur(arbre.getDroite(),"ebx");
            getValeur(arbre.getGauche(),"eax");
            file.write("\t"+ "sub eax, ebx" + "\n");
            file.write("\t"+"jg vrai_gt_" + temp_gt +"\n");
            file.write("\t"+"mov eax, 0"+"\n");
            file.write("\t"+"jmp sortie_gt_" + temp_gt + "\n");
            file.write("vrai_gt_" + temp_gt +":\n");
            file.write("\t"+"mov eax, 1"+"\n");
            file.write("sortie_gt_" + temp_gt +":\n");
            break;
            
        // non utilisé    
		case ">=":
			int temp_gte = nb_gte;
			nb_gte++;			
			
		    getValeur(arbre.getDroite(),"ebx");
		    getValeur(arbre.getGauche(),"eax");
		    file.write("\t"+ "sub eax, ebx" + "\n");
		    file.write("\t"+"jge vrai_gte_" + temp_gte +"\n");
		    file.write("\t"+"mov eax, 0"+"\n");
		    file.write("\t"+"jmp sortie_gte_" + temp_gte + "\n");
		    file.write("vrai_gte_" + temp_gte +":\n");
		    file.write("\t"+"mov eax, 1"+"\n");
		    file.write("sortie_gte_" + temp_gte +":\n");
		    break;
		    
		// non utilisé    
		case "!=":
			int temp_nonzero = nb_nonzero;
			nb_nonzero++;
			
		    getValeur(arbre.getDroite(),"ebx");
		    getValeur(arbre.getGauche(),"eax");
		    file.write("\t"+ "sub eax, ebx" + "\n");
		    file.write("\t"+"jze vrai_nonzero_" + temp_nonzero +"\n");
		    file.write("\t"+"mov eax, 0"+"\n");
		    file.write("\t"+"jmp sortie_nonzero_" + temp_nonzero + "\n");
		    file.write("vrai_nonzero_" + temp_nonzero +":\n");
		    file.write("\t"+"mov eax, 1"+"\n");
		    file.write("sortie_nonzero_" + temp_nonzero +":\n");
		    break;

			
		default:
			// Ne rien faire en cas de valeur inconnue
			break;
		}
	}
	
	// Récupération de la valeur d'une expression (arbre)
	// Si l'expression est terminale, alors on met sa valeur fixe dans le registre
	// Sinon, on exécute le code de l'expression, puis on récupère le résultat de l'expression dans le registre
	private void getValeur(Noeud arbre, String registre) throws IOException {
		if (arbre.isTerminal()) {
			// Test des arbres terminaux (nombre, variable, INPUT)
			switch (arbre.getExpr().toUpperCase()) {
			case "INPUT":
				// Entrée INPUT, on demande la valeur à l'utilisateur et on met la valeur dans le registre
				file.write("\t" + "in " + registre + "\n");
				break;
				
			default:
				// Par défaut, on considère que c'est un nombre ou un nom de variable
				// on met la valeur attribuée dans le registre
				file.write("\t" + "mov " + registre + ", " + arbre.getExpr() + "\n");
				break;
			}
			
		} else {
			initCode(arbre); // valeur dans la pile
			pop(registre); // eax // valeur sortie de la pile et ajoutée dans le registre
		}
	}
	
	// Pousse une valeur de registre dans la pile
	private void push(String registre) throws IOException {
		file.write("\t" + "push " + registre + "\n");
		compteurPile++;
	}
	
	// Récupère une valeur de la pile et la met dans le registre
	private void pop(String registre) throws IOException {
		if (compteurPile > 0) {
			file.write("\t" + "pop " + registre + "\n");
			compteurPile--;			
		}
	}
}
