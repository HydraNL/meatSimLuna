/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;

import main.CFG;



/**
 * Description of Evaluation.
 * 
 * @author rijk
 */
public class Evaluation {
	private double grade;
	private PContext currentContext;
	private double Iweight;
	private double indE;
	private double Sweight;
	private double socE;


	/**
	 * The constructor.
	 */
	public Evaluation(double Iweight, double indE, double Sweight, double socE, PContext myContext) {
		this.Iweight =Iweight;
		this.indE = indE;
		this.Sweight =Sweight;
		this.socE = socE;
		this.grade = CFG.complexEvaluation() ?  Iweight * indE + Sweight * socE:socE;
		this.currentContext = myContext;
	}

	public void setGrade(double grade) {
		this.grade = grade;	
	}

	public double getGrade() {
		return grade;
	}

	public double getIweight() {
		return Iweight;
	}

	public double getIndE() {
		return indE;
	}

	public double getSweight() {
		return Sweight;
	}

	public double getSocE() {
		return socE;
	}
	
	
}
