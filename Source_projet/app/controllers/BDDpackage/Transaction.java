/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers.BDDpackage;


/*
 *
 * @author spine
 */
public class Transaction {

    public int id;
    public String name;
    public double valeur;
    public String date;
    public int idRecurrence;
    public double timestamp_solde;

    Transaction() {

    }
    // tout
    public Transaction(int id, String name, double valeur, String date, int idRecurence, double timestamp_solde){
        this.id = id;
        this.name = name;
        this.valeur = valeur;
        this.date = date;
        this.idRecurrence = idRecurence;
        this.timestamp_solde = timestamp_solde;
    }

}
