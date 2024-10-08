
public class Line {
	public int x1,y1,x2,y2;

	public Line(int x1, int y1, int x2, int y2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	//https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
	//Line intersection code came from here (i hate this project suddenly)
	
	 class Point 
	{ 
	    public int x; 
	    public int y; 
	  
        public Point(int x, int y)  
        { 
            this.x = x; 
            this.y = y; 
        } 
        
        public String toString() {
        	return "(" + this.x + ", " + this.y + ")";
        }
	      
	}; 
	
	//Credit to user Grumdrig on StackOverflow for line segment intersection
	// Given three collinear points p, q, r, the function checks if 
	// point q lies on line segment 'pr' 
	public boolean onSegment(Point p, Point q, Point r) 
	{ 
	    if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && 
	        q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y)) 
	    return true; 
	  
	    return false; 
	} 
	  
	// To find orientation of ordered triplet (p, q, r). 
	// The function returns following values 
	// 0 --> p, q and r are collinear 
	// 1 --> Clockwise 
	// 2 --> Counterclockwise 
	public int orientation(Point p, Point q, Point r) 
	{ 
	    // See https://www.geeksforgeeks.org/orientation-3-ordered-points/ 
	    // for details of below formula. 
	    int val = (q.y - p.y) * (r.x - q.x) - 
	            (q.x - p.x) * (r.y - q.y); 
	  
	    if (val == 0) return 0; // collinear 
	  
	    return (val > 0)? 1: 2; // clock or counterclock wise 
	} 
	  
	// The main function that returns true if line segment 'p1q1' 
	// and 'p2q2' intersect. 
	public boolean doIntersect(Line b) 
	{ 
		if (b != null) {
			Point p1 = new Point(x1, y1);
			Point q1 = new Point(x2, y2);
			Point p2 = new Point(b.x1, b.y1);
			Point q2 = new Point(b.x2, b.y2);
		    // Find the four orientations needed for general and 
		    // special cases 
		    int o1 = orientation(p1, q1, p2); 
		    int o2 = orientation(p1, q1, q2); 
		    int o3 = orientation(p2, q2, p1); 
		    int o4 = orientation(p2, q2, q1); 
		  
		    // General case 
		    if (o1 != o2 && o3 != o4) 
		        return true; 
		  
		    // Special Cases 
		    // p1, q1 and p2 are collinear and p2 lies on segment p1q1 
		    if (o1 == 0 && onSegment(p1, p2, q1)) return true; 
		  
		    // p1, q1 and q2 are collinear and q2 lies on segment p1q1 
		    if (o2 == 0 && onSegment(p1, q2, q1)) return true; 
		  
		    // p2, q2 and p1 are collinear and p1 lies on segment p2q2 
		    if (o3 == 0 && onSegment(p2, p1, q2)) return true; 
		  
		    // p2, q2 and q1 are collinear and q1 lies on segment p2q2 
		    if (o4 == 0 && onSegment(p2, q1, q2)) return true; 
		    
		    return false; // Doesn't fall in any of the above cases 
		}
		
	  
		return false; // Doesn't fall in any of the above cases 
	} 
}
