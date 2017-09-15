package com.ezpz.pos.printer;

/**
 * Created by RezaPramudhika on 9/6/2017.
 */

public class BillModel {

    String productShortName;
    int salesAmount;
    int unitSalesCost;
    int unitDisc;

    public BillModel(String productSName, int amount, int unitSCost, int unitDisc){
        this.productShortName = productSName;
        this.salesAmount = amount;
        this.unitSalesCost = unitSCost;
        this.unitDisc = unitDisc;
    }

    public BillModel(){

    }

    public static void generatedMoneyReceipt(String productName, int quantity, int cost, int unitDisc){


            BillModel salesModel = new BillModel(productName, quantity, cost, unitDisc);
            StaticValue.billModelArrayList.add(salesModel);

//        BillModel salesModel = new BillModel(productName, quantity, cost);
//        StaticValue.billModelArrayList.add(salesModel);





    }

    public String getProductShortName() {
        return productShortName;
    }

    public int getSalesAmount() {
        return salesAmount;
    }

    public int getUnitSalesCost() {
        return unitSalesCost;
    }

    public int getUnitDisc() {
        return unitDisc;
    }
}
