/* Zeph Turner
 * April 2017
 * 
 * Implements Newton's iterative method for root finding on complex numbers. Works with Complex class. 
 * Public static variable i holds number of iterations to convergence. TOL can be adjusted manually.
 * */

import java.lang.Math;

public class NFPIMethod {
  
  public static int i;
  
  public static Complex nfpi(Complex p0) {
    //Index at which to start iteration
    i = 1; 
    
    //Set both p's to be equal for the time being
    Complex p1 = p0;
    
    //Max number of iterations before we should give up :(
    int N = 25;
    //Bool to control whether the code continues
    boolean cont = true; 
    final double TOL = 0.0001;
    
    do {
      //Update p using N(x) (named .g() in the Complex class.)
      p1 = p0.g();
      
      //Print the new guess with the index
      //System.out.println("P: " + p1 + "   i: " + i);
      
      //Check if tolerance has been met
      if( (p1.minus(p0)).abs() < TOL) {
        return p1;
      }
      //Update p0
      p0 = p1;
      //Increment i
      i++;
      //Check if we've done this too many times and should give up
      if( i > N) {
        return p1;
      }
      
      //Check if we should stop for one reason or the other
    } while(cont);
    return new Complex(0,0);
  }
}