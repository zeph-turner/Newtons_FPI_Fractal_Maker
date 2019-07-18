/* Zeph Turner
 * April 2017
 * 
 * Outputs a .png image to the working directory by using Newton's Method to visualize basins
 * in the Complex plane. 
 * */

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.util.function.UnaryOperator;
import java.util.ArrayList;

/*Instructions for Use
 * 
 * The first lines of the main method below contain all hardcoded variables, equations, roots, etc. 
 * Things you should set to use the program: 
 * 1. Set ImageXDim and ImageYDim to determine size of finished image. For polynomials of degree <6 or so, a full
 *    1920x1080 image does not take too long.)
 * 2. Set f and fp to some function and its derivative. Consult Complex() for possible operators to use - 
 *    .pow() and .sin() and .cos() methods are included for complex numbers. 
 * 3. In basinArray, set all expected roots of f() by using basinArray.add(new Complex(a, b)) for a+bi.
 *    (Wolfram Alpha can be used to determine complex roots of a function.)
 * 4. Set k if desired. (See paper or short explanation below for what this is.)
 * 
 * Then run the application. Image will be output into the working directory
 * as img.png. 
 */

public class ComplexImageMaker {
  
  public static void main(String[] args) {
    
    //Set image dimensions.
    int ImageXDim = 1920;
    int ImageYDim = 1080;
    
    //Set function and its derivative (check Complex() for complex arithmetic)
    UnaryOperator<Complex> f = (a) -> { return a.pow(3).minus(a);};
    UnaryOperator<Complex> fp = (a) -> {return a.pow(2).times(3).minus(1);};
    
    Complex.setF(f);
    Complex.setFPrime(fp);
    
    //See paper for explanation of K. Set K between 0.5 and 1. k=1 runs Newton's Method as usual.
    //Otherwise, instead of using f(x) in the N(x) calculation, f^k(x) is used. 
    //Smaller values of k produce more complex and beautiful fractals, as explained in the paper. 
    Complex.setK(1);
    
    //A great extension here would be to construct a function and its derivative from
    //hardcoded roots, to avoid having to encode the roots and the derivative manually.
    ArrayList<Complex> basinArray = new ArrayList<Complex>();
    basinArray.add(new Complex(1, 0));
    basinArray.add(new Complex(0, 0));
    basinArray.add(new Complex(-1, 0));
        
    //Set window bounds. Note that WindowYLowBound is actually the top edge of the screen.
    //I iterate through the Y coordinates from ImageYDim to 0 to compensate for this.
    double WindowXLowBound = -2; 
    double WindowXHiBound = 2;
    double WindowYLowBound = -1;
    double WindowYHiBound = 1;
    
    //Set alphaFactor so that the maximum iterations from NFPIMethod.java times the 
    //alphaFactor is approximately 255 (the maximum opaqueness). I currently have
    //maxIterations set to 25. In practice I have sometimes adjusted the alphaFactor
    //to maximize contrast in the generated images (for example, setting it higher if most
    //points converge to within TOL in <10 iterations). 
    int alphaFactor = 10;
    
    //Define some color integers to use in the rendered image. After the 6th color
    //they are randomly generated. 
    BufferedImage img = new BufferedImage(ImageXDim, ImageYDim, BufferedImage.TYPE_INT_ARGB);
    int colSize = 0;
    
    //(Set random colors until basinArray is exhausted. Set minimum colors to 6 to avoid
    // array out of bounds exception.)
    if(basinArray.size() < 6) {
      colSize = 6; 
    } else {
      colSize = basinArray.size() + 1;
    }
    
    int[] col = new int[colSize];
    int r = 0;
    int g = 91;
    int b = 63;
    col[0] = (r << 16) | (g<<8) | b;
    
    r = 107;
    g = 201;
    b = 56;
    col[2] = (r<<16) | (g<<8) | b;
    
    r = 11;
    g = 59;
    b = 112;
    col[4] = (r<<16) | (g<<8) | b;
    
    r = 51;
    g = 98;
    b = 151;
    col[3] = (r<<16) | (g<<8) | b;
    
    r= 42;
    g= 153;
    b= 119;
    col[1] = (r<<16) | (g<<8) | b;
    
    r=54;
    g=149;
    b=3;
    col[5] = (r<<16) | (g<<8) | b;
    
    //Random colors.
    for(int i = 6; i < col.length; i++) {
      r = (int) (Math.random()*255);
      g = (int) (Math.random()*255);
      b = (int) (Math.random()*255);
      col[i] = (r << 16) | (g << 8) | b;
    }
    
    
    
    //Initialize a Complex object to represent each pixel.
    Complex pixel = new Complex(0,0);
    //Initialize color integer.
    int thisColor = 0;
    //For each pixel in the image...
    for(int i = 0; i < ImageXDim; i++) {
      for(int j = ImageYDim-1; j > -1; j--) {
        //Set pixel coordinates based on window dimensions. (Note Y coordinate is reversed because
        //the top of the image is actually y=0, not y=WindowYHiBound.)
        pixel.setDoubles( ((double)i/ImageXDim)*(WindowXHiBound-WindowXLowBound) + WindowXLowBound,
                         ( 1 - ((double)j/ImageYDim))*(WindowYHiBound-WindowYLowBound) + WindowYLowBound);
        //Figure out which color to set the pixel to.
        thisColor = col[testBasin(pixel, basinArray)];
        //Then set the alpha based on how many iterations it took for the pixel to converge.
        int alpha;
        if(NFPIMethod.i * alphaFactor <= 255) {
          alpha = NFPIMethod.i * alphaFactor;
        } else {
          //Just in case I set the alphaFactor badly and get a number over 255.
          alpha = 255;
        }
        //Add alpha value to the color by bit-shifting it and combining the binary numbers
        //with a bitwise OR operation. 
        thisColor = ( alpha << 24) | thisColor;
        //Set the pixel to the color, and move on to the next pixel.
        img.setRGB(i, j, thisColor);
      }
    }
    
    //Write the BufferedImage to file and output filename to console.
    int k = 0;
    File output = new File("img" + k + ".png");
    while(output.exists()) {
      k++;
      output = new File("img" + k + ".png");
    }
    try {
      ImageIO.write(img, "PNG", output);
    } catch(Exception x) {
      System.out.println(x);
    }
    System.out.println("Image output to img" + k + ".png.");
  }
  
  public static int testBasin(Complex a, ArrayList<Complex> basinArray) {   
    
    //Run the iteration, and compare to each basin. 
    //I compare within 0.01 because TOL = 0.01.
    Complex aIter = NFPIMethod.nfpi(a);
    for(int i = 0; i < basinArray.size(); i++) {
      if( aIter.minus(basinArray.get(i)).abs() < 0.01) {
        //Return the number of the root that it matched.
        return i;
      }
    }
    //If a match isn't found, return the length of the array - which is what the next
    //root would be if there was one. Thus all points that don't converge, cyclical 
    //patterns that don't end up at one root or another, etc end up colored with an 
    //"extra" color.
    return basinArray.size();   
  }
  
}