package controllers;

import controllers.BDDpackage.BDD;

import controllers.BDDpackage.Categorie;
import controllers.BDDpackage.SousCategorie;
import controllers.BDDpackage.Utilisateur;
import controllers.BDDpackage.Pays;
import controllers.BDDpackage.Statut;
import play.mvc.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.Form.*;
import play.data.FormFactory;
import com.google.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;
import java.util.*;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This controller contains an action to handleHTTP requests
 *  to the application's home page.
 */
public class HomeController extends Controller {

    static public BDD DB = new BDD();
    Utilisateur user = new Utilisateur();

    private final FormFactory formFactory;
    private String errorMessageLogin = "";


    @Inject
    public HomeController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index()  throws SQLException {

        //DB.display_Categories();

        //DB.display_Sous_categorie(2);

        //DB.UtilisateurByID(1);
        return ok(views.html.index.render("Compact Budget"));
    }

    public Result test() {

        return ok(views.html.test.render());
    }

    // Exemple pour passer un paramètre de HTML -> Java (via URL)
    // Fichier à toucher : HomeController + routes + views
    public Result testParam(String name) {

        return ok(views.html.testParam.render(name));
    }

    // Gestion du login
    public Result LoginSubmit() {

        DynamicForm form = formFactory.form().bindFromRequest();
        int valCo = DB.checkConnectionGetId(form.get("password"),form.get("username"));
        if(valCo != 0) {
            user = DB.UtilisateurByID(valCo);
        }
        errorMessageLogin = "Erreur, veuillez réessayer.";
        return redirect("/profil");

    }

    //Gestion nouvel utilisateur
    public Result RegisterSubmit(){
        DynamicForm form = formFactory.form().bindFromRequest();


        //Gestion erreur
        boolean error = false;
        ArrayList<String> messageError = new ArrayList<String>();

        //Gestion checkbox option
        int opt = (form.get("Option") == null || form.get("Option").length() == 0) ? 1 : 2;
        //return ok(views.html.index.render(Integer.toString(opt)));

        //Controle pays
        if(Integer.parseInt(form.get("pays")) == 0)
        {
            messageError.add("Erreur, veuillez choisir un pays\n");
            error = true;
        }
        if(Integer.parseInt(form.get("statut")) == 0)
        {
            messageError.add("Erreur, veuillez choisir un statut\n");
            error = true;
        }
        if(form.get("surname").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un prénom\n");
            error = true;
        }
        if(form.get("name").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un nom\n");
            error = true;
        }
        if(form.get("username").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un username\n");
            error = true;
        }
        if(form.get("email").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un e-mail\n");
            error = true;
        }
        if(form.get("password").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un mot de passe\n");
            error = true;
        }
        if(form.get("anniversaire").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer une date de naissance\n");
            error = true;
        }


        int idResult = 0;
        if(!error)
        {
            idResult = DB.addUser(form.get("surname"),form.get("name"),form.get("email"),form.get("username")
             , BCrypt.hashpw(form.get("password"), BCrypt.gensalt()), form.get("genre"),form.get("anniversaire")
             , Integer.parseInt(form.get("statut"))
             , Integer.parseInt(form.get("pays")), opt);
        }
        //Recuperation pays pour affichage
        ArrayList<Pays> pays = new ArrayList<Pays>();
        pays = DB.get_Pays();

        //Recuperation statut pour affichage
        ArrayList<Statut> statut = new ArrayList<Statut>();
        statut = DB.get_Statut();
        if(idResult != 0) {
            user = DB.UtilisateurByID(idResult);
            return ok( views.html.utilisateur.render( user,0,"",pays,statut) );
        }
        else if(!error)
        {
            messageError.add("Erreur, veuillez choisir un autre username ou un autre email\n");
        }

        return ok(views.html.register.render(pays,statut,messageError));

    }

    //Gestion affichage submit
    public Result Register(){

        //Recuperation pays pour affichage
        ArrayList<Pays> pays = new ArrayList<Pays>();
        pays = DB.get_Pays();

        //Recuperation statut pour affichage
        ArrayList<Statut> statut = new ArrayList<Statut>();
        statut = DB.get_Statut();

        return ok(views.html.register.render(pays,statut,null));
    }

