/*******************************************************************************
 * 2015, All rights reserved.
 *******************************************************************************/
package framework;



/**
 * Description of Evaluation.
 * 
 * @author rijk
 */
public class Evaluation {
	private double grade;
	private PContext currentContext;

	/**
	 * The constructor.
	 */
	public Evaluation(double grade, PContext myContext) {
		this.grade= grade;
		this.currentContext = myContext;
	}

	public void setGrade(double grade) {
		this.grade = grade;	
	}

	public double getGrade() {
		return grade;
	}
	
	
}
