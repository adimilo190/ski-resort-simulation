package wejscie;
import elementy_symulacji.*;
import sportowcy.*;
import struktury_symulacji.*;
import symulacja.*;
import java.util.Scanner;
import java.util.Locale;
// Klasa odpowiedzialna za wczytywanie danych z wejścia i inicjalizację stanu początkowego ośrodka narciarskiego oraz kolejki zdarzeń dla symulacji
public class WczytanieDanych {
    // Główna metoda wczytująca dane
    public void wczytaj(Scanner wejscie, Osrodek osrodek, KolejkaZdarzen kolejka) {
        
        int liczbaWezlow = Integer.parseInt(wczytajNiepustaLinie(wejscie));
        wczytajWezly(wejscie, liczbaWezlow, osrodek);

        int liczbaWyciagow = Integer.parseInt(wczytajNiepustaLinie(wejscie));
        wczytajWyciagi(wejscie, liczbaWyciagow, osrodek, kolejka);

        int liczbaTras = Integer.parseInt(wczytajNiepustaLinie(wejscie));
        wczytajTrasy(wejscie, liczbaTras, osrodek);

        int liczbaGrup = Integer.parseInt(wczytajNiepustaLinie(wejscie));
        wczytajGrupySportowcow(wejscie, liczbaGrup, osrodek, kolejka);
    }
    // Metoda wczytująca dane o węzłach
    private void wczytajWezly(Scanner wejscie, int liczbaWezlow, Osrodek osrodek) {
        for (int i = 0; i < liczbaWezlow; i++) {
            Scanner skanerLinii = new Scanner(wczytajNiepustaLinie(wejscie));
            int wysokosc = skanerLinii.nextInt();
            int x = skanerLinii.nextInt();
            int y = skanerLinii.nextInt();
            boolean skomunikowany = skanerLinii.hasNext() && skanerLinii.next().equals("s");
            Wezel wezel = new Wezel(i, wysokosc, x, y, skomunikowany);
            osrodek.dodajWezel(wezel);
        }
    }
    // Metoda wczytująca dane o wyciągach
    private void wczytajWyciagi(Scanner wejscie, int liczbaWyciagow, Osrodek osrodek, KolejkaZdarzen kolejka) {
        for (int i = 0; i < liczbaWyciagow; i++) {
            Scanner skanerLinii = new Scanner(wczytajNiepustaLinie(wejscie));
            
            int startId = skanerLinii.nextInt();
            int koniecId = skanerLinii.nextInt();
            int odstepCzasowy = skanerLinii.nextInt();
            int maksymalnaGrupa = skanerLinii.nextInt();
            int czasPrzejazdu = skanerLinii.nextInt();

            Wezel start = osrodek.getWezel(startId);
            Wezel koniec = osrodek.getWezel(koniecId);
            Wyciag wyciag = new Wyciag(i, start, koniec, odstepCzasowy, maksymalnaGrupa, czasPrzejazdu);
            osrodek.dodajWyciag(wyciag);
            start.dodajWyciag(wyciag);
            kolejka.wstaw(new ZdarzenieOdjazdWyciagu(SilnikSymulacji.POCZATEK_DNIA, wyciag));
        }
    }
    // Metoda wczytująca dane o trasach
    private void wczytajTrasy(Scanner wejscie, int liczbaTras, Osrodek osrodek) {
        for (int i = 0; i < liczbaTras; i++) {
            Scanner skanerLinii = new Scanner(wczytajNiepustaLinie(wejscie));
            skanerLinii.useLocale(Locale.ENGLISH);
            int startId = skanerLinii.nextInt();
            int koniecId = skanerLinii.nextInt();
            int trudnosc = skanerLinii.nextInt();
            int czasPrzejazdu = skanerLinii.nextInt();
            double bazowaAtrakcyjnosc = skanerLinii.nextDouble();
            double odpornosc = skanerLinii.nextDouble();

            Wezel start = osrodek.getWezel(startId);
            Wezel koniec = osrodek.getWezel(koniecId);
            Trasa trasa = new Trasa(i, start, koniec, trudnosc, czasPrzejazdu, bazowaAtrakcyjnosc, odpornosc);
            osrodek.dodajTrase(trasa);
            start.dodajTrase(trasa);
        }
    }
    // Metoda wczytująca dane o grupach sportowców
    private void wczytajGrupySportowcow(Scanner wejscie, int liczbaGrup, Osrodek osrodek, KolejkaZdarzen kolejka) {
        int idSportowca = 0; 
        for (int i = 0; i < liczbaGrup; i++) {
            
            Scanner linia1 = new Scanner(wczytajNiepustaLinie(wejscie)).useLocale(Locale.ENGLISH);
            int liczbaWGrupie = linia1.nextInt();
            int poziom = linia1.nextInt();
            double epsilon = linia1.nextDouble();
            double beta = linia1.nextDouble();
            char rodzaj = linia1.next().charAt(0);
            boolean sledzony = linia1.hasNext() && linia1.next().equals("s");

            Scanner linia2 = new Scanner(wczytajNiepustaLinie(wejscie)).useLocale(Locale.ENGLISH);
            double wagaTrudnosci = linia2.nextDouble();
            double wagaWyrownania = linia2.nextDouble();
            double wagaZnudzenia = linia2.nextDouble();

            Scanner linia3 = new Scanner(wczytajNiepustaLinie(wejscie));
            int startWezelId = linia3.nextInt();
            String czasStartuStr = linia3.next();
            
            int odstep = 0;
            if (liczbaWGrupie > 1) {
                odstep = linia3.nextInt();
            }

            // Dekodowanie czasu ze Stringa i pobieranie węzła startowego
            int obecnyCzasPojawienia = czasNaSekundy(czasStartuStr);
            Wezel wezelStartowy = osrodek.getWezel(startWezelId);

            // Generowanie pojedynczych obiektów Sportowiec odpowiedniego rodzaju (L/Z/K)
            for (int j = 0; j < liczbaWGrupie; j++) {
                Sportowiec sportowiec = utworzSportowca(rodzaj, idSportowca, poziom, epsilon, beta,
                        wagaTrudnosci, wagaWyrownania, wagaZnudzenia, sledzony, wezelStartowy, osrodek);
                idSportowca++;
                if (sledzony) {
                    osrodek.dodajSledzonego(sportowiec);
                }
                kolejka.wstaw(new ZdarzeniePojawienieNaStoku(obecnyCzasPojawienia, sportowiec));
                obecnyCzasPojawienia += odstep;
            }
        }
    }
    // Tworzy sportowca odpowiedniego rodzaju na podstawie litery oznaczenia: L - Lokalny, Z - Zachłanny, K - Kolekcjoner
    private Sportowiec utworzSportowca(char rodzaj, int id, int poziom, double epsilon, double beta,
                                       double wagaTrudnosci, double wagaWyrownania, double wagaZnudzenia,
                                       boolean sledzony, Wezel start, Osrodek osrodek) {
        return switch (rodzaj) {
            case 'Z' -> new SportowiecZachlanny(id, poziom, epsilon, beta, wagaTrudnosci, wagaWyrownania, wagaZnudzenia, sledzony, start, osrodek);
            case 'K' -> new SportowiecKolekcjoner(id, poziom, epsilon, beta, wagaTrudnosci, wagaWyrownania, wagaZnudzenia, sledzony, start, osrodek);
            default  -> new SportowiecLokalny(id, poziom, epsilon, beta, wagaTrudnosci, wagaWyrownania, wagaZnudzenia, sledzony, start, osrodek);
        };
    }
    // Pomocnicza metoda do wczytywania niepustej linii z wejścia
    private String wczytajNiepustaLinie(Scanner skaner) {
        while (skaner.hasNextLine()) {
            String linia = skaner.nextLine().trim();
            if (!linia.isEmpty()) {
                return linia;
            }
        }
        return "";
    }
    // Pomocnicza metoda do konwersji czasu z formatu HH:MM:SS na sekundy
    private int czasNaSekundy(String czasHHMMSS) {
        String[] czesci = czasHHMMSS.split(":");
        int godziny = Integer.parseInt(czesci[0]);
        int minuty = Integer.parseInt(czesci[1]);
        int sekundy = Integer.parseInt(czesci[2]);
        return (godziny * 3600) + (minuty * 60) + sekundy;
    }
}
