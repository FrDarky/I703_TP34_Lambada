package fr.usmb.m1isc.compilation.tp;

import java.io.FileWriter;
import java.io.IOException;

public class Generator {
	private FileWriter file;
	
	public Generator() {
		file = null;
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
		
		// Test de l'expression
		switch (arbre.getExpr().toUpperCase()) {
		case ";":
			getValeur(arbre.getGauche(), "eax"); // eax
			// file.write("\t" + "pop eax" + "\n");
			initCode(arbre.getDroite());
			break;
			
		case "LET":
			getValeur(arbre.getDroite(), "eax"); // eax
			file.write("\t" + "mov " + arbre.getGauche().getExpr() + ", eax" + "\n");
			file.write("\t" + "push eax" + "\n"); // résultat en pile
			break;
			
		case "+":
			getValeur(arbre.getGauche(), "eax"); // eax
			getValeur(arbre.getDroite(), "ebx"); // ebx
			file.write("\t" + "add eax, ebx" + "\n"); // eax
			file.write("\t" + "push eax" + "\n"); // résultat en pile
			break;
			
		case "-":
			getValeur(arbre.getGauche(), "eax"); // eax
			getValeur(arbre.getDroite(), "ebx"); // ebx
			file.write("\t" + "sub eax, ebx" + "\n"); // eax
			file.write("\t" + "push eax" + "\n"); // résultat en pile
			break;
			
		case "*":
			getValeur(arbre.getGauche(), "eax"); // eax
			getValeur(arbre.getDroite(), "ebx"); // ebx
			file.write("\t" + "mul eax, ebx" + "\n"); // eax
			file.write("\t" + "push eax" + "\n"); // résultat en pile
			break;
			
		case "/":
			getValeur(arbre.getGauche(), "eax"); // eax
			getValeur(arbre.getDroite(), "ebx"); // ebx
			file.write("\t" + "div eax, ebx" + "\n"); // eax
			file.write("\t" + "push eax" + "\n"); // résultat en pile
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
			file.write("\t" + "mov " + registre + ", " + arbre.getExpr() + "\n"); // valeur dans le registre
		} else {
			initCode(arbre); // valeur dans la pile
			file.write("\t" + "pop " + registre + "\n"); // valeur sortie de la pile et ajoutée dans le registre
		}
	}
}
