package fr.usmb.m1isc.compilation.tp;

import java.io.FileWriter;
import java.io.IOException;

public class Generator {
	private FileWriter file;
	private int compteurPile;
	private int nb_lt, nb_lte, nb_gt, nb_gte, nb_zero, nb_nonzero, nb_wh, nb_if;
	
	public Generator() {
		file = null;
		compteurPile = 0;
		nb_lt = nb_lte = nb_gt = nb_gte = nb_zero = nb_nonzero = nb_wh = nb_if = 1;
	}
	
	public void generate(Noeud arbre, String filename) throws IOException {
		file = new FileWriter(filename + ".asm", false);
		
		// Données
		file.write("DATA SEGMENT\n");
		initDonnees(arbre);
		file.write("DATA ENDS\n");
		
		// Code
		file.write("CODE SEGMENT\n");
		initCode(arbre);
		
		// Vidage de pile
		while (compteurPile > 0) pop("eax");
		
        file.write("CODE ENDS\n");
		
		file.close();
	}
	
	public void initDonnees(Noeud arbre) throws IOException {
		// N'exécuter que si l'arbre n'est pas vide
		if (arbre != null) {
			// Si on trouve un LET, on ajoute la variable à la liste à la liste 
			if (arbre.getExpr().toUpperCase().equals("LET")) {
				file.write("\t" + arbre.getGauche().getExpr() + " DD\n");
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
		    getValeur(arbre.getDroite(), "ebx");
		    getValeur(arbre.getGauche(),"eax");
		    file.write("\t"+ "sub eax, ebx" + "\n");
		    file.write("\t"+"jl vrai_lt_" + nb_lt +"\n");
		    file.write("\t"+"mov eax, 0"+"\n");
		    file.write("\t"+"jmp sortie_lt_" + nb_lt + "\n");
		    file.write("vrai_lt_" + nb_lt +":\n");
		    file.write("\t"+"mov eax, 1"+"\n");
		    file.write("sortie_lt_" + nb_lt +":\n");
		    nb_lt++;
		    break;
		    
		case "<=":
		    getValeur(arbre.getDroite(),"ebx");
		    getValeur(arbre.getGauche(),"eax");
		    file.write("\t"+ "sub eax, ebx" + "\n");
		    file.write("\t"+"jle vrai_lte_" + nb_lte +"\n");
		    file.write("\t"+"mov eax, 0"+"\n");
		    file.write("\t"+"jmp sortie_lte_" + nb_lte + "\n");
		    file.write("vrai_lte_" + nb_lte +":\n");
		    file.write("\t"+"mov eax, 1"+"\n");
		    file.write("sortie_lte_" + nb_lte +":\n");
		    nb_lte++;
		    break;

		case "=":
			getValeur(arbre.getDroite(),"ebx");
			getValeur(arbre.getGauche(),"eax");
			file.write("\t"+ "sub eax, ebx" + "\n");
			file.write("\t"+"jz vrai_zero_" + nb_zero +"\n");
			file.write("\t"+"mov eax, 0"+"\n");
			file.write("\t"+"jmp sortie_zero_" + nb_zero + "\n");
			file.write("vrai_zero_" + nb_zero +":\n");
			file.write("\t"+"mov eax, 1"+"\n");
			file.write("sortie_zero_" + nb_zero +":\n");
			nb_zero++;
			break;
		    
		case "WHILE":
            file.write("debut_while_"+nb_wh+":\n");
            initCode(arbre.getGauche());
            file.write("\t"+"jz sortie_while_"+nb_wh+"\n");
            initCode(arbre.getDroite().getGauche());
            file.write("\t"+"jmp debut_while_"+nb_wh+"\n");
            file.write("sortie_while_"+nb_wh+":\n");
            nb_wh++;
            break;
            
		case "IF":
            initCode(arbre.getGauche());
            file.write("ite_si_" + nb_if + ":\n");
            file.write("\t"+"jz ite_sinon_" + nb_if + "\n");
            
            file.write("ite_alors_" + nb_if + ":\n");
            initCode(arbre.getDroite().getGauche());
            file.write("jmp ite_finsi_" + nb_if + "\n");
            
            file.write("ite_sinon_" + nb_if + ":\n");
            initCode(arbre.getDroite().getDroite().getDroite());
            file.write("ite_finsi_" + nb_if + ":\n");
            nb_if++;
            break; 
		
		// non utilisé
		case ">":
            getValeur(arbre.getDroite(),"ebx");
            getValeur(arbre.getGauche(),"eax");
            file.write("\t"+ "sub eax, ebx" + "\n");
            file.write("\t"+"jg vrai_gt_" + nb_gt +"\n");
            file.write("\t"+"mov eax, 0"+"\n");
            file.write("\t"+"jmp sortie_gt_" + nb_gt + "\n");
            file.write("vrai_gt_" + nb_gt +":\n");
            file.write("\t"+"mov eax, 1"+"\n");
            file.write("sortie_gt_" + nb_gt +":\n");
            nb_gt++;
            break;
            
        // non utilisé    
		case ">=":
		    getValeur(arbre.getDroite(),"ebx");
		    getValeur(arbre.getGauche(),"eax");
		    file.write("\t"+ "sub eax, ebx" + "\n");
		    file.write("\t"+"jge vrai_gte_" + nb_gte +"\n");
		    file.write("\t"+"mov eax, 0"+"\n");
		    file.write("\t"+"jmp sortie_gte_" + nb_gte + "\n");
		    file.write("vrai_gte_" + nb_gte +":\n");
		    file.write("\t"+"mov eax, 1"+"\n");
		    file.write("sortie_gte_" + nb_gte +":\n");
		    nb_gte++;
		    
		    
		// non utilisé    
		case "!=":
		    getValeur(arbre.getDroite(),"ebx");
		    getValeur(arbre.getGauche(),"eax");
		    file.write("\t"+ "sub eax, ebx" + "\n");
		    file.write("\t"+"jze vrai_nonzero_" + nb_nonzero +"\n");
		    file.write("\t"+"mov eax, 0"+"\n");
		    file.write("\t"+"jmp sortie_nonzero_" + nb_nonzero + "\n");
		    file.write("vrai_nonzero_" + nb_nonzero +":\n");
		    file.write("\t"+"mov eax, 1"+"\n");
		    file.write("sortie_nonzero_" + nb_nonzero +":\n");
		    nb_nonzero++;
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
