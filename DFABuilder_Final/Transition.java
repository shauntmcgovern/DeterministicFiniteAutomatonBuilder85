public class Transition {
   State u, v;
   char sym;
   public Transition(State u, State v, char sym) {
    this.u = u;
    this.v = v;
    this.sym = sym;
   }
}
