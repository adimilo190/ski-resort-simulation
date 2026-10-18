package sportowcy;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
import elementy_symulacji.Wyciag;
import elementy_symulacji.Krawedz;
import symulacja.SilnikSymulacji;
// Sportowiec typu Lokalny (jak w części 1): na każdym węźle podejmuje decyzję lokalnie i może zmienić zdanie przed zjazdem - nie układa sztywnego planu
public class SportowiecLokalny extends Sportowiec {
    public SportowiecLokalny(int id, int poziom, double spontanicznosc, double wspolczynnikZnudzenia,
                             double wagaTrudnosci, double wagaWyrownania, double wagaZnudzenia,
                             boolean sledzony, Wezel start, Osrodek osrodek) {
        super(id, poziom, spontanicznosc, wspolczynnikZnudzenia, wagaTrudnosci, wagaWyrownania, wagaZnudzenia, sledzony, start, osrodek);
    }
    // Po pojawieniu się na stoku oraz po każdym przejeździe Lokalny po prostu podejmuje kolejną decyzję
    @Override
    public void kontynuuj(int czas, SilnikSymulacji silnik) {
        decyzja(czas, silnik);
    }
    // Przemyślana decyzja: oblicza atrakcyjność każdej trasy i wyciągu z aktualnego węzła i wybiera opcję o najwyższej atrakcyjności
    @Override
    protected void decyzjaPrzemyslana(int czas, SilnikSymulacji silnik) {
        double maxAtrakcyjnosc = 0.0;
        Krawedz najlepszaOpcja = null;
        for (int i = 0; i < aktualnyWezel.getLiczbaTras(); i++) {
            Trasa t = aktualnyWezel.getTrasa(i);
            double atrakcyjnoscTrasy = t.obliczAtrakcyjnosc(this);
            if (atrakcyjnoscTrasy >= maxAtrakcyjnosc) {
                maxAtrakcyjnosc = atrakcyjnoscTrasy;
                najlepszaOpcja = t;
            }
        }
        for (int i = 0; i < aktualnyWezel.getLiczbaWyciagow(); i++) {
            Wyciag w = aktualnyWezel.getWyciag(i);
            double atrakcyjnoscWyciagu = w.obliczAtrakcyjnosc(this);
            if (atrakcyjnoscWyciagu >= maxAtrakcyjnosc) {
                maxAtrakcyjnosc = atrakcyjnoscWyciagu;
                najlepszaOpcja = w;
            }
        }
        // Po znalezieniu najlepszej opcji rozpoczynamy zjazd trasą lub ustawiamy się w kolejce do wyciągu
        if (najlepszaOpcja instanceof Trasa trasa) {
            raportSledzenia(czas, "Sportowiec" + id + " przemyślanie wybrał trasę nr " + trasa.getId());
            rozpocznijZjazd(trasa, czas, silnik);
        } else {
            Wyciag wyciag = (Wyciag) najlepszaOpcja;
            raportSledzenia(czas, "Sportowiec" + id + " przemyślanie wybrał wyciąg nr " + wyciag.getId());
            rozpocznijWjazd(wyciag, czas, silnik);
        }
    }
}
