package model.form;

public class Behaviour {

    public String name;
    public boolean walkOnFire;
    public boolean pushSbOver;
    public boolean helpSbUp;
    public boolean quenchSb;
    public boolean warn;

 /*   public String name;
    public boolean walkOnFire; //chodzić w ogniu
    public boolean pushSbOver; //popchnąc kogoś
    public boolean helpSbUp; //pomóc komuś wstać
    public boolean quenchSb; //ugasić kogoś
    public boolean warn;  //ostrzegać*/

    public Behaviour (String name,
                      boolean walkOnFire,
                      boolean pushSbOver,
                      boolean helpSbUp,
                      boolean quenchSb,
                      boolean warn) {
        this.walkOnFire = walkOnFire;
        this.name = name;
        this.pushSbOver = pushSbOver;
        this.helpSbUp = helpSbUp;
        this.quenchSb = quenchSb;
        this.warn = warn;
    }

    public String getName() {
        return this.name;
    }
}
