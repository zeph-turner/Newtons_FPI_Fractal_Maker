/* Zeph Turner
 * April 2017
 * 
 * Outputs a .png image to the working directory by using Newton's Method to visualize basins
 * in the Complex plane. Takes user input for roots to construct a polynomial and its derivative.
 * Relatively user-friendly with minimal hardcoding.
 * */

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.util.function.UnaryOperator;
import java.util.ArrayList;
import java.util.Scanner;

/*Instructions for Use
 * 
 * Run the application and follow displayed instructions. When entering roots, you must press "enter"
 * between the two components of the complex number or an error will be thrown. 
 * 
 * If user enters roots a, b ... n the polynomial used for the iteration will be 
 * ((x - a)(x - b)...(x - n))^kval.
 */

public class CIMFromRoots {
  
  public static void main(String[] args) {
    
    //Set image dimensions.
    int ImageXDim = 1920;
    int ImageYDim = 1080;
    //I leave these preset so the user doesn't enter like 90 roots and try to make a 2000x2000 image
    
    //User must enter at least one root. Form is h + li; h and l entered on separate lines, then combined
    //into a Complex object.
    ArrayList<Complex> basinArray = new ArrayList<Complex>();
    double h; 
    double l;
    Scanner userIn = new Scanner(System.in);
    System.out.println("Enter desired roots for function. First enter 'a', then 'b' (for a + bi).");
    h = userIn.nextDouble(); l = userIn.nextDouble();
    basinArray.add(new Complex(h, l));
    
    //User enters as many more roots as they want. Roots are displayed after entering.
    boolean isdone = false;
    System.out.println("Enter as many roots as you want. Enter Q (or any other letter) when done.");
    while(!isdone){
      if(userIn.hasNextDouble()) {
        h = userIn.nextDouble();
      } else {
        userIn.next();
        break;
      }
      if(userIn.hasNextDouble()) {
        l = userIn.nextDouble();
      } else {
        userIn.next();
        break;
      }
      basinArray.add(new Complex(h, l));
      System.out.println("Entered root " + basinArray.get(basinArray.size() - 1) + ". Next:");
    }
    
    
    //Set function and its derivative based on enered roots. 
    //The (check Complex() for complex arithmetic)
    UnaryOperator<Complex> f = (a) -> { 
      Complex prod = new Complex(1, 0);
      for(int i = 0; i < basinArray.size(); i++) {
        prod = prod.times(a.minus(basinArray.get(i)));
      }
      return prod;
    };
    //Derivative, using product rule. (Weirdly similar to part of the calculation of a Lagrangian...)
    //The derivative of (x-a) is 1, so basically we just knock one root out of the total product and sum over all
    //the roots.
    UnaryOperator<Complex> fp = (a) -> {
      Complex sum = new Complex(0, 0);
      Complex prod = new Complex(1, 0);
      for(int i = 0; i < basinArray.size(); i++) {
        for(int j = 0; j < basinArray.size(); j++) {
          if(j != i) {
            prod = prod.times(a.minus(basinArray.get(j)));
          }
        }
        sum = sum.plus(prod);
        prod.setDoubles(1, 0);
      }
      return sum;
    };
    
    Complex.setF(f);
    Complex.setFPrime(fp);
    
    //See paper for explanation of K.
    System.out.println("Set k (use 1 as default!):");
    double kval = userIn.nextDouble();
    Complex.setK(kval);
        
    //Set window bounds. Note that y = 0 is actually the top edge of the image (not the bottom).
    //I iterate through the Y coordinates from ImageYDim to 0 to compensate for this.
    System.out.println("Enter lower bound on X axis:");
    double WindowXLowBound = userIn.nextDouble(); 
    System.out.println("Enter upper bound on X axis:");
    double WindowXHiBound = userIn.nextDouble();
    System.out.println("Enter lower bound on Y axis:");
    double WindowYLowBound = userIn.nextDouble();
    System.out.println("Enter upper bound on Y axis:");
    double WindowYHiBound = userIn.nextDouble();
    
    //Set alphaFactor so that the maximum iterations from NFPIMethod.java times the 
    //alphaFactor is approximately 255 (the maximum opaqueness), or adjust to suit.
    int alphaFactor = 10;
    
    //Done with scanner now!
    userIn.close();
    
    //Define some color integers to use in the rendered image. After the 6th color
    //they are randomly generated. 
    BufferedImage img = new BufferedImage(ImageXDim, ImageYDim, BufferedImage.TYPE_INT_ARGB);
    int colSize = 0;
    
    //(Set random colors until basinArray is exhausted. Set minimum colors to 6 to avoid
    // array out of bounds exception.)
    
    //These are currently set to an approximate rainbow. 
    if(basinArray.size() < 6) {
      colSize = 6; 
    } else {
      colSize = basinArray.size() + 1;
    }
    
    int[] col = new int[colSize];
    int r = 255;
    int g = 0;
    int b = 0;
    col[0] = (r << 16) | (g<<8) | b;
    
    r = 255;
    g = 127;
    b = 0;
    col[1] = (r<<16) | (g<<8) | b;
    
    r = 255;
    g = 255;
    b = 0;
    col[2] = (r<<16) | (g<<8) | b;
    
    r = 0;
    g = 255;
    b = 0;
    col[3] = (r<<16) | (g<<8) | b;
    
    r= 0;
    g= 0;
    b= 255;
    col[4] = (r<<16) | (g<<8) | b;
    
    r=143;
    g=0;
    b=255;
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
    for(int i = ImageXDim-1; i > -1; i--) {
      for(int j = ImageYDim-1; j > -1; j--) {
        //Set pixel coordinates based on window dimensions. (Note Y coordinate is reversed because
        //the top of the image is actually y=0, not y=WindowYHiBound.)
        pixel.setDoubles( ((double)i/ImageXDim)*(WindowXHiBound-WindowXLowBound) + WindowXLowBound,
                         ((1 - (double)j/ImageYDim))*(WindowYHiBound-WindowYLowBound) + WindowYLowBound);
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
    
    //Write the BufferedImage to file.
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
    //Give some output with dimensions and window of the image as well as polynomial used.
    System.out.println(ImageXDim + " x " + ImageYDim + " image output displaying ");
    System.out.println(WindowXLowBound + " < x < " + WindowXHiBound + ", " + WindowYLowBound + " < y < "
                         + WindowYHiBound);
    System.out.print("For polynomial (");
    for(int c = 0; c < basinArray.size(); c++) {
      System.out.print("(x - " + basinArray.get(c) + ")");
    }
    System.out.println(")^" + kval);
    System.out.println("Image saved as img" + k + ".png.");
    
  }
  
  public static int testBasin(Complex a, ArrayList<Complex> basinArray) {   
    
    //Run the iteration, and compare to each basin. 
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
    //"extra" color (for the most part). 
    return basinArray.size();   
  }
  
}