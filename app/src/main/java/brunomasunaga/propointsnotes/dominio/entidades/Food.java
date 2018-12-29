package brunomasunaga.propointsnotes.dominio.entidades;

public class Food {
    public int FoodID;
    public String DescriptionFood;
    public String UnityFood;
    public double AmountUnity;
    public double Carbs;
    public double Prots;
    public double Fats;
    public double Fiber;
    public int PointsUnity;

    public static int calculatePoints(double carbs, double prots, double fats, double fiber, double mult){
        int calculatedPoints = (int) Math.max(Math.round(mult*(19*carbs + 16*prots+ 45*fats + 5*fiber)/175), 0);
        return calculatedPoints;
    }

}