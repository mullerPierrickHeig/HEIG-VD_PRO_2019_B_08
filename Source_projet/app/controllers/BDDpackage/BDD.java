/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers.BDDpackage;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;




/** Base de donnée.
 * @author Compact budget
 * @version 1.0
 * @since 1.0
 */
public class BDD {
    // Pour te connecter utilise un Login/Group roles admin (par default : postgres et ton mot de passe)
                                                               // Test
    private String url = "jdbc:postgresql://127.0.0.1:5432/BD_Budget"; // http://127.0.0.1:50165/browser/ ou  "jdbc:postgresql://localhost/dvdrental"
    private String user = "postgres";       // postgres   user_files (droit uniquement de selection)
    private String password = "123456789";          // 123456789     123
    private Connection conn = null;


    public String getUser(){
        return this.user;
    }
    
    public BDD(){
         try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println("Noo.");
            System.out.println(e.getMessage());
        }
    }
    
    public BDD(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    /**
     * Permet de convertir le nom d'une table simple avec le nom exacte
     *
     * @param TableName Nom de la table
     *
     * @return Nom de la table au bon format
     */
    private String table(String TableName) {
        return "public.\"" + TableName + "\" ";
    }
    /**
     * Permet de convertir l'id d'un pays en string
     *
     * @param PaysID Id du pays
     * @return Nom du pays
     */
    private String paysString(int PaysID) {
        String pays = "";
        
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("pays")
                    + "WHERE pays_id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            
            pstmt.setInt(1, PaysID);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            pays = rs.getString("nom");
    
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return pays;
        
    }


    /**
     * Permet de convertir l'id des options en string
     *
     * @param OptionsID Id de l'option
     * @return Tableau des options
     */
    private ArrayList<Boolean> optionsString(int OptionsID) {
        ArrayList<Boolean> options = new ArrayList<Boolean>();
        
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("options")
                    + " WHERE options_id = ? ;";
            
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            
            pstmt.setInt(1, OptionsID);
            ResultSet rs = pstmt.executeQuery();

            int i = 2;

            while (rs.next()) {
                options.add( rs.getBoolean(i) );
                i++;
            }
    
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return options;
        
    }

    /**
     * Permet de mettre à jour le profil d'un utilisateur, sans le mot de passe, les options ou le solde
     * @param userId ID de l'utilisateur à modifier
     * @param prenom prénom de l'utilisateur
     * @param nom nom de l'utilisateur
     * @param email email de l'utilisateur
     * @param pseudo pseudo de l'utilisateur
     * @param genre genre de l'utilisateur, true pour homme, false pour femme
     * @param anniversaire date de naissance de l'utilisateur
     * @param statut_id id du statut de l'utilisateur
     * @param pays_id id du pays de l'utilisateur
     * @return boolean true si update à marcher, false sinon
     */
    public boolean updateUser(int userId, String prenom,String nom,String email,String pseudo,Boolean genre,
                              String anniversaire,int statut_id, int pays_id){
            if(!checkUniqueUserWithId(email,pseudo,userId))
            {
                return false;
            }
            try {
                String sql = "UPDATE " + table("utilisateur") + " set prenom=?, nom=?,email=?," +
                        "pseudo=?,genre=?,anniversaire=?,statut_id=?,pays_id=? where utilisateur_id=? ; ";

                Date dateAnniversaire = null;
                java.sql.Date sDate = null;
                try {
                    dateAnniversaire = new SimpleDateFormat("yyyy-MM-dd").parse(anniversaire);
                    sDate = new java.sql.Date(dateAnniversaire.getTime());
                }
                catch (Exception e)
                {
                    Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, e);
                    return false;
                }
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, prenom);
                pstmt.setString(2, nom);
                pstmt.setString(3, email);
                pstmt.setString(4, pseudo);
                pstmt.setBoolean(5, genre);
                pstmt.setDate(6, sDate);
                pstmt.setInt(7, statut_id);
                pstmt.setInt(8, pays_id);
                pstmt.setInt(9, userId);

                int count = pstmt.executeUpdate();
                return (count > 0);
            }
            catch (SQLException ex) {
                Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            }
        return false;

    }

    /**
     * Met à jour les options de l'utilisateur
     * @param userId ID de l'utilisateur à modifier
     * @param OptionId Id de l'option à modifier
     * @return true si l'option à été modifié dans l'utilisateur, false sinon
     */
    public boolean updateOptionUser(int userId, int OptionId)
    {

        try {
            String sql = "UPDATE " + table("utilisateur") + " set options_id=? where utilisateur_id=? ; ";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, OptionId);
            pstmt.setInt(2,userId);

            int count = pstmt.executeUpdate();
            return (count > 0);


        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Permet de convertir l'acronyme du genre en String
     *
     * @param genre acronyme du genre
     * @return String du genre
     */
    private String genreString(String genre) {
         if (genre == null)
            return "Pas renseigné" ;
        else if(genre.equals("t"))
            return "Homme";
        else
            return "Femme";
        
    }
    
    /**
     * Permet de convertir l'id du statut en string
     *
     * @param statutID l'ID du statut
     * @return Nom du statut
     */
    private String statutString(int statutID) {
         String Statut = "";
        
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("statut")
                    + "WHERE statut_id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            
            pstmt.setInt(1, statutID);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            Statut = rs.getString("nom");
    
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Statut;
        
    }
     
     /**
     * Find users by his/her ID
     *
     * @param UtilisateurID l'id de l'utilisateur
      * @return l'Utilisateur trouvé, ou null si pas trouvé
     */
    public Utilisateur UtilisateurByID(int UtilisateurID) {

        Utilisateur user = null;
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("utilisateur")
                    + "WHERE utilisateur_id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            
            pstmt.setInt(1, UtilisateurID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                user = new Utilisateur( rs.getInt("utilisateur_id"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("pseudo"),
                        genreString(rs.getString("genre")),
                        rs.getString("anniversaire"),
                        rs.getString("cree_a"),
                        rs.getString("droit_id"),
                        statutString(rs.getInt("statut_id")),
                        paysString(rs.getInt("pays_id")),
                        optionsString(rs.getInt("options_id")),
                        rs.getDouble("solde"));
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }

        return user;
        
    }

    /**
     * Display users
     *
     * @param
     * @throws SQLException
     */
    private ArrayList<String> display_Utilisateurs(){
        
        // HashMap<String, String> users = new HashMap();
        // users.put(url, url)
        
        ArrayList<String> uniqueValues = new ArrayList<>();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table("utilisateur") );

            
            while (rs.next()) {
                uniqueValues.add(rs.getString("pseudo"));
                uniqueValues.add(rs.getString("email"));
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return uniqueValues;
    }


    /**
     * Permet de gérer l'enregistrement d'un utilisateur
     *
     * @params ...
     * @throws
     */
    public int addUser(String prenom, String nom, String email, String pseudo, String mdp,
                       String genre, String anniversaire,int statut,int Pays,int Option,double solde){
        boolean genreVal = Integer.parseInt(genre) == 1 ? true : false;
        return insert_Utilisateurs(prenom,nom,email,pseudo,mdp,genreVal,anniversaire,statut,Pays,Option,solde);
    }


    /**
     * Permet de verifier la validiter du nouveau utilisateur
     *
     * @params email,pseudo
     * @throws 
     */
    private boolean checkUniqueUser(String email, String pseudo){
        
        ArrayList<String> uniqueValues = display_Utilisateurs();
        
        if (uniqueValues.contains(email) || uniqueValues.contains(pseudo))
            return false;
        
        return true;
    }


    /**
     * Permet de verifier la validité d'une modification de profil (Nom et email)
     *
     * @params email,pseudo,userId
     * @throws
     */
    private boolean checkUniqueUserWithId(String email, String pseudo, int userId) {
        String sql = "Select * FROM " + table("utilisateur") + " WHERE (email=? OR pseudo=?) AND utilisateur_id!=?;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, pseudo);
            pstmt.setInt(3, userId);
            ResultSet rs = pstmt.executeQuery();
            return !rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;


    }

    /**
     * Permet d'insérer un nouveau utilisateur à la BDD
     *
     * @params ...
     * @throws 
     */
    private int insert_Utilisateurs(String prenom, String nom,String email,String pseudo,String mdp,Boolean genre,String anniversaire,int statut_id, int pays_id, int options_id, double solde){
        int droit_id = 2;
        int ok = 0;
        
        try {
            
            if ( !checkUniqueUser(email,pseudo))
                return ok;
            
            
            String SQL = "INSERT INTO "
                    + table("utilisateur")
                    + "(prenom, nom, email, pseudo, mdp, genre, anniversaire, droit_id, statut_id, pays_id, options_id, solde   ) "
                    + "VALUES "
                    + "('" + prenom +"','"+ nom +"','"+ email +"','"+ pseudo +"','"+ mdp +"',"
                    + genre.toString() +",'"+ anniversaire +"',"+ droit_id +","+ statut_id +","+ pays_id +","+ options_id + ", " +solde +" )" +
                    " RETURNING utilisateur_id;";
            
            Statement st = conn.createStatement();
            st.executeUpdate(SQL,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()){
                ok = rs.getInt(1);
            }

            
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }

    /**
     * Check COnnection and get ID back
     *
     * @param String passwd (Mot de passe)
     * @param String userN (Nom de l'utilisateur)
     * @throws SQLException
     */
    public int checkConnectionGetId(String passwd, String userN){

        // HashMap<String, String> users = new HashMap();
        // users.put(url, url)


        try {
            //Statement st = conn.createStatement();
            //ResultSet rs = st.executeQuery("SELECT * FROM " + table("Utilisateur") + " WHERE pseudo = ? AND mdp = ?; );
            String SQL = "SELECT * "
                    + "FROM " + table("utilisateur")
                    + "WHERE pseudo = ?;";

            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, userN);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
            {
                //return rs.getInt("utilisateur_id");
                if(BCrypt.checkpw(passwd, rs.getString("mdp")))
                {
                    return rs.getInt("utilisateur_id");
                }
                return 0;
            }
            else
            {
                return 0;
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * Get all Pays
     *
     * @param
     * @throws SQLException
     */
    public ArrayList<Pays> get_Pays(){

        ArrayList<Pays> listPays = new ArrayList<Pays>();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table("pays") );

            while (rs.next()) {
                // Gère pas encore la couleur a voir !!
                Pays pays = new Pays( rs.getInt("pays_id"),rs.getString("nom") );
                listPays.add(pays);

            }

            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listPays;
    }

    /**
     * Get all Pays
     *
     * @param
     * @throws SQLException
     */
    public ArrayList<Statut> get_Statut(){

        ArrayList<Statut> listStatut = new ArrayList<Statut>();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table("statut") );

            while (rs.next()) {
                // Gère pas encore la couleur a voir !!
                Statut statut = new Statut( rs.getInt("statut_id"),rs.getString("nom") );
                listStatut.add(statut);

            }

            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listStatut;
    }

     /**
     * Display Categorie
     *
     * @param
     * @throws SQLException
     */
    public ArrayList<Categorie> display_Categories(){

        ArrayList<Categorie> listCategorie = new ArrayList<>();
        
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table("categorie") );
            
            while (rs.next()) {
                // Gère pas encore la couleur a voir !!
                Categorie categorie = new Categorie( rs.getInt("categorie_id"),rs.getString("nom") );
                listCategorie.add(categorie);

            }
            
            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listCategorie;
    }

    /**
     * Find categorie by his/her ID
     *
     * @param CategorieID
     */
    public Categorie CategorieByID(int CategorieID) {
        Categorie categorie = null;
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("categorie")
                    + " WHERE categorie_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, CategorieID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                categorie = new Categorie(rs.getInt(1),rs.getString("nom"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return categorie;
    }

    /**
     * Find categorie by his/her name
     *
     * @param nom
     */
    public Categorie get_Categorie(String nom) {
        Categorie categorie = null;
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("categorie")
                    + " WHERE nom = ? ";

            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                categorie = new Categorie( rs.getInt(1),rs.getString("nom"));

            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }

        return categorie;

    }

    /**
     * Get Sous_categorie by
     *
     * @param
     * @throws SQLException
     */
    public ArrayList<SousCategorie> get_Sous_categorie(int categorie_id) {
        
        ArrayList<SousCategorie> listSousCategorie = new ArrayList<>();
        
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM " + table("sous_categorie") + " WHERE categorie_id = ?");
            st.setInt(1, categorie_id);
            ResultSet rs = st.executeQuery();
            
            while (rs.next())
            {
                Categorie categorie = CategorieByID(rs.getInt( "categorie_id" ));
                SousCategorie sousCat = new SousCategorie(rs.getInt(1),rs.getString("nom"),categorie);
                listSousCategorie.add(sousCat);
            }
            
            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return listSousCategorie;
    }
    
    /**
     * Permet de verifier la validiter d'une nouvelle sous categorie
     *
     * @params email,pseudo
     * @throws 
     */
    private boolean checkUniqueSousCategorie(String nom, int categorie_id){
        
        ArrayList<SousCategorie> uniqueValues = get_Sous_categorie(categorie_id);

        for(SousCategorie sousCat : uniqueValues){
            if (sousCat.nom == nom)
                return false;
        }

        
        return true;
    }
    
    /**
     * Permet d'insérer une nouvelle Sous_categorie à la BDD
     *
     * @params ...
     * @throws 
     */
    /*
    public boolean insert_Sous_categorie(String nom, int categorie_id){
        boolean ok = false;
        
        try {
            
            if ( !checkUniqueSousCategorie(nom, categorie_id ))
                return ok;
            
            
            String SQL = "INSERT INTO "
                    + table("Sous_categorie")
                    + "(nom, categorie_id) "
                    + "VALUES "
                    + "('" + nom +"'," + categorie_id + ");";
            
            Statement st = conn.createStatement();
            st.executeUpdate(SQL);
            ok = true;
            
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }
*/


    /**
     * Permet d'insérer une nouvelle Sous_categorie à la BDD
     *
     * @params ...
     * @throws
     */
    public boolean insert_Sous_categorie(SousCategorie sousCategorie, int idUser){
        boolean ok = false;
        int idSousCat = 0 ;

        try {

            int idCat = sousCategorie.categorie.id;
            if ( !checkUniqueSousCategorie(sousCategorie.nom, idCat))
                return ok;


            String SQL = "INSERT INTO "
                    + table("sous_categorie")
                    + "(nom, categorie_id, is_global) "
                    + "VALUES "
                    + "('" + sousCategorie.nom +"'," + sousCategorie.categorie.id + ", false );";

            Statement st = conn.createStatement();

            st.executeUpdate(SQL,Statement.RETURN_GENERATED_KEYS);
            // Récupère l'id de la nouvelle sous catégorie
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()){
                idSousCat = rs.getInt(1);

                ok = true;
                updateSousCatPerso(idSousCat, idUser);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }

    /**
     * Renvoie l'ID d'une sous categorie suivant son nom
     *
     * @param sousCategorie nom de la sous categorie
     * @return l'ID de la sous categorie
     */
    public int getSousCategorieID(String sousCategorie){
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("sous_categorie")
                    + " WHERE nom = ? ";

            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, sousCategorie);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                return rs.getInt("sous_categorie_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return 0;
    }
    /**
     * Get Type de transaction in BD
     *
     * @param
     * @throws SQLException
     */
    public ArrayList<String> get_Type_transaction() {

        ArrayList<String> listType_transaction = new ArrayList<>();

        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM " + table("type_transaction"));
            ResultSet rs = st.executeQuery();

            while (rs.next())
            {
                listType_transaction.add( rs.getString("type") );
            }

            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listType_transaction;
    }

    public ArrayList<Integer> getSousCategorieMonthly(int userID, int sousCatID) {
        String SQL = "SELECT SUM(Transaction.valeur) as Somme FROM " + table("utilisateur") +
                "INNER JOIN " + table("Modele_transaction") + "ON Modele_transaction.utilisateur_id = Utilisateur.id " +
                "INNER JOIN " + table("Transaction") + "ON Modele_transaction.modele_transaction_id = Transaction.modele_transaction_id " +
                "WHERE Utilisateur.id = ? AND Modele_transaction.sous_categorie_id = ? " + "GROUP BY MONTH(Transaction.date);";

        ArrayList<Integer> sommes = new ArrayList<>();

        try{
            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(2, sousCatID);
            pstmt.setInt(1, userID);

            ResultSet rs = pstmt.executeQuery();


            while (rs.next()) {
                sommes.add(rs.getInt(1));
            }

        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sommes;
    }

    /**
     * Permet d'ajouter un revenu
     *
     * @param userID            ID de l'utilisateur
     * @param valeur            valeur du revenu
     * @param sousCategorie     sous categorie
     * @param recurrence        recurrence du payement
     * @param note              note de l'utilisateur concernant le payement
     * @return                  retoure 0 s'il n'y a pas eu de probleme, -1 autrement
     */
    public int addIncome(int userID, int valeur, String sousCategorie, String recurrence, String note){
        String SQL = "INSERT INTO " + table("modele_transaction") + "(valeur, date, note, utilisateur_id, sous_categorie_id, type_transaction_id, recurrence_id) " +
                        "VALUES (?, NOW(),?, ?, ?, ?, ?);";

        try{
            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, valeur);
            pstmt.setString(2, note);
            pstmt.setInt(3, userID);
            pstmt.setInt(4, getSousCategorieID(sousCategorie));
            pstmt.setInt(5, 2);
            if(recurrence == null){
                pstmt.setNull(6, Types.INTEGER);
            }else{
                pstmt.setInt(6, getRecIdByName(recurrence));
            }

            //ResultSet rs = pstmt.executeQuery();
            pstmt.executeUpdate();
            return 0;
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * Permet d'ajouter une depense
     *
     * @param userID            ID de l'utilisateur
     * @param valeur            valeur de le depense
     * @param sousCategorie     sous categorie
     * @param recurrence        recurrence du payement
     * @param note              note de l'utilisateur concernant le payement
     * @return                  retoure 0 s'il n'y a pas eu de probleme, -1 autrement
     */
    public int addExpense(int userID, int valeur, String sousCategorie, String recurrence, String note){
        String SQL = "INSERT INTO " + table("modele_transaction") + "(valeur, date, note, utilisateur_id, sous_categorie_id, type_transaction_id, recurrence_id) " +
                "VALUES (?, NOW(),?, ?, ?, ?, ?);";

        try{
            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, valeur);
            pstmt.setString(2, note);
            pstmt.setInt(3, userID);
            pstmt.setInt(4, getSousCategorieID(sousCategorie));
            pstmt.setInt(5, 1);
            if(recurrence == null){
                pstmt.setNull(6, Types.INTEGER);
            }else{
                pstmt.setInt(6, getRecIdByName(recurrence));
            }

            pstmt.executeUpdate();
            return 0;
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * Permet d'ajouter une depense
     *
     * @param userID            ID de l'utilisateur
     * @param valeur            valeur de le depense
     * @param idSousCategorie   id de sous categorie
     * @param recurrenceId      id de la recurrence du payement
     * @param note              note de l'utilisateur concernant le payement
     * @param idTypeTrans       id de du type de transaction
     * @return                  retoure 0 s'il n'y a pas eu de probleme, -1 autrement
     */
    public int addMovement(int userID, double valeur, int idSousCategorie, int recurrenceId, String note, int idTypeTrans){
        String SQL = "INSERT INTO " + table("modele_transaction") + "(valeur, date, note, utilisateur_id, sous_categorie_id, type_transaction_id, recurrence_id) " +
                "VALUES ("+ valeur +", NOW(),?, ?, ?, ?, ?);";

        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);

            //pstmt.setBigDecimal(1, valeur);
            pstmt.setString(1, note);
            pstmt.setInt(2, userID);
            //Gestion sans categorisation;
            if (idSousCategorie == 0)
            {
                pstmt.setInt(3, 98);
            }
            else
            {
                pstmt.setInt(3, idSousCategorie);
            }

            pstmt.setInt(4, idTypeTrans);
            if(recurrenceId == 0){
                pstmt.setNull(5, Types.INTEGER);
            }else{
                pstmt.setInt(5, recurrenceId);
            }

            pstmt.executeUpdate();
            return 0;
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }



    public ArrayList<Categorie> getAllCategories() {
        ArrayList<Categorie> categories = new ArrayList<Categorie>();
        try {
            String SQL = "SELECT categorie_id " + "FROM " + table("categorie");

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(SQL);

            while (rs.next()) {

                categories.add(CategorieByID(rs.getInt("categorie_id")));

            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return categories;
    }

    /*public ArrayList<Integer> getTenLastSousCategorie(int userID, int sousCatID) {
        String SQL = "SELECT SUM(Transaction.valeur) as Somme FROM " + table("Utilisateur") +
                "INNER JOIN " + table("Modele_transaction") + "ON Modele_transaction.utilisateur_id = Utilisateur.id " +
                "INNER JOIN " + table("Transaction") + "ON Modele_transaction.modele_transaction_id = Transaction.modele_transaction_id " +
                "WHERE Utilisateur.id = ? AND Modele_transaction.sous_categorie_id = ? " + "GROUP BY MONTH(Transaction.date)";

*/
    /**
     * Renvoie les 10 dernieres depenses toutes categories confondues
     *
     * @param userID        ID de l'utilisateur
     * @param sousCatID     ID de la sous categorie
     * @return              retourne une liste contenant les 10 dernieres transactions de la sous categorie
     */
    public ArrayList<Integer> getTenLastSousCategorie(int userID, int sousCatID) {
        String SQL = "SELECT Transaction.valeur, Modele_transaction.type_transaction_id, Transaction.date FROM " + table("modele_transaction")
                        + " INNER JOIN " + table("Transaction") + " ON Modele_transaction.modele_transaction_id = Transaction.modele_transaction_id"
                        + " WHERE Modele_transaction.utilisateur_id = ? AND Modele_transaction.sous_categorie_id = ?"
                        + " ORDER BY Transaction.date DESC LIMIT 10;";

        ArrayList<Integer> sommes = new ArrayList<>();

        try{
            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, userID);
            pstmt.setInt(2, sousCatID);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                sommes.add(rs.getInt(1));
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sommes;
    }

    private int getRecIdByName(String recurrence)
    {
        String sql = "SELECT recurence_id FROM " + table("recurence") + " WHERE periodicite = ?;";
        int result = 0;
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, recurrence);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result = rs.getInt(1);
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public double getSoldeById(int userId)
    {
        String sql = "SELECT solde FROM " + table("utilisateur") + " WHERE utilisateur_id = ?;";
        double result = 0;
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                result = rs.getDouble(1);
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    /**
     * Renvoie les 10 dernieres depenses tout confondu
     *
     * @param   userID ID de l'utilisateur
     * @return  retourne un tableau 2 dimensions avec 2 arrayList, la 1ere avec la categorie, la 2eme avec le montant
     */
    public ArrayList<Integer> DixDerniersMouvements(int userID){
        String SQL = "SELECT Transaction.valeur, Modele_transaction.type_transaction_id, Modele_transaction.sous_categorie_id, Transaction.date"
                        + " FROM Modele_transaction"
                        + " INNER JOIN Transaction ON Modele_transaction.modele_transaction_id = Transaction.modele_transaction_id"
                        + " WHERE Modele_transaction.utilisateur_id = ?"
                        + " ORDER BY Transaction.date DESC LIMIT 10;";

        ArrayList<Integer> sommes = new ArrayList<>();

        try{
            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, userID);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                sommes.add(rs.getInt(1));
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sommes;
    }

    /**
     * Récupère les diffèrentes récurences
     *
     * @return une ArrayList<Recurrence> contenant toutes les récurences de la base de donnée
     */
    public ArrayList<Recurrence> getRecurrence()
    {
        String sql = "SELECT * FROM " + table("recurence") + ";";
        ArrayList<Recurrence> recs = new ArrayList<>();
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                recs.add(new Recurrence(rs.getInt("recurence_id"),rs.getString("periodicite")));
            }
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recs;

    }

    public void updateSousCatPerso(int id_sous_cat, int id_user){
        String SQL  = "CALL add_sous_cat_perso(?, ?)";

        try{
            CallableStatement cs = conn.prepareCall(SQL);

            //Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, "id user :" + id_user, "" );

            cs.setInt(1, id_user);
            cs.setInt(2, id_sous_cat);

            cs.execute();
        }
        catch(SQLException ex){
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {

        BDD app = new BDD();
        System.out.println("------------------Utilisateurs------------------------------------------------");
        app.display_Utilisateurs();
        System.out.println("------------------Categorie------------------------------------------------");
        app.display_Categories();
        System.out.println("------------------Sous categorie------------------------------------------------");
        app.get_Sous_categorie(4);
        System.out.println("------------------Utilisateurs 1------------------------------------------------");
        app.UtilisateurByID(1);
        System.out.println("------------------Utilisateurs 2------------------------------------------------");
        app.UtilisateurByID(2);
        System.out.println("------------------Utilisateurs 3------------------------------------------------");
        app.UtilisateurByID(3);
        
        System.out.println("------------------Insert Utilisateurs ------------------------------------------------");
        if (app.insert_Utilisateurs( "prenom1",  "nom2", "email2", "pseudo1", "mdp_evadvservsrevervrev_vdfvdfvdF_vfdvdf_lol", true, "1950-03-11", 2,  23,  1,0) == 0)
            System.out.println("erreur !");
        
        System.out.println("------------------Insert Sous_categorie------------------------------------------------");


        System.out.println("------------------Insert get categorie------------------------------------------------");
        Categorie cat = app.CategorieByID( 1 );
        System.out.println(cat.id);

        System.out.println("------------------Get Type_transaction------------------------------------------------");
        System.out.println(app.get_Type_transaction());
    }
}
