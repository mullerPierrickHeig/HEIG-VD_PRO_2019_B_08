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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;




/*
 *
 * @author spine
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
     * @param TableName
     */
    private String table(String TableName) {
        return "Public.\"" + TableName + "\" ";   
    }
    /**
     * Permet de convertir l'id d'un pays en string
     *
     * @param PaysID
     */
    private String paysString(int PaysID) {
        String pays = "";
        
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("Pays")
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
     * @param OptionsID
     */
    private ArrayList<Boolean> optionsString(int OptionsID) {
        ArrayList<Boolean> options = new ArrayList<Boolean>();
        
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("Options")
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

    public boolean updateOptionUser(int userId, int OptionId)
    {
        try {
            String sql = "UPDATE " + table("Utilisateur") + " set options_id=? where utilisateur_id=? ; ";

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
     * Permet de convertir le boolean du genre en String
     *
     * @param genre
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
     * @param statutID
     */
    private String statutString(int statutID) {
         String Statut = "";
        
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("Statut")
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
     * @param UtilisateurID
     */
    public Utilisateur UtilisateurByID(int UtilisateurID) {

        Utilisateur user = null;
        try {
            String SQL = "SELECT * "
                    + "FROM " + table("Utilisateur")
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
                        optionsString(rs.getInt("options_id")) );
                
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
            ResultSet rs = st.executeQuery("SELECT * FROM " + table("Utilisateur") );

            
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
                       String genre, String anniversaire,int statut,int Pays,int Option){
        Boolean genreVal = Integer.parseInt(genre) == 1 ? true : false;
        if(checkUniqueUser(email,pseudo))
        {
           return insert_Utilisateurs(prenom,nom,email,pseudo,mdp,genreVal,anniversaire,statut,Pays,Option);
        }
        return 0;
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
     * Permet d'insérer un nouveau utilisateur à la BDD
     *
     * @params ...
     * @throws 
     */
    private int insert_Utilisateurs(String prenom, String nom,String email,String pseudo,String mdp,Boolean genre,String anniversaire,int statut_id, int pays_id, int options_id){
        int droit_id = 2;
        int ok = 0;
        
        try {
            
            if ( !checkUniqueUser(email,pseudo))
                return ok;
            
            
            String SQL = "INSERT INTO "
                    + table("Utilisateur")
                    + "(prenom, nom, email, pseudo, mdp, genre, anniversaire, droit_id, statut_id, pays_id, options_id) "
                    + "VALUES "
                    + "('" + prenom +"','"+ nom +"','"+ email +"','"+ pseudo +"','"+ mdp +"',"
                    + genre.toString() +",'"+ anniversaire +"',"+ droit_id +","+ statut_id +","+ pays_id +","+ options_id + ")" +
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
                    + "FROM " + table("Utilisateur")
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
            ResultSet rs = st.executeQuery("SELECT * FROM " + table("Pays") );

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
            ResultSet rs = st.executeQuery("SELECT * FROM " + table("Statut") );

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
            ResultSet rs = st.executeQuery("SELECT * FROM " + table("Categorie") );
            
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
                    + "FROM " + table("Categorie")
                    + " WHERE categorie_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setInt(1, CategorieID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Gérer la couleur !!!!!!!
                //Color color = Color.decode( rs.getString("couleur") );
                //Color color2 = Color.getColor( rs.getString("couleur") );

                categorie = new Categorie( rs.getInt(1),rs.getString("nom"));

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
                    + "FROM " + table("Categorie")
                    + " WHERE nom = ? ";

            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Gérer la couleur !!!!!!!
                //Color color = Color.decode( rs.getString("couleur") );
                //Color color2 = Color.getColor( rs.getString("couleur") );

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
            PreparedStatement st = conn.prepareStatement("SELECT * FROM " + table("Sous_categorie") + " WHERE categorie_id = ?");
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
    public boolean insert_Sous_categorie(SousCategorie sousCategorie){
        boolean ok = false;

        try {

            int idCat = sousCategorie.categorie.id;
            if ( !checkUniqueSousCategorie(sousCategorie.nom, idCat))
                return ok;


            String SQL = "INSERT INTO "
                    + table("Sous_categorie")
                    + "(nom, categorie_id) "
                    + "VALUES "
                    + "('" + sousCategorie.nom +"'," + sousCategorie.categorie.id + ");";

            Statement st = conn.createStatement();
            st.executeUpdate(SQL);
            ok = true;

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
                    + "FROM " + table("Sous_categorie")
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
            PreparedStatement st = conn.prepareStatement("SELECT * FROM " + table("Type_transaction"));
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
        String SQL = "SELECT SUM(Transaction.valeur) as Somme FROM " + table("Utilisateur") +
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
        String SQL = "INSERT INTO " + table("Modele_transaction") + "(valeur, date, note, utilisateur_id, sous_categorie_id, type_transaction_id, recurrence_id) " +
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
                pstmt.setInt(6, Recurrence.getIdByName(recurrence));
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
        String SQL = "INSERT INTO " + table("Modele_transaction") + "(valeur, date, note, utilisateur_id, sous_categorie_id, type_transaction_id, recurrence_id) " +
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
                pstmt.setInt(6, Recurrence.getIdByName(recurrence));
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
     * Renvoie les 10 dernieres depenses toutes categories confondues
     *
     * @param userID        ID de l'utilisateur
     * @param sousCatID     ID de la sous categorie
     * @return              retourne une liste contenant les 10 dernieres transactions de la sous categorie
     */
    public ArrayList<Integer> getTenLastSousCategorie(int userID, int sousCatID) {
        String SQL = "SELECT Transaction.valeur, Modele_transaction.type_transaction_id, Transaction.date FROM " + table("Modele_transaction")
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
        if (app.insert_Utilisateurs( "prenom1",  "nom2", "email2", "pseudo1", "mdp_evadvservsrevervrev_vdfvdfvdF_vfdvdf_lol", true, "1950-03-11", 2,  23,  1) == 0)
            System.out.println("erreur !");
        
        System.out.println("------------------Insert Sous_categorie------------------------------------------------");


        System.out.println("------------------Insert get categorie------------------------------------------------");
        Categorie cat = app.CategorieByID( 1 );
        System.out.println(cat.id);

        System.out.println("------------------Get Type_transaction------------------------------------------------");
        System.out.println(app.get_Type_transaction());
        


    }
    
}
