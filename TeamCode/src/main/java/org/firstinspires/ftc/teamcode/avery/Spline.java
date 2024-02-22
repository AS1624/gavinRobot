class Spline extends path {
  private Vector2D[] controlPoints;
  private Vector2D term3;
  private Vector2D term4;
  
  public Spline(Vector2D[] controlPoints){
    super(controlPoints);
    this.term2 = 
      controlPoints[0].mult(-1)
      .add(controlPoints[1].mult(-2))
      .add(controlPoints[2].mult(3))
      .add(controlPoints[3].mult(-1));

    this.term3 = 
      controlPoints[0].mult(2)
      .add(controlPoints[1].mult(1))
      .add(controlPoints[2].mult(-2))
      .add(controlPoints[3].mult(1));
  }

  public Spline(){
    super(new Vector2D[4]);
    //controlPoints = new Vector2D[4];
  }

  public void addStart(Vector2D start){
    this.controlPoints[0] = start;
  }

  public void addEnd(Vector2D end){
    this.controlPoints[3] = end;
  }

  public void addStartVelocity(Vector2D controlPoint){
    this.controlPoints[1] = controlPoint;
  }

  public 

  @override
  public Vector2D point(double t){
    return 
      controlPoints[0]
      .add(controlPoints[1].mult(t))
      .add(term2.mult(t * t))
      .add(term3.mult(t * t * t));
  }

  @override
  public Vector2D derivative(double t){
    return 
      controlPoints[1]
      .add(term2.mult(2 * t))
      .add(term3.mult(3 * t * t));
  }

  /**
  * @param point a vector representing the robot's coorinates
  * @returns a unit vector representing the direction of robot travel
  * replit ghostwriter is amazing 
  */
  @override
  public Vector2D closestT(Vector2D point){
    //TODO: add
    return new Vector2D(0, 0);
  }
}