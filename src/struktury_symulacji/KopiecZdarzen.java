package struktury_symulacji;

import symulacja.Zdarzenie;
// Klasa implementująca kopiec do zarządzania zdarzeniami w silniku symulacji
// Działa w czasie O(log n) dla operacji wstawiania i pobierania zdarzeń
public class KopiecZdarzen implements KolejkaZdarzen {
    private Zdarzenie[] kopiec;
    private int rozmiar;

    public KopiecZdarzen(int poczatkowaPojemnosc) {
        kopiec = new Zdarzenie[poczatkowaPojemnosc];
        rozmiar = 0;
    }

    private void podwojRozmiar() {
        Zdarzenie[] nowaTablica = new Zdarzenie[kopiec.length * 2];
        System.arraycopy(kopiec, 0, nowaTablica, 0, kopiec.length);
        kopiec = nowaTablica;
    }

    @Override
    public void wstaw(Zdarzenie z) {
        if (rozmiar >= kopiec.length) {
            podwojRozmiar();
        }
        kopiec[rozmiar] = z;
        ustawNowyElement(rozmiar);
        rozmiar++;
    }

    @Override
    public Zdarzenie pobierz() {
        if (czyPusta()) throw new IllegalStateException("Kolejka zdarzen jest pusta");
        Zdarzenie root = kopiec[0];
        kopiec[0] = kopiec[rozmiar - 1];
        kopiec[rozmiar - 1] = null;
        rozmiar--;
        if (rozmiar > 0) ustawNoweMinimum(0);
        return root;
    }

    @Override
    public boolean czyPusta() {
        return rozmiar == 0;
    }
    // Metoda do ustawiania nowego elementu w kopcu, aby zachować właściwość kopca po dodaniu nowego zdarzenia
    private void ustawNowyElement(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (kopiec[index].compareTo(kopiec[parentIndex]) < 0) {
                zamien(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }
    // Metoda do ustawiania nowego minimum w kopcu po usunięciu korzenia, aby zachować właściwość kopca
    private void ustawNoweMinimum(int index) {
        while (2 * index + 1 < rozmiar) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = leftChild;
            if (rightChild < rozmiar && kopiec[rightChild].compareTo(kopiec[leftChild]) < 0) {
                smallest = rightChild;
            }
            if (kopiec[index].compareTo(kopiec[smallest]) <= 0) break;
            zamien(index, smallest);
            index = smallest;
        }
    }

    private void zamien(int i, int j) {
        Zdarzenie temp = kopiec[i];
        kopiec[i] = kopiec[j];
        kopiec[j] = temp;
    }
}