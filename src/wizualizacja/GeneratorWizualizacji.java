package wizualizacja;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
import elementy_symulacji.Wyciag;
import elementy_symulacji.Krawedz;
import sportowcy.Sportowiec;
import kadra.mapki.GeneratorMapek;
import kadra.mapki.pliki.WyjatekSystemuPlikow;
import kadra.mapki.styl.GruboscKonturu;
import kadra.mapki.styl.StylKrawedzi;
import kadra.mapki.styl.StylLinii;
import kadra.mapki.styl.StylWezla;
// Klasa generująca mapki ośrodka przy użyciu dostarczonego GeneratoraMapek (kadra.mapki)
// Wszystkie mapki z jednego uruchomienia powstają tym samym obiektem generatora; przed każdą mapką wołamy zeruj()
// Układ mapek jest wspólny (te same węzły i krawędzie); mapki różnią się tylko tekstem na krawędziach
public class GeneratorWizualizacji {
    private final GeneratorMapek generator;
    private final Osrodek osrodek;
    private final int poczatek;
    private final int koniec;
    public GeneratorWizualizacji(GeneratorMapek generator, Osrodek osrodek, int poczatek, int koniec) {
        this.generator = generator;
        this.osrodek = osrodek;
        this.poczatek = poczatek;
        this.koniec = koniec;
    }
    // Mapka 1: graf ośrodka z parametrami tras i wyciągów
    public void generujMapeParametrow(String nazwaPliku) throws WyjatekSystemuPlikow {
        generator.zeruj();
        dodajWezly();
        for (int i = 0; i < osrodek.getLiczbaTras(); i++) {
            Trasa t = osrodek.getTrasa(i);
            String linia1 = "t" + t.getId() + ": poziom: " + t.getPoziomTrudnosci() + ", czas: " + t.getCzasPrzejazdu() + "s";
            String linia2 = String.format(Locale.ENGLISH, "odporność: %.2f, %.5f", t.getBazowaAtrakcyjnosc(), t.getOdpornosc());
            dodajKrawedzTrasy(t, dwieLinie(linia1, linia2));
        }
        for (int i = 0; i < osrodek.getLiczbaWyciagow(); i++) {
            Wyciag w = osrodek.getWyciag(i);
            String linia1 = "w" + w.getId() + ": " + w.getMaksymalnaGrupa() + " os. co " + w.getOdstepCzasowy() + "s";
            String linia2 = "czas: " + w.getCzasPrzejazdu() + "s";
            dodajKrawedzWyciagu(w, dwieLinie(linia1, linia2));
        }
        generator.tworzMapke(nazwaPliku);
    }
    // Mapka 2: graf ośrodka ze statystykami tras i wyciągów na koniec dnia
    public void generujMapeStatystyk(String nazwaPliku) throws WyjatekSystemuPlikow {
        generator.zeruj();
        dodajWezly();
        for (int i = 0; i < osrodek.getLiczbaTras(); i++) {
            Trasa t = osrodek.getTrasa(i);
            String linia1 = String.format(Locale.ENGLISH, "t%d: śnieg: %.2f", t.getId(), t.getWyrownanie());
            String linia2 = "zjazdy: " + t.getLiczbaPrzejazdow();
            dodajKrawedzTrasy(t, dwieLinie(linia1, linia2));
        }
        for (int i = 0; i < osrodek.getLiczbaWyciagow(); i++) {
            Wyciag w = osrodek.getWyciag(i);
            w.zakonczStatystyki(koniec);
            long maksMozliwe = (long) w.getLiczbaOdjazdow() * w.getMaksymalnaGrupa();
            int procent = maksMozliwe == 0 ? 0 : (int) Math.round(100.0 * w.getLiczbaPrzejazdow() / maksMozliwe);
            String linia1 = "w" + w.getId() + ": kol: " + Math.round(w.getSredniaDlugoscKolejki(poczatek, koniec)) + "(śr), " + w.getMaksDlugoscKolejki() + "(maks)";
            String linia2 = "wjazdy: " + w.getLiczbaPrzejazdow() + " / " + maksMozliwe + " (" + procent + "%)";
            dodajKrawedzWyciagu(w, dwieLinie(linia1, linia2));
        }
        generator.tworzMapke(nazwaPliku);
    }
    // Mapka 3: historia przejazdów konkretnego śledzonego sportowca (liczba i numery jego przejazdów na każdej trasie/wyciągu)
    public void generujMapeHistorii(Sportowiec sledzony, String nazwaPliku) throws WyjatekSystemuPlikow {
        generator.zeruj();
        dodajWezly();
        // Zbieramy numery przejazdów sportowca per trasa i per wyciąg; numer przejazdu = pozycja na liście historii (od 1), numeracja wspólna
        Map<Trasa, List<Integer>> numeryTras = new HashMap<>();
        Map<Wyciag, List<Integer>> numeryWyciagow = new HashMap<>();
        List<Krawedz> historia = sledzony.getHistoriaPrzejazdow();
        for (int i = 0; i < historia.size(); i++) {
            Krawedz krawedz = historia.get(i);
            int numer = i + 1;
            if (krawedz instanceof Trasa trasa) {
                numeryTras.computeIfAbsent(trasa, k -> new ArrayList<>()).add(numer);
            } else {
                numeryWyciagow.computeIfAbsent((Wyciag) krawedz, k -> new ArrayList<>()).add(numer);
            }
        }
        for (int i = 0; i < osrodek.getLiczbaTras(); i++) {
            Trasa t = osrodek.getTrasa(i);
            dodajKrawedzTrasy(t, tekstHistorii("t", t.getId(), numeryTras.get(t)));
        }
        for (int i = 0; i < osrodek.getLiczbaWyciagow(); i++) {
            Wyciag w = osrodek.getWyciag(i);
            dodajKrawedzWyciagu(w, tekstHistorii("w", w.getId(), numeryWyciagow.get(w)));
        }
        generator.tworzMapke(nazwaPliku);
    }
    // Dodaje wszystkie węzły ośrodka; węzły skomunikowane mają pogrubiony kontur
    private void dodajWezly() {
        for (int i = 0; i < osrodek.getLiczbaWezlow(); i++) {
            Wezel w = osrodek.getWezel(i);
            GruboscKonturu grubosc = w.getSkomunikowany() ? GruboscKonturu.POGRUBIONY : GruboscKonturu.ZWYKLY;
            generator.dodajWezel(w.getId(), w.getX(), w.getY(), new StylWezla(grubosc));
        }
    }
    // Dodaje krawędź trasy (linia ciągła) z podanym tekstem (dwie linie)
    private void dodajKrawedzTrasy(Trasa t, List<String> linie) {
        generator.dodajKrawedz(t.getPoczatek().getId(), t.getKoniec().getId(), new StylKrawedzi(StylLinii.CIAGLA), linie);
    }
    // Dodaje krawędź wyciągu (linia przerywana) z podanym tekstem (dwie linie)
    private void dodajKrawedzWyciagu(Wyciag w, List<String> linie) {
        generator.dodajKrawedz(w.getPoczatek().getId(), w.getKoniec().getId(), new StylKrawedzi(StylLinii.PRZERYWANA), linie);
    }
    // Dodaje krawędź trasy z pojedynczym (być może długim) tekstem - generator sam podzieli go na linie i przytnie
    private void dodajKrawedzTrasy(Trasa t, String tekst) {
        generator.dodajKrawedz(t.getPoczatek().getId(), t.getKoniec().getId(), new StylKrawedzi(StylLinii.CIAGLA), tekst);
    }
    // Dodaje krawędź wyciągu z pojedynczym (być może długim) tekstem - generator sam podzieli go na linie i przytnie
    private void dodajKrawedzWyciagu(Wyciag w, String tekst) {
        generator.dodajKrawedz(w.getPoczatek().getId(), w.getKoniec().getId(), new StylKrawedzi(StylLinii.PRZERYWANA), tekst);
    }
    // Buduje pomocniczą listę dwóch linii tekstu krawędzi
    private List<String> dwieLinie(String pierwsza, String druga) {
        List<String> linie = new ArrayList<>();
        linie.add(pierwsza);
        linie.add(druga);
        return linie;
    }
    // Buduje tekst krawędzi dla mapki historii: np. "t3(2): 3,10" gdy były przejazdy, a "t5(0)" gdy nie było żadnego
    private String tekstHistorii(String prefiks, int id, List<Integer> numery) {
        if (numery == null || numery.isEmpty()) {
            return prefiks + id + "(0)";
        }
        StringBuilder budowniczy = new StringBuilder();
        budowniczy.append(prefiks).append(id).append("(").append(numery.size()).append("): ");
        for (int i = 0; i < numery.size(); i++) {
            if (i > 0) {
                budowniczy.append(",");
            }
            budowniczy.append(numery.get(i));
        }
        return budowniczy.toString();
    }
}
