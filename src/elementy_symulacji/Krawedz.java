package elementy_symulacji;
import sportowcy.Sportowiec;
//Klasa reprezentująca krawędź w grafie ośrodka narciarskiego, czyli trasę lub wyciąg
public abstract class Krawedz {
    // Atrybuty wspólne dla tras i wyciągów
    protected final int id;
    protected final Wezel poczatek;
    protected final Wezel koniec;
    protected final int czasPrzejazdu;
    protected int liczbaPrzejazdow;
    // Konstruktor, który będzie wywoływany przez klasy dziedziczące (Trasa i Wyciag)
    public Krawedz(int id, Wezel poczatek, Wezel koniec, int czasPrzejazdu) {
        this.id = id;
        this.poczatek = poczatek;
        this.koniec = koniec;
        this.czasPrzejazdu = czasPrzejazdu;
        this.liczbaPrzejazdow = 0;
    }
    // Metoda abstrakcyjna do obliczania atrakcyjności, która będzie implementowana w klasach Trasa i Wyciag
    public abstract double obliczAtrakcyjnosc(Sportowiec sportowiec);
    // Gettery dla atrybutów
    public int getId() { return id; }
    public Wezel getPoczatek() { return poczatek; } 
    public Wezel getKoniec() { return koniec; }
    public int getCzasPrzejazdu() { return czasPrzejazdu; }
    public int getLiczbaPrzejazdow() { return liczbaPrzejazdow; }
}