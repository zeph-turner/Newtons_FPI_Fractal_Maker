# README

Zeph Turner

4/2/2017

Note from 2019 me: This is the code from a class project I completed during my sophomore year of college for a numerical analysis course. It's a spiritual successor to a program my sister and I enjoyed when we were little kids, that would take any function and somehow generate fractals from it. I was never able to find the program again after high school and thought about it often, wondering how it created the fractals. When I learned about the fractal behavior of basins when using Newton's Method in the complex plane, I suspected the fractals I generated as a kid came from this or a similar phenomenon.

This code and the accompanying presentation (included as a PDF) have not been updated since written. 

--

This package consists of six classes - CIMFromRoots, ComplexImageMaker, HalleyCImageMaker, HalleysMethod, NFPIMethod, and Complex. For use of the program, skip to APPLICATION CLASSES. 


## Complex CLASS

A class containing complex numbers, basic arithmetic operations on them, and a couple methods that hold functions on complex numbers.

The constructor can be blank (default is 0 + 0i) or take two doubles. Complex(a, b) represents a + bi.

Operations include addition and multiplication of two complex numbers or a complex number and a double, division and subtraction of two complex numbers, .pow(double a) which raises a complex number to the power a, .cos() and .sin(), and .abs, which returns the 2-norm of the complex number (sqrt( a^2 + b^2)). 

There are also setters for f, fprime, and k. f and fprime are UnaryOperators for a Complex input and output that represent a function and its derivative. They are used in NFPIMethod and HalleysMethod (through another method which calls them). "k" is described in the paper - Newton's method uses f(x)^k instead of f(x), so setting k=1 gives original Newton's method. f2prime is a UnaryOperator for the second derivative of a function, used in Halley's method.

This class does not need to be touched to use the application classes.


## HalleysMethod AND NFPIMethod CLASSES

Implement Halley's and Newton's method for complex numbers, respectively. Both have a static variable, i, that returns the number of iterations before convergence to TOL, which is public. These classes do not need to be touched to use the application classes.


## APPLICATION CLASSES: CIMFromRoots, ComplexImageMaker, HalleyCImageMaker

ComplexImageMaker and HalleyCImageMaker support hardcoding of functions and therefore can be used to make non-polynomial images. Both require the user to input expected/true roots of the function used for the iterative method. Image dimensions, image "window" (x and y bounds), and "k" can also be hardcoded. 

CIMFromRoots (Complex Image Maker From Roots) requires NO hardcoding and takes user input for all variables (except image dimensions, which are preset to a reasonable size). User inputs complex roots, which are multiplied together to make a polynomial. User also inputs x and y bounds and "k".

All of the application classes will probe for an unused image name, and so won't overwrite previously created images. 