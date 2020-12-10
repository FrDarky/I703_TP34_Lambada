package fr.usmb.m1isc.compilation.tp;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java_cup.runtime.Symbol;

public class Main {

	public static void main(String[] args) throws Exception  {
		 LexicalAnalyzer yy;
		 if (args.length > 0)
		        yy = new LexicalAnalyzer(new FileReader(args[0])) ;
		    else
		        yy = new LexicalAnalyzer(new InputStreamReader(System.in)) ;
		@SuppressWarnings("deprecation")
		parser p = new parser (yy);
		Symbol val = p.parse();
		Noeud result = (Noeud) val.value;
		System.out.println(result);
		
		Generator g = new Generator();
		try {
			g.generate(result, "programme");
		}
		catch (IOException e) {
			System.err.println("Erreur lors de la création du fichier assembleur. Arrêt du programme.");
			e.printStackTrace();
			System.exit(1);
		}
	}
}