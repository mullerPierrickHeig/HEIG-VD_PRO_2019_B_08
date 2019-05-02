package controllers.BDDpackage;


// Pour afficher une Enum : java.util.Arrays.asList(Recurrence.values())
public class Recurrence {

    private final int id;
    private final String name;

    Recurrence(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName() {return name;}

    
}
