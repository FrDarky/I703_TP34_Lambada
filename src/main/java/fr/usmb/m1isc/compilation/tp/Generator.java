package fr.usmb.m1isc.compilation.tp;

import java.io.FileWriter;
import java.io.IOException;

public class Generator {
	private FileWriter file;
	private int compteurPile;
	
	public Generator() {
		file = null;
		compteurPile = 0;
	}
	
	public void generate(Noeud arbre, String filename) throws IOException {
		file = new FileWriter(filename + ".asm", false);
		
		// Donn�es
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
		// N'ex�cuter que si l'arbre n'est pas vide
		if (arbre != null) {
			// Si on trouve un LET, on ajoute la variable � la liste � la liste 
			if (arbre.getExpr().toUpperCase().equals("LET")) {
				file.write("\t" + arbre.getGauche().getExpr() + " DD\n");
			}
			// Sinon on lit r�cursivement dans l'arbre
			initDonnees(arbre.getGauche());
			initDonnees(arbre.getDroite());			
		}
	}
	
	public void initCode(Noeud arbre) throws IOException {
		// Stop si l'arbre est vide
		if (arbre == null) return;
		if (arbre.getGauche() != null) initCode(arbre.getGauche());
		
		
		// Test de l'expression
		switch (arbre.getExpr().toUpperCase()) {
		case ";":
			if (arbre.getDroite() != null) initCode(arbre.getDroite());
			break;
			
		case "LET":
			getValeur(arbre.getDroite(), "eax"); // eax
			file.write("\t" + "mov " + arbre.getGauche().getExpr() + ", eax" + "\n");
			push("eax"); // r�sultat en pile
			break;
			
		case "OUTPUT":
			pop("eax"); // eax
			file.write("\t" + "out eax" + "\n");
			break;
			
		case "+":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			pop("eax"); // eax
			file.write("\t" + "add eax, ebx" + "\n"); // eax
			push("eax"); // r�sultat en pile
			break;
			
		case "-":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			pop("eax"); // eax
			file.write("\t" + "sub eax, ebx" + "\n"); // eax
			push("eax"); // r�sultat en pile
			break;
			
		case "*":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			pop("eax"); // eax
			file.write("\t" + "mul eax, ebx" + "\n"); // eax
			push("eax"); // r�sultat en pile
			break;
			
		case "/":
			getValeur(arbre.getDroite(), "ebx"); // ebx
			pop("eax"); // eax
			file.write("\t" + "div eax, ebx" + "\n"); // eax
			push("eax"); // r�sultat en pile
			break;
			
		default:
			// Ne rien faire en cas de valeur inconnue
			break;
		}
	}
	
	// R�cup�ration de la valeur d'une expression (arbre)
	// Si l'expression est terminale, alors on met sa valeur fixe dans le registre
	// Sinon, on ex�cute le code de l'expression, puis on r�cup�re le r�sultat de l'expression dans le registre
	private void getValeur(Noeud arbre, String registre) throws IOException {
		if (arbre.isTerminal()) {
			// Test des arbres terminaux (nombre, variable, INPUT)
			switch (arbre.getExpr().toUpperCase()) {
			case "INPUT":
				// Entr�e INPUT, on demande la valeur � l'utilisateur et on met la valeur dans le registre
				file.write("\t" + "in " + registre + "\n");
				break;
				
			default:
				// Par d�faut, on consid�re que c'est un nombre ou un nom de variable
				// on met la valeur attribu�e dans le registre
				file.write("\t" + "mov " + registre + ", " + arbre.getExpr() + "\n");
				break;
			}
			
		} else {
			initCode(arbre); // valeur dans la pile
			pop(registre); // eax // valeur sortie de la pile et ajout�e dans le registre
		}
	}
	
	// Pousse une valeur de registre dans la pile
	private void push(String registre) throws IOException {
		file.write("\t" + "push " + registre + "\n");
		compteurPile++;
	}
	
	// R�cup�re une valeur de la pile et la met dans le registre
	private void pop(String registre) throws IOException {
		if (compteurPile <= 0) {
			System.err.println("Erreur critique : d�passement de pile. Arr�t du programme.");
			file.close();
			System.exit(1);
		}
		file.write("\t" + "pop " + registre + "\n");
		compteurPile--;
	}
}
