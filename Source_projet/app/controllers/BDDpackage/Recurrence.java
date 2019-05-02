package controllers.BDDpackage;


// Pour afficher une Enum : java.util.Arrays.asList(Recurrence.values())
public enum Recurrence {
    Annuel(1, "Annuel"),
    Mensuel(2, "Mensuel"),
    Hebdomadaire(3, "Hebdomadaire"),
    Quotidien(4, "Quotidien"),
    Trimestriel(5, "Trimestriel"),
    Semestriel(6, "Semestriel");

    private final int id;
    private final String name;

    Recurrence(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }
    public static int getIdByName(String name){
        for(Recurrence r : java.util.Arrays.asList(Recurrence.values())){
            if(r.name == name) return r.id;
        }
        return -1;
    }

    
}
