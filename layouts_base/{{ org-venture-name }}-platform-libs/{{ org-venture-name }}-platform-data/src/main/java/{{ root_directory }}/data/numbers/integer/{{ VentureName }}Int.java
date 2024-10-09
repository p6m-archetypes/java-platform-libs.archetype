package {{ root_package }}.data.numbers.integer;

public abstract class {{ VentureName }}Int {

  final int value;

  protected {{ VentureName }}Int(int i) {
    this.value = i;
  }

  public int value() {
    return value;
  }
}
