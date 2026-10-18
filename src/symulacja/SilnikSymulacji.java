package symulacja;

import struktury_symulacji.KolejkaZdarzen;
// Klasa SilnikSymulacji zarządza przebiegiem symulacji, przetwarzając zdarzenia z kolejki zdarzeń
public class SilnikSymulacji {
    private final KolejkaZdarzen kolejka;
    public static final int POCZATEK_DNIA = 9 * 3600;
    public static final int KONIEC_SYMULACJI = 15 * 3600;
    public SilnikSymulacji(KolejkaZdarzen kolejka) {
        this.kolejka = kolejka;
    }

    public KolejkaZdarzen getKolejka() {
        return kolejka;
    }
    // Metoda uruchamiająca symulację, która przetwarza zdarzenia z kolejki aż do jej wyczerpania lub osiągnięcia końca symulacji
    public void uruchom() {
        while (!kolejka.czyPusta()) {
            Zdarzenie z = kolejka.pobierz();
            if(z.getCzasWystapienia() >= KONIEC_SYMULACJI) {
                if(z instanceof ZdarzenieKoniecZjazduTrasa || z instanceof ZdarzenieKoniecWjazduWyciagiem) {
                    z.wykonaj(this);
                }
            } else {
                z.wykonaj(this);
            }
        }
    }
}