package swarm.util;

public class Kurwektoria {

	
	// Returns true if 'end' can be reached at the given 'speed', otherwise
	// it returns false.
	public static boolean calculateTrajectory(final Vec3D diff, float speed, float gravity, boolean useHighArc, final Vec3D out)
	{
	    boolean canHit = false;

	    double term1 = 0.0f;
	    double term2 = 0.0f;
	    double root = 0.0f;

	    
	    float xzlen = (float) Math.sqrt(diff.x * diff.x + diff.z * diff.z);
	    // horizontally-flattened difference vector.
	    vec2 horz = new vec2(xzlen, 0);
	  
	    // for precision loss
	    final float factor = 100.0f;

	    float x = horz.x / factor; 
	    float y = (float) (diff.y / factor);
	    float v = speed / factor;
	    float g = gravity / factor;
	    
	    term1 = Math.pow(v, 4) - (g * ((g * Math.pow(x,2)) + (2 * y * Math.pow(v,2))));

	    // If term1 is positive, then the 'end' point can be reached at the given 'speed'.
	    if ( term1 >= 0 )
	    {
	        canHit = true;

	        term2 = Math.sqrt(term1);

	        double divisor = (g * x);

	        if ( divisor != 0.0f )
	        {
	            if ( useHighArc )
	            {
	                root = ( Math.pow(v,2) + term2 ) / divisor;
	            }
	            else
	            {
	                root = ( Math.pow(v,2) - term2 ) / divisor;
	            }

	            root = Math.atan(root);
	            
	            //horz.x = 1;
	            //horz.nor();
	            horz.rotate((float)(root));
	        }
	        
	        float faktor = horz.x / xzlen ;
	        // Now apply the speed to the direction, giving a velocity
	        out.set(diff.x * faktor, horz.y, diff.z * faktor).normalize().scale(speed);
	    }

	    return canHit;
	}


}
