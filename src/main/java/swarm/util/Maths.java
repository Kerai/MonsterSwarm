package swarm.util;

public class Maths {
	
	public static double fastSqrt(double a) {
		return Double.longBitsToDouble( ( ( Double.doubleToLongBits( a )-(1l<<52) )>>1 ) + ( 1l<<61 ) );
	}
	
	public static double fastSqrtNewton(double a) {
		double sqrt = fastSqrt(a);
		sqrt = (sqrt + a/sqrt)/2.0;
		return (sqrt + a/sqrt)/2.0;
	}

}
