package symulacja;
// Klasa abstrakcyjna reprezentująca zdarzenie w symulacji, które ma określony czas wystąpienia i unikalny identyfikator wstawienia do kolejki
public abstract class Zdarzenie implements Comparable<Zdarzenie> {
    private static long licznikGlobalny = 0;
    private final int czasWystapienia;
    private final long idWstawienia;

    public Zdarzenie(int czasWystapienia) {
        this.czasWystapienia = czasWystapienia;
        this.idWstawienia = licznikGlobalny++;
    }
    // Abstrakcyjna metoda wykonaj, która będzie implementowana przez konkretne typy zdarzeń, aby określić, co się dzieje, gdy zdarzenie jest przetwarzane przez silnik symulacji
    public abstract void wykonaj(SilnikSymulacji silnik);
    // Metoda compareTo porównuje zdarzenia najpierw po czasie wystąpienia, a następnie po identyfikatorze wstawienia
    @Override
    public int compareTo(Zdarzenie inne) {
        if (this.czasWystapienia != inne.getCzasWystapienia()) {
            return Integer.compare(this.czasWystapienia, inne.getCzasWystapienia());
        }
        return Long.compare(this.idWstawienia, inne.getIdWstawienia());
    }
    // Gettery do pobierania czasu wystąpienia i identyfikatora wstawienia
    public int getCzasWystapienia() { return czasWystapienia; }
    private long getIdWstawienia() { return idWstawienia; }
}