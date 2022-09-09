package algoritmo;

public class Poupador extends ProgramaPoupador {

	@Override
	public int acao() {
		return (int) (Math.random() * 5);
	}
	
}