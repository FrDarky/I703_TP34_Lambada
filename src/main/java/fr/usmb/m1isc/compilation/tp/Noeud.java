package fr.usmb.m1isc.compilation.tp;

public class Noeud {
	private String expr;
	private Object gauche = null;
	private Object droite = null;
	
	
	public Noeud(String expr) {
		this.expr = expr;
	}
	
	public Noeud(String expr, Object g, Object d) {
		this(expr);
		this.gauche = g;
		this.droite = d;
	}
	
	public void setGauche(Object gauche) {
		this.gauche = gauche;
	}
	
	public void setDroite(Object droite) {
		this.droite = droite;
	}
	
	// Retourne l'expression
	public String getExpr() {
		return this.expr;
	}
	
	// Retourne le noeud gauche
	public Noeud getGauche() {
		return (Noeud)this.gauche;
	}
	
	// Retourne le noeud droit
	public Noeud getDroite() {
		return (Noeud)this.droite;
	}
	
	// Retourne true si le noeud n'a pas de fils, false si le noeud a 1 ou 2 fils.
	public Boolean isTerminal() {
		return (gauche == null && droite == null);
	}
	
	public String toString() {
		if (gauche == null && droite == null) {
			return expr;
		} else {
			return "(" + expr + (gauche != null ? " " + gauche.toString() : "") + (droite != null ? " " + droite.toString() : "") + ")";
		}
	}
}
