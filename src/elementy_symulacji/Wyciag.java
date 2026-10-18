package elementy_symulacji;
import struktury_symulacji.BuforCykliczny;
import sportowcy.Sportowiec;
// Klasa reprezentująca wyciąg narciarski, dziedzicząca po Krawędzi
public class Wyciag extends Krawedz {
    private final int odstepCzasowy;
    private final int maksymalnaGrupa;
    private final BuforCykliczny kolejkaOczekujacych;
    // Statystyki kolejki: maksymalna długość, suma (długość * sekundy) do liczenia średniej, czas ostatniej operacji oraz liczba odjazdów wagonika
    private int maksDlugoscKolejki;
    private long sumaSekundoDlugosci;
    private int czasOstatniejOperacji;
    private int liczbaOdjazdow;
    public Wyciag(int id, Wezel poczatek, Wezel koniec, int odstepCzasowy, int maksymalnaGrupa, int czasPrzejazdu) {
        super(id, poczatek, koniec, czasPrzejazdu);
        this.odstepCzasowy = odstepCzasowy;
        this.maksymalnaGrupa = maksymalnaGrupa;
        this.kolejkaOczekujacych = new BuforCykliczny(10);
        this.maksDlugoscKolejki = 0;
        this.sumaSekundoDlugosci = 0;
        this.czasOstatniejOperacji = -1;
        this.liczbaOdjazdow = 0;
    }
    // Implementacja metody obliczAtrakcyjnosc, która ocenia atrakcyjność wyciągu na podstawie atrakcyjności tras prowadzących z jego końca
    public double obliczAtrakcyjnosc(Sportowiec s) {
        double maxAtrakcyjnosc = 0.0; 
        for (int i = 0; i < koniec.getLiczbaTras(); i++) {
            Trasa t = koniec.getTrasa(i);
            double atrakcyjnoscTrasy = t.obliczAtrakcyjnosc(s);
            if (atrakcyjnoscTrasy >= maxAtrakcyjnosc) {
                maxAtrakcyjnosc = atrakcyjnoscTrasy;
            }
        }
        return maxAtrakcyjnosc;
    }
    // Metoda do rejestrowania przejazdu, która zwiększa licznik przejazdów o liczbę zabranych pasażerów
    public void rejestrujPrzejazd(int zabraniPasazerowie) { 
        liczbaPrzejazdow += zabraniPasazerowie; 
    }
    // Metoda do dodawania sportowca do kolejki oczekujących na wyciąg; aktualizuje statystyki kolejki w danym czasie
    public void dodajDoKolejki(Sportowiec s, int czas) {
        zarejestrujOperacjeKolejki(czas);
        kolejkaOczekujacych.dodaj(s);
        if (kolejkaOczekujacych.getDlugosc() > maksDlugoscKolejki) {
            maksDlugoscKolejki = kolejkaOczekujacych.getDlugosc();
        }
    }
    // Rejestruje odjazd wagonika w danym czasie: domyka statystyki kolejki (przed zabraniem pasażerów) i zlicza odjazd
    public void zarejestrujOdjazd(int czas) {
        zarejestrujOperacjeKolejki(czas);
        liczbaOdjazdow++;
    }
    // Domyka sumę długości kolejki do podanego czasu (wołane raz na koniec symulacji, by uwzględnić ostatni odcinek)
    public void zakonczStatystyki(int czasKonca) {
        zarejestrujOperacjeKolejki(czasKonca);
    }
    // Aktualizuje sumę (długość kolejki * liczba sekund) o odcinek od poprzedniej operacji do teraz - długość była przez ten czas stała (optymalizacja: nie liczymy co sekundę)
    private void zarejestrujOperacjeKolejki(int czas) {
        if (czasOstatniejOperacji >= 0) {
            sumaSekundoDlugosci += (long) kolejkaOczekujacych.getDlugosc() * (czas - czasOstatniejOperacji);
        }
        czasOstatniejOperacji = czas;
    }
    // Gettery do pobierania atrybutów i statystyk wyciągu
    public int getOdstepCzasowy() { return odstepCzasowy; }
    public int getMaksymalnaGrupa() { return maksymalnaGrupa; }
    public BuforCykliczny getKolejkaOczekujacych() { return kolejkaOczekujacych; }
    public int getMaksDlugoscKolejki() { return maksDlugoscKolejki; }
    public int getLiczbaOdjazdow() { return liczbaOdjazdow; }
    // Średnia długość kolejki liczona po wszystkich sekundach symulacji w oknie [poczatek, koniec)
    public double getSredniaDlugoscKolejki(int poczatek, int koniec) {
        return (double) sumaSekundoDlugosci / (koniec - poczatek);
    }
}
