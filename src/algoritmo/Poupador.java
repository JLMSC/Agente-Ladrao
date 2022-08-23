package algoritmo;

import java.util.Arrays;

public class Poupador extends ProgramaPoupador {

	@Override
	public int acao() {
		return (int) (Math.random() * 5);
	}
	
}