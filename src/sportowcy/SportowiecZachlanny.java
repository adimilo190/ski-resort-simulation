package sportowcy;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
// Sportowiec typu Zachłanny: jako cel wybiera najatrakcyjniejszą trasę spośród wszystkich tras w całym ośrodku i układa plan przejazdu do niej
public class SportowiecZachlanny extends SportowiecZPlanem {
    public SportowiecZachlanny(int id, int poziom, double spontanicznosc, double wspolczynnikZnudzenia,
                               double wagaTrudnosci, double wagaWyrownania, double wagaZnudzenia,
                               boolean sledzony, Wezel start, Osrodek osrodek) {
        super(id, poziom, spontanicznosc, wspolczynnikZnudzenia, wagaTrudnosci, wagaWyrownania, wagaZnudzenia, sledzony, start, osrodek);
    }
    // Cel zachłannego to trasa o najwyższej atrakcyjności w całym ośrodku (remisy rozstrzygamy dowolnie - bierzemy pierwszą napotkaną)
    @Override
    protected Trasa wybierzCelowaTrase() {
        Trasa najlepsza = null;
        double maxAtrakcyjnosc = -1.0;
        for (int i = 0; i < osrodek.getLiczbaTras(); i++) {
            Trasa t = osrodek.getTrasa(i);
            double atrakcyjnosc = t.obliczAtrakcyjnosc(this);
            if (atrakcyjnosc > maxAtrakcyjnosc) {
                maxAtrakcyjnosc = atrakcyjnosc;
                najlepsza = t;
            }
        }
        return najlepsza;
    }
}
