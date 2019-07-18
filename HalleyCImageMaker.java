/* Zeph Turner
 * April 2017
 * 
 * Outputs a .png image to the working directory by using Halley's Method to visualize basins
 * in the Complex plane. 
 * */

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.util.function.UnaryOperator;
import java.util.ArrayList;

/*Instructions for Use
 * In Complex.java: 
 *    Set f, fp (f'), and f2p (f'') to represent the desired functions. 
 * 
 * In the application: 
 *    Set desired image dimensions and window bounds. 
 * 
 *    In testBasin(), set roots of the function. (If function has infinite roots,
 * try setting the ones closest to the window.) If you add more roots, 
 * enlarge the col[] matrix in the application accordingly (increasing the length
 * will add random colors out to col.length). 
 * 
 * Then run the application. Image will be output into the working directory
 * as h_img.png. */

public class HalleyCImageMaker {
  
    public static void main(String[] args) {
    
    //Set image dimensions.
    int ImageXDim = 500;
    int ImageYDim = 400;
    
    //Set function and its derivative (check Complex() for complex arithmetic)
    UnaryOperator<Complex> f = (a) -> { return a.pow(3).minus(a);};
    UnaryOperator<Complex> fp = (a) -> {return a.pow(2).times(3).minus(1);};
    UnaryOperator<Complex> f2p = (a) -> {return a.times(6);};
    
    Complex.setF(f);
    Complex.setFPrime(fp);
    Complex.setF2Prime(f2p);
    
    //Roots of f() above.
    ArrayList<Complex> basinArray = new ArrayList<Complex>();
    basinArray.add(new Complex(-1, 0));
    basinArray.add(new Complex(0, 0));
    basinArray.add(new Complex(1, 0));
        
    //Set window bounds. Note that WindowYLowBound is actually the top edge of the screen.
    //I iterate through the Y coordinates from ImageYDim to 0 to compensate for this.
    double WindowXLowBound = -1; 
    double WindowXHiBound = 1;
    double WindowYLowBound = -0.5;
    double WindowYHiBound = 0.5;
    
    //Set alphaFactor so that the maximum iterations from NFPIMethod.java times the 
    //alphaFactor is approximately 255 (the maximum opaqueness).
    int alphaFactor = 20;
    
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
                         (1 -((double)j/ImageYDim))*(WindowYHiBound-WindowYLowBound) + WindowYLowBound);
        //Figure out which color to set the pixel to.
        thisColor = col[testBasin(pixel, basinArray)];
        //Then set the alpha based on how many iterations it took for the pixel to converge.
        int alpha;
        if(HalleysMethod.i * alphaFactor <= 255) {
          alpha = HalleysMethod.i * alphaFactor;
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
    File output = new File("h_img" + k + ".png");
    while(output.exists()) {
      k++;
      output = new File("h_img" + k + ".png");
    }
    try {
      ImageIO.write(img, "PNG", output);
    } catch(Exception x) {
      System.out.println(x);
    }
    System.out.println("Image successfully output to h_img" + k + ".png.");
    
  }
  
  public static int testBasin(Complex a, ArrayList<Complex> basinArray) {   
    
    //Run the iteration, and compare to each basin. 
    //I compare within 0.01 because TOL = 0.01.
    Complex aIter = HalleysMethod.hm(a);
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