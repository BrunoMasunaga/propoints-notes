package brunomasunaga.propointsnotes.dominio.entidades;

import java.io.Serializable;

public class Registre implements Serializable {
    public int RegID;
    public String Day;
    public String Hour;
    public double QuantityFood;

    public int FoodID;
    public String DescriptionFood;
    public String UnityFood;
    public double AmountUnity;
    public double Carbs;
    public double Prots;
    public double Fats;
    public double Fiber;
    public int PointsUnity;

    public static int calculatePoints(Registre reg){
        if (reg.Carbs == -1) return Food.calculatePointsNoInfo(reg.PointsUnity, reg.QuantityFood);
        return Food.calculatePointsInfo(reg.Carbs, reg.Prots, reg.Fats, reg.Fiber, reg.QuantityFood);
    }
}
