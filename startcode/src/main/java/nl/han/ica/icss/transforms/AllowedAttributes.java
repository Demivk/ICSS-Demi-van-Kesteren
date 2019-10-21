package nl.han.ica.icss.transforms;

public enum AllowedAttributes {
    BACKGROUNDCOLOR("background-color"),
    COLOR("color"),
    WIDTH("width"),
    HEIGHT("height");

    public final String attribute;

    private AllowedAttributes(String attribute) {
        this.attribute = attribute;
    }
}
