import java.lang.Math;
import java.util.function.UnaryOperator;

/* Class representing complex numbers with basic arithmetic operations and support for a function and its derivatives
 * for use in Newton's or Halley's method.
 * */

public class Complex {
  
  //A complex number is represented as a + bi.
  private double a;
  private double b;
  
  protected static UnaryOperator<Complex> f;
  protected static UnaryOperator<Complex> fprime;
  protected static double k;
  
  //(Used in Halley's Method)
  protected static UnaryOperator<Complex> f2prime;
  
  //Initialize at 0 + 0i.
  public Complex() {
    a = 0;
    b = 0;
  }
  
  //Or initialize at a point set by the application.
  public Complex(double a, double b) {
    this.a = a;
    this.b = b;
  }
  
  //Complex addition
  public Complex plus(Complex second) {
    Complex third = new Complex( (second.getA() + this.a), (second.getB() + this.b));
    return third;
  }
  
  //Set a and b manually, after instantiation.
  public void setDoubles(double a, double b) {
    this.a = a;
    this.b = b;
  }
  
  //Addition of a complex and a real number
  public Complex plus(double second) {
    return new Complex(this.a + second, this.b);
  }
  
  //Subtraction of two complex numbers
  public Complex minus(Complex second) {
    Complex third = new Complex( (this.a - second.getA()), (this.b - second.getB()));
    return third;
  }
  
  //Subtraction of a real number from a complex number
  public Complex minus(double second) {
    return new Complex(this.a - second, this.b);
  }
  
  //Taking the opposite sign of a complex number
  public Complex opposite() {
    Complex second = new Complex(-this.a, -this.b);
    return second;
  }
  
  //Multiplication of two complex numbers
  public Complex times(Complex second) {
    Complex third = new Complex( (this.a * second.getA()) - (this.b * second.getB()), 
                                (this.b*second.getA()) + (this.a*second.getB()));
    return third;
  }
  
  //Multiplication of a complex number by a scalar
  public Complex times(double num) {
    Complex second = new Complex(num*this.a, num*this.b);
    return second;
  }
  
  
  //Complex division, with rationalization
  public Complex over(Complex second) {
    double numerator = (this.a * second.getA()) + (this.b * second.getB());
    double denominator = (second.getA()*second.getA()) + (second.getB()*second.getB());
    double avalue = numerator/denominator;
    
    numerator = (this.b*second.getA()) - (this.a*second.getB());
    double bvalue = numerator/denominator;
    return new Complex(avalue, bvalue);
  }
  
  //Complex sin() function (I "cheated" by using Math.sin/h and cos/h, instead of
  //using the exponential equation)
  public Complex sin() {
    double x = this.getA();
    double y = this.getB();
    double re = Math.sin(x) * Math.cosh(y);
    double im = Math.cos(x) * Math.sinh(y);
    return new Complex(re, im);
  }
  
  //Complex cos()
  public Complex cos() {
    double x = this.getA();
    double y = this.getB();
    double re = Math.cos(x) * Math.cosh(y);
    double im = Math.sin(x) * Math.sinh(y);
    return new Complex(re, -im);
  }
  
      
  //Rreturn real part
  public double getA() {
    return this.a;
  }
  
  //Return complex part
  public double getB() {
    return this.b;
  }
  
  //Return absolute value of the complex number, which is a real number
  public double abs() {
    return (Math.sqrt( (a*a) + (b*b) ));
  }
  
  //Print the complex number as "a + bi"
  public String toString() {
    return (this.a + " + " + this.b + "i");
  }
  
  //Set f() function from application by passing in a UnaryOperator<Complex>
  public static void setF(UnaryOperator<Complex> f){
    Complex.f = f;
  }
  
  //Perform some function with the complex number this is called on as "x"
  public Complex f() {
    return f.apply(this);
  }
 
  public static void setFPrime(UnaryOperator<Complex> fprime) {
    Complex.fprime = fprime;
  }
  
  //Derivative of above function
  public Complex fprime() {
    return fprime.apply(this);
  }
  
  //Raise a complex number to some power i (for positive, integer i) by repeated
  //multiplication. 
  public Complex pow(int i) {
    Complex base = new Complex(1, 0);
    for(int j = 0; j < i; j++) {
      base = base.times(this);
    }
    return base;
  }
  
  public static void setK(double j) {
    Complex.k = j;
  }
  
  //Newton's formula
  public Complex g() {
    //Factor to "stretch" the fractal parts by - this is equivalent to taking the 
    //kth root of the entire function f(). 
    return this.minus( (this.f().over(this.fprime())).times(1/k) );
  }
  
  public static void setF2Prime(UnaryOperator<Complex> r) {
    Complex.f2prime = r;
  }
  
  public Complex f2prime() {
    return f2prime.apply(this);
  }
  
  //Halley's Method: a higher-order root finding method used when f''(x) is known
  public Complex h() {
    return this.minus( (this.f().times(2).times(this.fprime())  )
                 .over( this.fprime().pow(2).times(2).minus( (this.f().times(this.f2prime())))));
  }
    
}
  