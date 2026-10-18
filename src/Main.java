
import java.util.Scanner;
import java.util.Locale;

import elementy_symulacji.*;
import sportowcy.Sportowiec;
import symulacja.*;
import wejscie.*;
import struktury_symulacji.*;
import wizualizacja.GeneratorWizualizacji;
import kadra.mapki.GeneratorMapek;
import kadra.mapki.pliki.WyjatekSystemuPlikow;
// Główna klasa uruchamiająca symulację: wczytuje dane, uruchamia silnik, generuje raport i mapki oraz obsługuje wyjątki
public class Main {
    public static void main(String[] args) {
        // Ścieżkę katalogu na mapki podaje użytkownik jako argument programu
        if (args.length < 1) {
            System.err.println("Brak argumentu: podaj ścieżkę katalogu na mapki. Uruchom np.: java -ea Main \"katalog/na/mapki\" < dane.txt");
            return;
        }
        String katalogMapek = args[0];
        try {
            // Tworzymy generator (i katalog na mapki) na początku - jeśli ścieżka jest błędna, dowiemy się od razu
            GeneratorMapek generatorMapek = new GeneratorMapek(katalogMapek);

            Scanner skaner = new Scanner(System.in);
            skaner.useLocale(Locale.ENGLISH);
            Osrodek osrodek = new Osrodek();
            KolejkaZdarzen kolejka = new KolejkaZdarzenBiblioteczna();
            WczytanieDanych wczytywarka = new WczytanieDanych();
            wczytywarka.wczytaj(skaner, osrodek, kolejka);
            skaner.close();

            SilnikSymulacji silnik = new SilnikSymulacji(kolejka);
            silnik.uruchom();
            osrodek.generujRaportKoncowy(SilnikSymulacji.POCZATEK_DNIA, SilnikSymulacji.KONIEC_SYMULACJI);

            // Wszystkie mapki generujemy tym samym (jedynym) obiektem generatora
            GeneratorWizualizacji wizualizacja = new GeneratorWizualizacji(generatorMapek, osrodek, SilnikSymulacji.POCZATEK_DNIA, SilnikSymulacji.KONIEC_SYMULACJI);
            wizualizacja.generujMapeParametrow("parametry.tex");
            wizualizacja.generujMapeStatystyk("statystyki.tex");
            for (Sportowiec sledzony : osrodek.getSledzeni()) {
                wizualizacja.generujMapeHistorii(sledzony, "historia-sportowiec-" + sledzony.getId() + ".tex");
            }
        } catch (WyjatekSystemuPlikow e) {
            // Problem z systemem plików (np. zła ścieżka, brak uprawnień) - sugerujemy użytkownikowi sprawdzenie ścieżki i uprawnień
            System.err.println("Błąd systemu plików podczas generowania mapek. Upewnij się, że podana ścieżka katalogu jest poprawna i masz uprawnienia do zapisu w tej lokalizacji.");
            e.printStackTrace();
        } catch (Exception e) {
            // Inny wyjątek świadczy prawdopodobnie o błędzie programisty - prosimy o zgłoszenie zespołowi deweloperskiemu
            System.err.println("Wystąpił nieoczekiwany błąd programu. Prosimy zgłosić ten problem zespołowi deweloperskiemu.");
            e.printStackTrace();
        }
    }
}