    // Exemple pour passer un paramètre de java -> HTML
    public Result Profil() {


        // Get user_id
        if(user.getId() == 0)
        {
            return ok( views.html.Login.render(errorMessageLogin));
        }
        else
        {
            //Recuperation pays pour affichage
            ArrayList<Pays> pays = new ArrayList<Pays>();
            pays = DB.get_Pays();

            //Recuperation statut pour affichage
            ArrayList<Statut> statut = new ArrayList<Statut>();
            statut = DB.get_Statut();

            return ok( views.html.utilisateur.render( user,0,"",pays,statut) );
        }

    }

    //Gestion de la déconnection
    public Result Disconnect(){
        user = new Utilisateur();
        errorMessageLogin = "";
        return redirect("/profil");

    }

    // Exemple pour passer un paramètre de java -> HTML
    public Result Categorie() {

        ArrayList<Categorie> listCategorie = new ArrayList<Categorie>();
        listCategorie = DB.display_Categories();

        return ok( views.html.Categorie.render( listCategorie) );
    }

    // Permet d'ajouter une sous catégorie à la base de donnée
   public Result sousCategorie(String defaultSelect ) {
        // Envoie la liste de toute les catégories au HTML
       ArrayList<Categorie> listCategorie = new ArrayList<Categorie>();
       listCategorie = DB.display_Categories();

        return ok( views.html.sousCategorie.render( listCategorie, defaultSelect) );
    }

    // Permet d'ajouter une sous catégorie
    public Result addSousCategorie(String nom, int categorie_id) {

        int alerte = 2;
        String message = "Success insertion ";

        // Recherche la categorie selectionné
        Categorie categorieChoisi = DB.CategorieByID(categorie_id);
        // Cree la sous catégore souhaitée
        SousCategorie sousCategorie = new SousCategorie(nom, categorieChoisi );

        // Test la valeur de retour !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Ajout d'une notif 
        if (!DB.insert_Sous_categorie( sousCategorie)){
            alerte = 1;
            message = "Error: insertion failed !";
        }

        //Recuperation pays pour affichage
        ArrayList<Pays> pays = new ArrayList<Pays>();
        pays = DB.get_Pays();

        //Recuperation statut pour affichage
        ArrayList<Statut> statut = new ArrayList<Statut>();
        statut = DB.get_Statut();
        // Retour a la page souhaitée (profil)
        return ok( views.html.utilisateur.render( DB.UtilisateurByID( 1 ),alerte,message,pays,statut) );
    }

    // Gestion des options
    public Result ModifOptions()
    {
        if(user.getId() == 0)
        {
            return redirect("/profil");
        }
        else
        {
            return ok(views.html.options.render(user.getOptions()));
        }
    }

    public Result ModifOptionsSub(String Option)
    {
        //return ok(views.html.index.render(Option));
        if(user.getId() == 0)
        {
            return ok(views.html.index.render("Compact Budget"));
        }
        else
        {
            boolean ret = DB.updateOptionUser(user.getId(), Integer.parseInt(Option));
            if(ret)
            {
                int valco = user.getId();
                user = DB.UtilisateurByID(valco);
                return redirect("/profil");
            }
            else
            {
               return redirect("/options");
            }
        }
    }

    public Result ModifProfile()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        //Gestion erreur
        boolean error = false;
        ArrayList<String> messageError = new ArrayList<String>();


        if(form.get("surname").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un prénom\n");
            error = true;
        }
        if(form.get("name").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un nom\n");
            error = true;
        }
        if(form.get("username").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un username\n");
            error = true;
        }
        if(form.get("email").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer un e-mail\n");
            error = true;
        }
        if(form.get("anniversaire").length() == 0)
        {
            messageError.add("Erreur, veuillez entrer une date de naissance\n");
            error = true;
        }


        return ok (views.html.index.render("temp"));
    }
}
