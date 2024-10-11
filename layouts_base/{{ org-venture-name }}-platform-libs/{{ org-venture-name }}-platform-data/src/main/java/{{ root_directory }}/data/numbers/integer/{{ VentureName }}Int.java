package {{ root_package }}.data.numbers.integer;

public abstract class {{ SolutionName }}Int {

  final int value;

  protected {{ SolutionName }}Int(int i) {
    this.value = i;
  }

  public int value() {
    return value;
  }
}
