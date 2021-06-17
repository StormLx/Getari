package fr.inrae.agroclim.getari.memento;

public enum HistorySingleton {
    /**
     * instance.
     */
    INSTANCE;

    /**
     * infos.
     */
    private History history;

    HistorySingleton() {
        this.history = new History();
    }

    /**
     * Singleton.
     *
     * @return instance
     */
    public static HistorySingleton getInstance() {
        return INSTANCE;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(final History h) {
        this.history = h;
    }

}
