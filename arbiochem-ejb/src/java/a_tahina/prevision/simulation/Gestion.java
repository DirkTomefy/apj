package a_tahina.prevision.simulation;

public enum Gestion {

    FIFO,
    LIFO;

    int indice(int rang, int taille) {
        return this == FIFO ? rang : taille - 1 - rang;
    }
}
