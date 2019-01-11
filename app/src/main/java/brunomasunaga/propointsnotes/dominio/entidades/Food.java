package brunomasunaga.propointsnotes.dominio.entidades;

import java.io.Serializable;

public class Food implements Serializable {
    public int FoodID;
    public String DescriptionFood;
    public String UnityFood;
    public double AmountUnity;
    public double Carbs;
    public double Prots;
    public double Fats;
    public double Fiber;
    public int PointsUnity;

    public static int calculatePointsInfo(double carbs, double prots, double fats, double fiber, double mult){
        int calculatedPoints = (int) Math.max(Math.round(mult*(19*carbs + 16*prots+ 45*fats + 5*fiber)/175), 0);
        return calculatedPoints;
    }

    public static int calculatePointsNoInfo(int points, double mult){
        int calculatedPoints = (int) Math.round(mult*points);
        return calculatedPoints;
    }

}