package fr.usmb.m1isc.compilation.tp;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java_cup.runtime.Symbol;

public class Main {

	public static void main(String[] args) throws Exception {
		LexicalAnalyzer yy;
		
		System.out.println("Compilateur LAMBADA -> ASM de Jacquet Virgile et Matrod R�mi");
		
		
		if (args.length > 0) {
			System.out.println("Lecture du fichier " + args[0]);
			yy = new LexicalAnalyzer(new FileReader(args[0])) ;			 
		}
		else {
			System.out.println("�criture du code LAMBADA (Ctrl+Z pour arr�ter d'�crire) :");
			yy = new LexicalAnalyzer(new InputStreamReader(System.in)) ;		    	
		}
		
		@SuppressWarnings("deprecation")
		parser p = new parser(yy);
		Symbol val = p.parse();
		Noeud result = (Noeud) val.value;
		
		System.out.println("Arbre g�n�r�. R�sultat sous forme d'expression pr�fix�e parenth�s�e :");		
		System.out.println(result);
		
		String programme;
		if (args.length > 1) {
			programme = args[1];
		}
		else {
			programme = "programme.asm";
		}
		System.out.println("Compilation dans \"" + programme + "\"...");
		
		Generator g = new Generator();
		try {
			g.generate(result, programme);
			System.out.println("Termin�.");
			System.out.println("Vous pouvez ex�cuter le fichier \"" + programme + "\" avec la machine � registres \"vm-0.9.jar\".");
		}
		catch (IOException e) {
			System.err.println("Erreur lors de la cr�ation du fichier assembleur. Arr�t du programme.");
			e.printStackTrace();
			System.exit(1);
		}
	}
}