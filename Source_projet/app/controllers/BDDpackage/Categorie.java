/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers.BDDpackage;


import java.awt.*;

/*
 *
 * @author spine
 */
public class Categorie {
    public String nom;
    public int id;
    public Color couleur;

    Categorie() {

    }

    public Categorie(int id, String nom, Color couleur){
        this.id = id;
        this.nom = nom;
        this.couleur = couleur;
    }

    public Categorie(int id, String nom){
        this(id,nom,null);
    }

    public String getName()
    {
        return nom;
    }

}
