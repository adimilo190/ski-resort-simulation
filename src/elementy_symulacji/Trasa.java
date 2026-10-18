package elementy_symulacji;
import sportowcy.Sportowiec;
// Klasa reprezentująca trasę, która dziedziczy po klasie Krawedz
public class Trasa extends Krawedz {
    // Atrybuty specyficzne dla trasy, takie jak poziom trudności, bazowa atrakcyjność i odporność na zużycie
    private final int poziomTrudnosci; 
    private final double bazowaAtrakcyjnosc; 
    private final double odpornosc; 

    public Trasa(int id, Wezel poczatek, Wezel koniec, int poziomTrudnosci, int czasPrzejazdu, double bazowaAtrakcyjnosc, double odpornosc) {
        super(id, poczatek, koniec, czasPrzejazdu);
        this.poziomTrudnosci = poziomTrudnosci; 
        this.bazowaAtrakcyjnosc = bazowaAtrakcyjnosc; 
        this.odpornosc = odpornosc;
    }

    public void rejestrujPrzejazd() {
        liczbaPrzejazdow++;
    }
    // Gettery parametrów trasy (używane m.in. przy generowaniu mapki parametrów)
    public int getPoziomTrudnosci() { return poziomTrudnosci; }
    public double getBazowaAtrakcyjnosc() { return bazowaAtrakcyjnosc; }
    public double getOdpornosc() { return odpornosc; }
    // Metoda do obliczania atrakcyjności trasy dla danego sportowca, która uwzględnia dopasowanie trudności, wyrownanie nawierzchni oraz znudzenie tą trasą
    public double obliczAtrakcyjnosc(Sportowiec s) {
        double d = obliczDopasowanieTrudnosci(s.getPoziomZaawansowania());
        double w = obliczWyrownanie();
        double z = s.poziomZnudzenia(this);
        return s.getWagaTrudnosci() * d + s.getWagaWyrownania() * w + s.getWagaZnudzenia() * (1.0 - z);
    }
    // Zwraca wyrównanie nawierzchni trasy (wartość w) przy obecnej liczbie przejazdów - wywołane na koniec dnia daje wyrównanie końcowe (statystyka)
    public double getWyrownanie() {
        return obliczWyrownanie();
    }
    // Metody do obliczania wyrownania i dopasowania trudności
    private double obliczWyrownanie() {
        return bazowaAtrakcyjnosc + (1.0 - bazowaAtrakcyjnosc) * Math.pow(odpornosc, liczbaPrzejazdow);
    }
    private double obliczDopasowanieTrudnosci(int p_n) {
        if (poziomTrudnosci >= p_n + 5) return 0.0; 
        else if (poziomTrudnosci >= p_n) return 1.0 - ((poziomTrudnosci - p_n) / 5.0); 
        else return Math.max(0.2, 1.0 - ((p_n - poziomTrudnosci) / 5.0)); 
    }
}
