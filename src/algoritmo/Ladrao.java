package algoritmo;

import java.util.Arrays;

public class Ladrao extends ProgramaLadrao {

	// O terreno "memorizado" pelo ladrão.
	private int[][] knownField = new int[30][30];

	// TODO: DOC AQUI
	private int[][] traveledPath = new int[30][30];

	public Ladrao() {
		// Inicializa a matriz multidimensional, a qual representa o terrenho "memorizado".
		for (int[] field : knownField) {
			// Inicializa todas as informações do terreno como "desconhecido".
			Arrays.fill(field, -2);
		}

		// TODO: MUDAR DOC
		// O caminho percorrido pelo ladrão.
		for (int[] path : traveledPath) {
			// Indica a quantidade de vezes que visitou tal terreno.
			Arrays.fill(path, 0);
		}
	}

	@Override
	public int acao() {
		this.memorizeLand(this.sensor.getVisaoIdentificacao(), this.sensor.getPosicao());
		// TODO: ARRUMAR ESSA MERDA.
		// return this.decidePath(this.sensor.getPosicao());
		return 2;
	}

	private int decidePath(java.awt.Point currentPosition) {
		// TODO: REFIZ O MEMORIZAR, AGORA É ARRUMAR ESSA MERDA.
		// // A posição atual do ladrão.
		// Integer positionX = (int) currentPosition.getX();
		// Integer positionY = (int) currentPosition.getY();

		// // Aumenta a quantidade de visitas, do terreno atual.
		// this.traveledPath[positionY][positionX]++;

		// // Contém a informação dos terrenos adjacentes ao do ladrão.
		// ArrayList<Integer> adjacentPaths = new ArrayList<Integer>();

		// // Contém a quantidade de vezes que os terrenos, adjacentes ao do ladrão,
		// foram
		// // visitados.
		// // Juntamente com o valor referente à sua direção.
		// Map<Integer, Integer> adjacentLandVisitCount = new HashMap<>();

		// // Verifica se existe terrenos adjacentes ao ladrão e adiciona-os às listas.
		// // Cima.
		// if ((positionY - 1) >= 0) {
		// adjacentPaths.add(this.knownField[positionY - 1][positionX]);
		// adjacentLandVisitCount.put(1, this.traveledPath[positionY - 1][positionX]);
		// }
		// // Baixo.
		// if ((positionY + 1) <= 29) {
		// adjacentPaths.add(this.knownField[positionY + 1][positionX]);
		// adjacentLandVisitCount.put(2, this.traveledPath[positionY + 1][positionX]);
		// }
		// // Direita.
		// if ((positionX + 1) <= 29) {
		// adjacentPaths.add(this.knownField[positionY][positionX + 1]);
		// adjacentLandVisitCount.put(3, this.traveledPath[positionY][positionX + 1]);
		// }
		// // Esquerda.
		// if ((positionX - 1) >= 0) {
		// adjacentPaths.add(this.knownField[positionY][positionX - 1]);
		// adjacentLandVisitCount.put(4, this.traveledPath[positionY][positionX - 1]);
		// }

		// // Ignora os terrenos "impossíveis" de visitar.
		// ArrayList<Integer> nonVisitablePaths = new ArrayList<>(Arrays.asList(-2, -1,
		// 1, 3, 4, 5));
		// for (int i = 0; i < adjacentPaths.size(); i++) {
		// if (nonVisitablePaths.contains(adjacentPaths.get(i))) {
		// adjacentLandVisitCount.remove(i + 1);
		// }
		// }

		// // Pega o terreno com a menor quantia de visitas.
		// Integer lessVisitedLand = Collections.min(adjacentLandVisitCount.values());

		// // Cria uma lista somente com os terrenos com as menores quantias de visitas.
		// ArrayList<Integer> adjacentLeastVisitedLands = new ArrayList<Integer>();
		// for (Map.Entry<Integer, Integer> entry : adjacentLandVisitCount.entrySet()) {
		// int landDirection = entry.getKey(), landVisitCount = entry.getValue();

		// if (landVisitCount == lessVisitedLand) {
		// adjacentLeastVisitedLands.add(landDirection);
		// }
		// }

		// // Escolhe um terreno e visita-o.
		// Random landSelector = new Random();
		// return
		// adjacentLeastVisitedLands.get(landSelector.nextInt(adjacentLeastVisitedLands.size()));
	}

	/**
	 * Armazena o último estado dos terrenos visíveis, pelo ladrão.
	 * @param gridView A visão do ladrão.
	 * @param currentPosition A posição atual do ladrão.
	 */
	private void memorizeLand(int[] gridView, java.awt.Point currentPosition) {
		// A posição atual do ladrão.
		Integer positionX = (int) currentPosition.getX();
		Integer positionY = (int) currentPosition.getY();

		// O índice do elemento correspondente ao terreno visível.
		Integer gridIndex = 0;
		// Itera sobre cada terreno visível pelo ladrão.
		for (int y = positionY - 2; y <= positionY + 2; y++) {
			for (int x = positionX - 2; x <= positionX + 2; x++) {
				// Ignora a posição do ladrão.
				if (!(x == positionX && y == positionY)) {
					// Verifica se o terreno é válido.
					if (x >= 0 && x <= 29) {
						if (y >= 0 && y <= 29) {
							// "Memoriza" o terreno.
							this.knownField[y][x] = gridView[gridIndex];
						}
					}
					// Passa para o próximo terreno visível.
					gridIndex++;
				}
			}
		}
	}
}