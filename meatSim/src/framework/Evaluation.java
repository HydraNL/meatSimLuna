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
		this.Iweight =Iweight; //ND 1 0.25
		this.indE = indE; //ND 1 0.25
		this.Sweight =Sweight; //ND 1 0.25 + 0.25 ofzo
		this.socE = socE; //tanh met avg 1 ofzo
		double x = (Iweight * indE + Sweight * socE)/2.0;
		this.grade = CFG.complexEvaluation() ?  
				1+ 0.5 *Math.tanh((x-1)/0.5):
			socE;
		this.currentContext = myContext;
	}

	public void setGrade(double grade) {
		this.grade = grade;	
	}

	public double getGrade() {
	//	System.out.println(grade);
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
	
	public PContext getContext(){
		return currentContext;
	}
}
